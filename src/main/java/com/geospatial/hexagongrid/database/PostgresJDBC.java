package com.geospatial.hexagongrid.database;

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

import com.geospatial.hexagongrid.hexagon.Coordinates;
import com.geospatial.hexagongrid.hexagon.Hexagon;
import com.geospatial.hexagongrid.index.CubeCoordinatesIndex;
import com.geospatial.hexagongrid.tessellation.Boundary;
import com.geospatial.hexagongrid.tessellation.CornerEdgeTessellation;
import com.geospatial.hexagongrid.utils.JVMUtils;
import com.google.gson.JsonObject;

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
        private final Properties properties;

        public PostgresJDBC(Builder builder) {
                this.host = builder.host;
                this.port = builder.port;
                this.database = builder.database;
                this.properties = builder.properties;
                this.pgjdbcUrl = generateJDBCUrl();
        }

        /* Public methods */
        public final Connection getConnection() {
                Connection connection = null;
                try {
                        // https://stackoverflow.com/questions/62426544/no-suitable-driver-found-for-jdbcpostgresql-but-i-have-install-driver
                        try {
                                Class.forName("org.postgresql.Driver");
                        } catch (ClassNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                        connection = DriverManager.getConnection(pgjdbcUrl, properties);
                        System.out.println("Connected to the PostgreSQL server as user: " + connection.getMetaData().getUserName());

                } catch (SQLException e) {
                        printSQLException(e);
                }

                return connection;
        }

        public final void fetchTable(String tableName, int rowsLimit) {
                final String sql = """
                        SELECT * FROM %s
                        LIMIT %s
                """;

                final String query = String.format(sql, tableName, rowsLimit);
                try (final Connection conn = getConnection();
                                final Statement stmt = conn.createStatement();
                                final ResultSet rs = stmt.executeQuery(query)) {

                        final ResultSetMetaData rsMetadata = rs.getMetaData();
                        final int columnCount = rsMetadata.getColumnCount();

                        // Iterate through the data in the result set and display it.
                        System.out.println("--- Query results");
                        while (rs.next()) {
                                // Print one row
                                for (int i = 1; i <= columnCount; i++) {
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

                                System.out.println("--------------------------------");
                        }

                } catch (SQLException e) {
                        printSQLException(e);
                }
        }

        public final JsonObject createTessellationTable(String tableName) {
                final JsonObject response = new JsonObject();

                try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                        String createTableSql = String.format("""
                                                CREATE TABLE IF NOT EXISTS %s (
                                                        ccid_q          integer                         NOT NULL,
                                                        ccid_r          integer                         NOT NULL,
                                                        ccid_s          integer                         NOT NULL,
                                                        circumradius    float8                          NOT NULL,
                                                        centroid        geometry(POINT, 4326)           NOT NULL,
                                                        geometry        geometry(POLYGON, 4326)         NOT NULL
                                                );
                                        """,
                                        tableName);

                        int statusCode = stmt.executeUpdate(createTableSql);
                        if (statusCode == 0) {
                                response.addProperty("status", "SUCCESS");
                                System.out.println("Executed createTessellationTable successfully.");
                        } else {
                                response.addProperty("status", "FAILED");
                                System.out.println("Failed to execute createTessellationTable.");
                        }

                } catch (SQLException e) {
                        response.addProperty("error", e.toString());
                        printSQLException(e);
                }

                return response;
        }

        public final JsonObject batchInsertTessellation(String tableName, CornerEdgeTessellation tessellation) {
                // JDBC batch configurations
                final int BATCH_SIZE_LIMIT = 5000;

                // Get hexagons
                final List<Hexagon> hexagons = tessellation.getGisHexagons();

                // Prepare SQL
                final String insertTessellationSql = String.format("""
                                INSERT INTO %s (ccid_q, ccid_r, ccid_s, circumradius, centroid, geometry)
                                VALUES (?,
                                        ?,
                                        ?,
                                        ?,
                                        ST_SetSRID(ST_MakePoint(?, ?), 4326),
                                        ST_SetSRID(
                                                ST_MakePolygon(ST_MakeLine(ARRAY[
                                                        ST_MakePoint(?, ?),
                                                        ST_MakePoint(?, ?),
                                                        ST_MakePoint(?, ?),
                                                        ST_MakePoint(?, ?),
                                                        ST_MakePoint(?, ?),
                                                        ST_MakePoint(?, ?),
                                                        ST_MakePoint(?, ?)])
                                        ), 4326));
                                """,
                                tableName);

                // Prepare dynamic queries to batch insert Hexagons into PostGIS
                JsonObject response = new JsonObject();
                JsonObject message = new JsonObject();

                try (Connection conn = getConnection();
                                PreparedStatement preparedStmt = conn
                                                .prepareStatement(insertTessellationSql)) {
                        // Set autocommit off
                        conn.setAutoCommit(false);
                        
                        // Start time
                        System.out.println("--- Batch execution begin..");
                        final long startTime = System.currentTimeMillis();
                        
                        // Loop through hexagons and populate prepared statement
                        int batchCount = 0;
                        int batchExecutionCount = 0;
                        for (Hexagon hexagon : hexagons) {
                                CubeCoordinatesIndex cci = hexagon.getCCI();
                                preparedStmt.setInt(1, cci.getQ());
                                preparedStmt.setInt(2, cci.getR());
                                preparedStmt.setInt(3, cci.getS());

                                double circumradius = hexagon.getCircumradius();
                                preparedStmt.setDouble(4, circumradius);

                                Coordinates centroid = hexagon.getCentroid();
                                preparedStmt.setDouble(5, centroid.getLongitude());
                                preparedStmt.setDouble(6, centroid.getLatitude());

                                List<Coordinates> gisVertices = hexagon.getGisVertices();
                                preparedStmt.setDouble(7, gisVertices.get(0).getLongitude());
                                preparedStmt.setDouble(8, gisVertices.get(0).getLatitude());

                                preparedStmt.setDouble(9, gisVertices.get(1).getLongitude());
                                preparedStmt.setDouble(10, gisVertices.get(1).getLatitude());

                                preparedStmt.setDouble(11, gisVertices.get(2).getLongitude());
                                preparedStmt.setDouble(12, gisVertices.get(2).getLatitude());

                                preparedStmt.setDouble(13, gisVertices.get(3).getLongitude());
                                preparedStmt.setDouble(14, gisVertices.get(3).getLatitude());

                                preparedStmt.setDouble(15, gisVertices.get(4).getLongitude());
                                preparedStmt.setDouble(16, gisVertices.get(4).getLatitude());

                                preparedStmt.setDouble(17, gisVertices.get(5).getLongitude());
                                preparedStmt.setDouble(18, gisVertices.get(5).getLatitude());

                                preparedStmt.setDouble(19, gisVertices.get(6).getLongitude());
                                preparedStmt.setDouble(20, gisVertices.get(6).getLatitude());

                                // Add statement into batch
                                preparedStmt.addBatch();
                                batchCount++;

                                // Commit to DB every BATCH_SIZE_LIMIT (default: 1000)
                                if (batchCount % BATCH_SIZE_LIMIT == 0) {
                                        try {
                                                preparedStmt.executeBatch();
                                                conn.commit();
                                                batchExecutionCount++;
                                                System.out.println("- Batch " + batchExecutionCount + "th commited.");
                                        } catch (SQLException e) {
                                                conn.rollback();
                                                printSQLException(e);
                                        }
                                }
                        }

                        /*
                         * Set auto-commit back to normal and execute left over batches (batch amount <
                         * JDBC_BATCH_LIMIT)
                         */
                        conn.setAutoCommit(true);
                        preparedStmt.executeBatch();
                        batchExecutionCount++;
                        
                        // End time when finished batch inserts
                        System.out.println("- Final batch " + batchExecutionCount + "th executed.");
                        final long endTime = System.currentTimeMillis();

                        // Calculate elapsed time of batch execution
                        final double elapsedMillisecs = endTime - startTime;
                        final double elapsedSeconds = elapsedMillisecs / 1000;

                        /*
                         * Prepare batchInsertTessellation response
                         */
                        response.addProperty("status", "SUCCESS");
                        response.add("message", message);
                        
                        message.addProperty("tableName", tableName);
                        message.addProperty("totalHexagons", hexagons.size());
                        message.addProperty("totalBatchExecutions", batchExecutionCount);
                        message.addProperty("elapsedSeconds", elapsedSeconds);
                        message.addProperty("rowsPerBatch", BATCH_SIZE_LIMIT);
                        message.addProperty("rowsInserted", batchCount);

                } catch (BatchUpdateException batchUpdateException) {
                        response.addProperty("status", "FAILED");
                        response.addProperty("error", batchUpdateException.toString());
                        printBatchUpdateException(batchUpdateException);
                } catch (SQLException sqlException) {
                        response.addProperty("status", "FAILED");
                        response.addProperty("error", sqlException.toString());
                        printSQLException(sqlException);
                }

                return response;
        }

        public final JsonObject addPrimaryKeyIfNotExists(String tableName) {
                JsonObject response = new JsonObject();

                final String checkPrimaryKeySql = String.format(
                        """
                        SELECT constraint_name from information_schema.table_constraints
                        WHERE table_name = '%s'
                        AND constraint_type = 'PRIMARY KEY'
                        """,
                        tableName);

                final String addPrimaryKeySql = String.format(
                        """
                        ALTER TABLE %s
                        ADD PRIMARY KEY (ccid_q, ccid_r, ccid_s)
                        """,
                        tableName);

                try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
                        ResultSet rs = statement.executeQuery(checkPrimaryKeySql);

                        boolean hasPrimaryKey = rs.next(); // Return `false` if there is no more row (means no PK)
                        if (hasPrimaryKey) {
                                // Query PRIMARY KEY name
                                String constraintName = rs.getString("constraint_name");
                                System.out.println(String.format(
                                                "PRIMARY KEY '%s' already exists for table '%s'.",
                                                constraintName,
                                                tableName));
                        } else {
                                statement.executeUpdate(addPrimaryKeySql);
                                rs = statement.executeQuery(checkPrimaryKeySql);

                                boolean hasNextRow = rs.next();
                                if (hasNextRow) {
                                        response.addProperty("status", "SUCCESS");
                                        response.addProperty("message", String.format(
                                                        "PRIMARY KEY '%s' added to table '%s'.",
                                                        rs.getString("constraint_name"),
                                                        tableName));
                                } else {
                                        response.addProperty("status", "FAILED");
                                        response.addProperty("message", String.format(
                                                        "Failed to add PRIMARY KEY to table '%s'.",
                                                        tableName));
                                }
                        }

                } catch (SQLException e) {
                        printSQLException(e);
                }

                return response;
        }

        public static void printBatchUpdateException(BatchUpdateException b) {
                System.err.println("--- BatchUpdateException");
                System.err.println("SQLState: \t" + b.getSQLState());
                System.err.println("Message: \t" + b.getMessage());
                System.err.println("Vendor: \t" + b.getErrorCode());

                System.err.println("Update counts: \t");
                int[] updateCounts = b.getUpdateCounts();
                for (int i = 0; i < updateCounts.length; i++) {
                        System.err.print(updateCounts[i] + "   ");
                }
        }

        /* Private methods */
        private final String generateJDBCUrl() {
                final StringBuilder urlBuilder = new StringBuilder().append(DBMS_URL);

                if (this.host == null) {
                        // No host
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
                private Properties properties = new Properties();

                private final Dotenv dotenv = Dotenv.load();

                public Builder() {
                }

                public final Builder host(String hostKey) {
                        this.host = dotenv.get(hostKey);
                        return this;
                }

                public final Builder port(int port) {
                        this.port = port;
                        return this;
                }

                public final Builder database(String databaseKey) {
                        this.database = dotenv.get(databaseKey);
                        return this;
                }

                public final Builder authentication(String usernameKey, String passwordKey) {
                        final String username = dotenv.get(usernameKey);
                        final String password = dotenv.get(passwordKey);

                        this.properties.setProperty("user", username);
                        this.properties.setProperty("password", password);

                        return this;
                }

                public final Builder reWriteBatchedInserts(boolean isEnabled) {
                        this.properties.setProperty("reWriteBatchedInserts", Boolean.toString(isEnabled));
                        return this;
                }

                public final PostgresJDBC build() {
                        return new PostgresJDBC(this);
                }
        }

        /* Test */
        public static void main(String[] args) {
                PostgresJDBC pg = new PostgresJDBC.Builder()
                                .host("POSTGRES_HOST")
                                .port(5432)
                                .database("POSTGRES_DATABASE")
                                .authentication("POSTGRES_USERNAME", "POSTGRES_PASSWORD")
                                .reWriteBatchedInserts(true)
                                .build();

                /*
                 * Vietnam - Nominatim OpenStreetMap
                 * - URL :
                 * https://nominatim.openstreetmap.org/ui/details.html?osmtype=R&osmid=49915
                 * - ID : R49915
                 */
                final Coordinates vn_min_coords_osm = new Coordinates(102, 8);
                final Coordinates vn_max_coords_osm = new Coordinates(109.9, 23.5);
                final Coordinates vn_centroid_osm = new Coordinates(107.9650855, 15.9266657);
                final Boundary vn_boundary_osm = new Boundary(vn_min_coords_osm, vn_max_coords_osm);

                /*
                 * Vietnam - spatial_db
                 * - database : spatial_db
                 * - table : vietnam_border
                 */
                final Coordinates vn_min_coords_internal = new Coordinates(102.14458466, 7.39143848);
                final Coordinates vn_max_coords_internal = new Coordinates(117.81734467, 23.39243698);
                final Coordinates vn_centroid_internal = new Coordinates(106.4063821609223, 16.57755915233502);
                final Boundary vn_boundary_internal = new Boundary(vn_min_coords_internal, vn_max_coords_internal);

                // Tessellation configurations
                final int circumradius = 1350;
                final Coordinates centroid = vn_centroid_internal;
                final Boundary boundary = vn_boundary_internal;

                // Don't modify this
                final Hexagon hexagon = new Hexagon(centroid, circumradius);
                final CornerEdgeTessellation tessellation = new CornerEdgeTessellation(hexagon);
                tessellation.tessellate(boundary);

                // Database table name formats
                final String TABLE_NAME_TEMPLATE = "%s_tessellation_%sm";

                // Database configurations
                System.out.println("\n------ Database configs ------");
                final String table_name = String.format(TABLE_NAME_TEMPLATE,
                                "vietnam",
                                circumradius);
                System.out.println("Table name: " + table_name);

                // pg.createTessellationTable(table_name);
                // pg.batchInsertTessellation(table_name, tessellation);
                // pg.addPrimaryKeyIfNotExists(table_name);
                JVMUtils.printMemoryUsages("MB");

                // Test query
                pg.fetchTable(table_name, 5);
        }
}