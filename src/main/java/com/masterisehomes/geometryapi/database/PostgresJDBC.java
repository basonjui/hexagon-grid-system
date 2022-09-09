package com.masterisehomes.geometryapi.database;

import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

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
	private final String username;
	@ToString.Exclude
	private final String password;

	public PostgresJDBC(Builder builder) {
		this.host = builder.host;
		this.port = builder.port;
		this.database = builder.database;
		this.username = builder.username;
		this.password = builder.password;
		this.pgjdbcUrl = generateJDBCUrl();
	}

	/* Public methods */
	public final Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(this.pgjdbcUrl, this.username, this.password);
			System.out.println("Connected to the PostgreSQL server: "
					+ conn.getMetaData().getUserName()
					+ "\n");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return conn;
	}

	public final void testQuery(String table, int rowsLimit) {
		final String RAW_SQL = new StringBuilder()
				.append("SELECT * FROM %s\n")
				.append("LIMIT %s")
				.toString();
		final String SQL = String.format(RAW_SQL, table, rowsLimit);

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

	/* PostgresJDBC Builder */
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
			this.username = dotenv.get(usernameKey);
			this.password = dotenv.get(passwordKey);
			return this;
		}

		public final PostgresJDBC build() {
			return new PostgresJDBC(this);
		}
	}

	public void createTable(String tableName, Map<String,String> columns) {
		String createTableSQL = new StringBuilder()
				.append("CREATE TABLE IF NOT EXISTS table_name (")
				.append("	column1 datatype(length) column_contraint,")
				.append("	column2 datatype(length) column_contraint,")
				.append("	column3 datatype(length) column_contraint,")
				.append("	table_constraints			  ")
				.append(");                                               ")
				.toString();

		try (Connection conn = getConnection();
				PreparedStatement createTablePstm = conn.prepareStatement(createTableSQL)) {
			conn.setAutoCommit(false);
			createTablePstm.setInt(1, 69);
			createTablePstm.setString(2, "some string");
			createTablePstm.setString(3, "some string");
			createTablePstm.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/* Test */
	public static void main(String[] args) {
		PostgresJDBC pg = new PostgresJDBC.Builder()
				.host("10.10.12.197")
				.port(5432)
				.database("spatial_db")
				.authentication("POSTGRES_DWH_USERNAME", "POSTGRES_DWH_PASSWORD")
				.build();

		System.out.println("\n---");
		System.out.println(pg.pgjdbcUrl);
		System.out.println("---\n");
		pg.testQuery("chanmay_1km_vietnam", 5);
	}
}