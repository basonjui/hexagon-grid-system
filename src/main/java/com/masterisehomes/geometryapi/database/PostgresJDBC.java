package com.masterisehomes.geometryapi.database;

import java.sql.*;
import java.util.Properties;

public class PostgresJDBC {
        private static final String DBMS_URL = "jdbc:postgresql:";

        private String DATABASE;
        private String HOST;
        private int PORT; // default == 0
        
        private String JDBC_URL;

        public void setDatabase(String database) {
                this.DATABASE = database;
        }

        public void setHost(String host) {
                this.HOST = host;
        }

        public void setPort(int port) {
                this.PORT = port;
        }

        private String generateJDBCUrl() {
                final StringBuilder URL_BUILDER = new StringBuilder();

                if (this.HOST.isEmpty()) {
                        if (this.DATABASE.isEmpty()) {
                                // jdbc:postgresql:/
                                URL_BUILDER.append(DBMS_URL).append("/");
                        } else {
                                // jdbc:postgresql:database
                                URL_BUILDER.append(this.DATABASE);
                        }
                } else {
                        // jdbc:postgresql://host
                        URL_BUILDER.append("//").append(this.HOST);

                        if (this.DATABASE.isEmpty()) {
                                /* If there is no specified PORT */
                                if (this.PORT == 0) {
                                        // jdbc:postgresql://host/
                                        URL_BUILDER.append("/");
                                } else {
                                        // jdbc:postgresql://host:port/
                                        URL_BUILDER.append(":").append(this.PORT)
                                                        .append("/");
                                }

                        } else {
                                if (this.PORT == 0) {
                                        // jdbc:postgresql://host/database
                                        URL_BUILDER.append("/").append(this.DATABASE);
                                } else {
                                        // jdbc:postgresql://host:port/database
                                        URL_BUILDER.append(":").append(this.PORT)
                                                        .append("/").append(this.DATABASE);
                                }
                        }
                }

                return URL_BUILDER.toString();
        }

        public Connection getConnection() throws SQLException {
                // Check if JDBC_URL is ready
                if (this.JDBC_URL.isEmpty()) {
                        this.JDBC_URL = generateJDBCUrl();
                }
                
                Connection conn = null;
                // Properties connectionProps = new Properties();
                // connectionProps.put("user", this.userName);
                // connectionProps.put("password", this.password);

                // if (this.dbms.equals("mysql")) {
                //         conn = DriverManager.getConnection(
                //                         "jdbc:" + this.dbms + "://" +
                //                                         this.serverName +
                //                                         ":" + this.portNumber + "/",
                //                         connectionProps);
                // } else if (this.dbms.equals("derby")) {
                //         conn = DriverManager.getConnection(
                //                         "jdbc:" + this.dbms + ":" +
                //                                         this.dbName +
                //                                         ";create=true",
                //                         connectionProps);
                // }

                System.out.println("Connected to database");
                return conn;
        }
}
