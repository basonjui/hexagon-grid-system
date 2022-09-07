package com.masterisehomes.geometryapi.database;

import java.sql.*;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class PostgresJDBC {
        private static final String DBMS_URL = "jdbc:postgresql:";

        @Setter
        private String database;
        @Setter
        private String host;
        @Setter
        private int port; // default == 0

        @Setter
        private String username;
        @Setter
        @Getter(AccessLevel.NONE)
        private String password;
        private String pgjdbcUrl;

        private String generateJDBCUrl() {
                final StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(DBMS_URL);

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

        public Connection getConnection() {
                // Check if pgjdbcUrl is ready
                if (this.pgjdbcUrl == null) {
                        this.pgjdbcUrl = generateJDBCUrl();
                }

                Connection conn = null;
                try {
                        conn = DriverManager.getConnection(this.pgjdbcUrl, this.username, this.password);
                        System.out.println("Connected to the PostgreSQL server successfully.");
                } catch (SQLException e) {
                        System.out.println(e.getMessage());
                }

                return conn;

        }

        public static void main(String[] args) {
                PostgresJDBC pg = new PostgresJDBC();
                pg.setHost("10.10.12.197");
                pg.setPort(5432);
                pg.setDatabase("raw_db");

                pg.setUsername("quan.bui");
                pg.setPassword("Q.bui95@masterise");

                System.out.println("\nJDBC Url: " + pg.generateJDBCUrl());

                pg.getConnection();
        }
}
