package com.masterisehomes.geometryapi.database;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.index.CubeCoordinatesIndex;
import com.masterisehomes.geometryapi.tessellation.AxialClockwiseTessellation;
import com.masterisehomes.geometryapi.tessellation.Boundary;
import com.masterisehomes.geometryapi.utils.JVMUtils;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.ToString;

@ToString
public class PostgresJDBC {
        private static final String DBMS_URL = "jdbc:postgresql:";
        private final String pgjdbcUrl;

        @Getter
        private final String host;
        @Getter
        private final int port;
        @Getter
        private final String database;

        @ToString.Exclude
        private final Properties props;

        public PostgresJDBC(Builder builder) {
                this.host = builder.host;
                this.port = builder.port;
                this.database = builder.database;
                this.props = builder.props;
                this.pgjdbcUrl = generateJDBCUrl();
        }

        /* Public methods */
        public final Connection getConnection() {
                Connection connection = null;
                try {
                        connection = DriverManager.getConnection(this.pgjdbcUrl, this.props);
                        System.out.println("\n"
                                        + "Connected to the PostgreSQL server: "
                                        + connection.getMetaData().getUserName());
                } catch (SQLException e) {
                        printSQLException(e);
                }

                return connection;
        }

        public final void testQuery(String table, int limit) {
                final String SQL_TEMPLATE = new StringBuilder()
                                .append("SELECT * FROM %s\n")
                                .append("LIMIT %s")
                                .toString();
                final String SQL = String.format(SQL_TEMPLATE, table, limit);

                try (final Connection connection = getConnection();
                                final Statement statement = connection.createStatement();
                                final ResultSet rs = statement.executeQuery(SQL)) {

                        final ResultSetMetaData rsMetadata = rs.getMetaData();
                        final int columnsCount = rsMetadata.getColumnCount();

                        System.out.println("--- Query results");

                        // Iterate through the data in the result set and display it.
                        while (rs.next()) {
                                // Print one row
                                for (int i = 1; i <= columnsCount; i++) {
                                        String tabs;
                                        if (rsMetadata.getColumnName(i).length() < 8) {
                                                tabs = "\t\t\t";
                                        } else {
                                                tabs = "\t\t";
                                        }

                                        System.out.print(String.format("(%s) ", rsMetadata.getColumnTypeName(i)));
                                        System.out.print(rsMetadata.getColumnName(i) + tabs + ": ");
                                        System.out.print(rs.getString(i) + "\n");
                                }

                                System.out.println("---------------------------------");
                        }
                } catch (SQLException e) {
                        printSQLException(e);
                }
        }

