package com.masterisehomes.geometryapi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.ToString;

@ToString
public class PostgresJDBC {
        private static final String DBMS_URL = "jdbc:postgresql:";
        private String pgjdbcUrl;

        private String host;
        private int port; // default == 0
        @Getter
        private String database;
        
        @Getter
        @ToString.Exclude
        private String username;
        @ToString.Exclude
        private String password;

        public PostgresJDBC(Builder builder) {
                this.host = builder.host;
                this.port = builder.port;
                this.database = builder.database;
                this.username = builder.username;
                this.password = builder.password;
                this.pgjdbcUrl = generateJDBCUrl();
        }

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
                        System.out.println("Connected to the PostgreSQL server with username: " 
                                        + conn.getMetaData().getUserName()
                                        + "\n");
                } catch (SQLException e) {
                        System.out.println(e.getMessage());
                }

                return conn;
        }

        public final void printRowsFromTable(String table, int rowsCount) {
                final String RAW_SQL = new StringBuilder()
                                .append("SELECT * FROM %s\n")
                                .append("LIMIT %s")
                                .toString();
                final String SQL = String.format(RAW_SQL, table, rowsCount);

                try (final Connection conn = getConnection();
                                final Statement stmt = conn.createStatement();
                                final ResultSet rs = stmt.executeQuery(SQL)) {

                        final ResultSetMetaData rsmd = rs.getMetaData();
                        final int columnsCount = rsmd.getColumnCount();
                        
                        // Iterate through the data in the result set and display it.
                        while (rs.next()) {
                                // Print one row
                                for (int i = 1; i <= columnsCount; i++) {
                                        System.out.print(rsmd.getColumnName(i) + ": ");
                                        System.out.println(rs.getString(i));
                                }

                                System.out.println("---");// Move to the next line to print the next row.
                        }
                } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                }
        }

        public static class Builder {
                private String host;
                private int port;
                private String database;
       
                private String username;
                private String password;

                private final Dotenv dotenv = Dotenv.configure()
                                .directory(System.getProperty("user.dir"))
                                .filename(".env")
                                .load();

                public Builder() {}

                public Builder host(String host) {
                        this.host = host;
                        return this;
                }

                public Builder port(int port) {
                        this.port = port;
                        return this;
                }

                public Builder database(String database) {
                        this.database = database;
                        return this;
                }

                public Builder authentication(String usernameKey, String passwordKey) {
                        this.username = dotenv.get(usernameKey);
                        this.password = dotenv.get(passwordKey);
                        return this;
                }
                
                public PostgresJDBC build() {
                        return new PostgresJDBC(this);
                }
        }

        public static void main(String[] args) {
                String rootDirectory = System.getProperty("user.dir");
                Dotenv dotenv = Dotenv.configure()
                                .directory(rootDirectory)
                                .filename(".env")
                                .load();

                PostgresJDBC pg = new PostgresJDBC.Builder()
                                .host("10.10.12.197")
                                .port(5432)
                                .database("spatial_db")
                                .authentication("POSTGRES_DWH_USERNAME", "POSTGRES_DWH_PASSWORD")
                                .build();

                System.out.println(pg);

                pg.getConnection();

                pg.printRowsFromTable("chanmay_1km_vietnam", 10);
        }
}