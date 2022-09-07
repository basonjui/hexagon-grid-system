package com.masterisehomes.geometryapi.database;

import java.sql.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import io.github.cdimascio.dotenv.Dotenv;

@ToString
public class PostgresJDBC {
        private static final String DBMS_URL = "jdbc:postgresql:";
        private String pgjdbcUrl;

        @Getter
        @Setter
        private String database;
        @Setter
        private String host;
        @Getter
        @Setter
        private int port; // default == 0

        @Getter
        @Setter
        private String username;
        @Setter
        private String password;

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

        public final Connection getConnection() {
                // Check if pgjdbcUrl is ready
                if (this.pgjdbcUrl == null) {
                        this.pgjdbcUrl = generateJDBCUrl();
                }

                Connection conn = null;
                try {
                        conn = DriverManager.getConnection(this.pgjdbcUrl, this.username, this.password);
                        System.out.println("Connected to the PostgreSQL server successfully!");
                        System.out.println("- Driver: " + conn.getMetaData().getDriverName());
                        System.out.println("- Username: " + conn.getMetaData().getUserName());
                } catch (SQLException e) {
                        System.out.println(e.getMessage());
                }

                return conn;
        }

        /**
         * Get actors count
         * 
         * @return
         */
        public final void printRowsFromTable(String table, int rowsCount) {
                final String RAW_SQL = new StringBuilder()
                                .append("SELECT * FROM %s\n")
                                .append("LIMIT %s")
                                .toString();
                final String SQL = String.format(RAW_SQL, table, rowsCount);

                try (Connection conn = getConnection();
                                Statement stmt = conn.createStatement();
                                ResultSet rs = stmt.executeQuery(SQL)) {

                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnsNumber = rsmd.getColumnCount();

                        // Iterate through the data in the result set and display it.
                        while (rs.next()) {
                                // Print one row
                                for (int i = 1; i <= columnsNumber; i++) {
                                        System.out.print(rsmd.getColumnName(i) + ": ");
                                        System.out.print(rs.getString(i) + "  ||  ");
                                }

                                System.out.println("\n---");// Move to the next line to print the next row.
                        }
                } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                }
        }

        public static void main(String[] args) {
                String rootDirectory = System.getProperty("user.dir");
                Dotenv dotenv = Dotenv.configure()
                                .directory(rootDirectory)
                                .filename(".env")
                                .load();

                PostgresJDBC pg = new PostgresJDBC();
                pg.setHost(dotenv.get(
                                "POSTGRES_DWH_HOST"));
                pg.setPort(Integer.parseInt(dotenv.get(
                                "POSTGRES_DWH_PORT")));
                pg.setDatabase("spatial_db");
                pg.setUsername(dotenv.get(
                                "POSTGRES_DWH_USERNAME"));
                pg.setPassword(dotenv.get(
                                "POSTGRES_DWH_PASSWORD"));

                pg.getConnection();

                pg.printRowsFromTable("chanmay_1km_vietnam", 10);
        }
}