        public final void createGeometryTable(String tableName) {
                final String CREATE_TABLE_SQL = new StringBuilder()
                                .append("CREATE TABLE IF NOT EXISTS " + tableName + " (" + "\n")
                                .append("	ccid_q		integer 		NOT NULL," + "\n")
                                .append("	ccid_r		integer 		NOT NULL," + "\n")
                                .append("	ccid_s		integer 		NOT NULL," + "\n")
                                .append("	circumradius 	float8 			NOT NULL," + "\n")
                                .append("	centroid	geometry(POINT,4326)    NOT NULL," + "\n")
                                .append("	geometry  	geometry(POLYGON,4326) 	NOT NULL," + "\n")
                                .append("	PRIMARY KEY(ccid_q, ccid_r, ccid_s)" + "\n")
                                .append(");" + " ")
                                .toString();

                try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
                        statement.executeUpdate(CREATE_TABLE_SQL);
                        System.out.println("Created geometry table successfully with query:");
                        System.out.println(CREATE_TABLE_SQL);
                } catch (SQLException e) {
                        printSQLException(e);
                }
        }

        public final void batchInsertByTessellation(String table, AxialClockwiseTessellation tessellation) {

                /* Tessellation data */
                final List<Hexagon> gisHexagons = tessellation.getGisHexagons();
                final int TOTAL_HEXAGONS = gisHexagons.size();

                final String INSERT_SQL = "INSERT INTO " + table
                                + " (ccid_q, ccid_r, ccid_s, circumradius, centroid, geometry) "
                                + String.format("VALUES (%s, %s, %s, %s, %s, %s);",
                                                "?",
                                                "?",
                                                "?",
                                                "?",
                                                "ST_SetSRID(ST_MakePoint(?, ?), 4326)",
                                                "ST_SetSRID(ST_MakePolygon(ST_MakeLine(ARRAY["
                                                                + "ST_MakePoint(?, ?), "
                                                                + "ST_MakePoint(?, ?), "
                                                                + "ST_MakePoint(?, ?), "
                                                                + "ST_MakePoint(?, ?), "
                                                                + "ST_MakePoint(?, ?), "
                                                                + "ST_MakePoint(?, ?), "
                                                                + "ST_MakePoint(?, ?)])), 4326)");

                try (Connection connection = getConnection();
                                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {

                        /* Set autocommit off */
                        connection.setAutoCommit(false);

                        /* JDBC Batching configurations */
                        int batchCount = 0;
                        int batchExecutionCount = 0;
                        final int JDBC_BATCH_SIZE = 1000;

                        /* Start time of batch execution */
                        System.out.println("--- Batch execution begin..");
                        long startTime = System.currentTimeMillis();

                        for (Hexagon hexagon : gisHexagons) {
                                CubeCoordinatesIndex cci = hexagon.getCCI();
                                preparedStatement.setInt(1, cci.getQ());
                                preparedStatement.setInt(2, cci.getR());
                                preparedStatement.setInt(3, cci.getS());

                                double circumradius = hexagon.getCircumradius();
                                preparedStatement.setDouble(4, circumradius);

                                Coordinates centroid = hexagon.getCentroid();
                                preparedStatement.setDouble(5, centroid.getLongitude());
                                preparedStatement.setDouble(6, centroid.getLatitude());

                                List<Coordinates> gisVertices = hexagon.getGisVertices();
                                preparedStatement.setDouble(7, gisVertices.get(0).getLongitude());
                                preparedStatement.setDouble(8, gisVertices.get(0).getLatitude());

                                preparedStatement.setDouble(9, gisVertices.get(1).getLongitude());
                                preparedStatement.setDouble(10, gisVertices.get(1).getLatitude());

                                preparedStatement.setDouble(11, gisVertices.get(2).getLongitude());
                                preparedStatement.setDouble(12, gisVertices.get(2).getLatitude());

                                preparedStatement.setDouble(13, gisVertices.get(3).getLongitude());
                                preparedStatement.setDouble(14, gisVertices.get(3).getLatitude());

                                preparedStatement.setDouble(15, gisVertices.get(4).getLongitude());
                                preparedStatement.setDouble(16, gisVertices.get(4).getLatitude());

                                preparedStatement.setDouble(17, gisVertices.get(5).getLongitude());
                                preparedStatement.setDouble(18, gisVertices.get(5).getLatitude());

                                preparedStatement.setDouble(19, gisVertices.get(6).getLongitude());
                                preparedStatement.setDouble(20, gisVertices.get(6).getLatitude());

                                /* Add INSERT statement into JDBC Batch */
                                preparedStatement.addBatch();

                                /* Execute batch every JDBC_BATCH_LIMIT */
                                batchCount++; // update batchCount

                                /* Commit to DB every JDBC_BATCH_SIZE */
                                if (batchCount % JDBC_BATCH_SIZE == 0) {
                                        try {
                                                batchExecutionCount++;
                                                preparedStatement.executeBatch();
                                                connection.commit();
                                                System.out.println("- Batch " + batchExecutionCount + "th.");
                                        } catch (SQLException e) {
                                                connection.rollback();
                                                printSQLException(e);
                                        }
                                }
                        }

                        /*
                         * Set auto-commit back to normal and execute left over batches (batch amount <
                         * JDBC_BATCH_LIMIT)
                         */
                        connection.setAutoCommit(true);
                        preparedStatement.executeBatch();
                        batchExecutionCount++;
                        System.out.println("- Batch " + batchExecutionCount + "th.");

                        /* End time of batch executionn */
                        long endTime = System.currentTimeMillis();

                        /* Calculate elapsed time of batch execution */
                        double elapsedTimeMs = endTime - startTime;
                        double elapsedTimeSec = elapsedTimeMs / 1000;

                        System.out.println("\n------ Batch execution logs ------");
                        System.out.println("Batch execution time : " + elapsedTimeSec + " s");
                        System.out.println("Total batch inserts  : " + batchExecutionCount);
                        System.out.println("Hexagons per batch   : " + JDBC_BATCH_SIZE);
                        System.out.println("Hexagons inserted    : " + batchCount);
                        System.out.println("---");
                        System.out.println("Total Hexagons       : " + TOTAL_HEXAGONS);

                } catch (BatchUpdateException batchUpdateException) {
                        printBatchUpdateException(batchUpdateException);
                } catch (SQLException e) {
                        printSQLException(e);
                }
        }

        public static void printBatchUpdateException(BatchUpdateException b) {
                System.err.println("--- BatchUpdateException:");
                System.err.println("SQLState: \t" + b.getSQLState());
                System.err.println("Message: \t" + b.getMessage());
                System.err.println("Vendor: \t" + b.getErrorCode());
                System.err.print("Update counts: \t");

                int[] updateCounts = b.getUpdateCounts();
                for (int i = 0; i < updateCounts.length; i++) {
                        System.err.print(updateCounts[i] + "   ");
                }
        }

        /* Private methods */
        private final String generateJDBCUrl() {
                final StringBuilder urlBuilder = new StringBuilder().append(DBMS_URL);

                if (this.host == null) {
                        /* No host */
                        if (this.database == null) {
                                // jdbc:postgresql:/
                                urlBuilder.append("/");
                        } else {
                                // jdbc:postgresql:database
                                urlBuilder.append(this.database);
                        }
                } else {
                        // jdbc:postgresql://host
                        urlBuilder.append("//").append(this.host);

                        if (this.database == null) {
                                /* No port */
                                if (this.port == 0) {
                                        // jdbc:postgresql://host/
                                        urlBuilder.append("/");
                                } else {
                                        // jdbc:postgresql://host:port/
                                        urlBuilder.append(":").append(this.port)
                                                        .append("/");
                                }

                        } else {
                                /* There is port */
                                if (this.port == 0) {
                                        // jdbc:postgresql://host/database
                                        urlBuilder.append("/").append(this.database);
                                } else {
                                        // jdbc:postgresql://host:port/database
                                        urlBuilder.append(":").append(this.port)
                                                        .append("/").append(this.database);
                                }
                        }
                }

                return urlBuilder.toString();
        }

        public static void printSQLException(SQLException exception) {
                for (Throwable e : exception) {
                        if (e instanceof SQLException) {
                                e.printStackTrace(System.err);
                                System.err.println(System.lineSeparator());
                                System.err.println("SQLState:\t" + ((SQLException) e).getSQLState());
                                System.err.println("Error Code:\t" + ((SQLException) e).getErrorCode());
                                System.err.println("Message:\t" + e.getMessage());
                                Throwable t = exception.getCause();
                                while (t != null) {
                                        System.out.println("Cause:\t\t" + t);
                                        t = t.getCause();
                                }
                        }
                }
        }

        /* PostgresJDBC Builder */
        public static class Builder {
                private String host;
                private int port;
                private String database;
                private Properties props = new Properties();

                private final Dotenv dotenv = Dotenv.configure()
                                .directory(System.getProperty("user.dir"))
                                .filename(".env")
                                .load();

                public Builder() {
                }

                public final Builder host(String host) {
                        this.host = host;
                        return this;
                }

                public final Builder port(int port) {
                        this.port = port;
                        return this;
                }

                public final Builder database(String database) {
                        this.database = database;
                        return this;
                }

                public final Builder authentication(String usernameKey, String passwordKey) {
                        String username = dotenv.get(usernameKey);
                        String password = dotenv.get(passwordKey);

                        this.props.setProperty("user", username);
                        this.props.setProperty("password", password);

                        return this;
                }

                public final Builder reWriteBatchedInserts(boolean isEnabled) {
                        this.props.setProperty("reWriteBatchedInserts", Boolean.toString(isEnabled));
                        return this;
                }

                public final PostgresJDBC build() {
                        return new PostgresJDBC(this);
                }
        }

        /* Test */
        public static void main(String[] args) {
                PostgresJDBC pg = new PostgresJDBC.Builder()
                                .host("10.10.12.197")
                                .port(5432)
                                .database("spatial_db")
                                .authentication("POSTGRES_DWH_USERNAME", "POSTGRES_DWH_PASSWORD")
                                .reWriteBatchedInserts(true)
                                .build();

                final Coordinates origin = new Coordinates(106, 15);
                // Coordinates origin = new Coordinates(109.466667, 23.383333);
                // final Hexagon hexagon = new Hexagon(origin, 250);
                final Hexagon hexagon_750 = new Hexagon(origin, 750);

                /* Something is wrong with this Boundary */
                final Boundary boundary = new Boundary(
                                new Coordinates(102.133333, 8.033333),
                                new Coordinates(109.466667, 23.383333)
				);
                /* Still missing some wards at the top, check missing_wards.csv */
                final Boundary oct_17_boundary = new Boundary(
                                new Coordinates(102.050278, 23.583612),
                                new Coordinates(109.666945, 8)
				);

                /*
                 * *** DANGEROUS ***
                 * 
                 * Tessellation and write to DB..
                 */
                final AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon_750);
                tessellation.tessellate(oct_17_boundary);

                // final String vietnam_hexagon_250m = "vietnam_hexagon_250m";
                final String vietnam_hexagon_750m = "vietnam_hexagon_750m";

		final String table_name = vietnam_hexagon_750m;
                // pg.createGeometryTable(table_name);
                // pg.batchInsertByTessellation(table_name, tessellation);
                // JVMUtils.printMemories("MB");

                pg.testQuery(vietnam_hexagon_750m, 5);
        }
}