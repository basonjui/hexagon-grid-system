package com.masterisehomes.geometryapi.database;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.tessellation.AxialClockwiseTessellation;
import com.masterisehomes.geometryapi.tessellation.Boundary;

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
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(this.pgjdbcUrl, this.username, this.password);
			System.out.println("Connected to the PostgreSQL server: "
					+ connection.getMetaData().getUserName());
		} catch (SQLException e) {
			printSQLException(e);
		}

		return connection;
	}

	public final void testQuery(String table, int rowsLimit) {
		final String RAW_SQL = new StringBuilder()
				.append("SELECT * FROM %s\n")
				.append("LIMIT %s")
				.toString();
		final String SQL = String.format(RAW_SQL, table, rowsLimit);

		try (final Connection connection = getConnection();
				final Statement stmt = connection.createStatement();
				final ResultSet rs = stmt.executeQuery(SQL)) {

			final ResultSetMetaData rsmd = rs.getMetaData();
			final int columnsCount = rsmd.getColumnCount();

			System.out.println("--- Query results ---");
			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				// Print one row
				for (int i = 1; i <= columnsCount; i++) {
					String tabs;
					if (rsmd.getColumnName(i).length() < 8) {
						tabs = "\t\t\t";
					} else {
						tabs = "\t\t";
					}

					System.out.print(String.format("(%s) ", rsmd.getColumnTypeName(i)));
					System.out.print(rsmd.getColumnName(i) + tabs + ": ");
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
			System.out.println("Creating geometry table successfully with query:");
			System.out.println("---\n" + CREATE_TABLE_SQL);
		} catch (SQLException e) {
			printSQLException(e);
		}
	}

	public final void batchInsert(String tableName, AxialClockwiseTessellation tessellation) throws Exception {
		final String INSERT_SQL = "INSERT INTO " + tableName
				+ " (ccid_q, ccid_r, ccid_s, circumradius, centroid, geometry) VALUES "
				+ " (?, ?, ?, ?, ?);";

/* 		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) { */

			final List<Hexagon> gisHexagons = tessellation.getGisHexagons();
			final int TOTAL_HEXAGONS = gisHexagons.size();
			final int MAX_BATCH_SIZE = 100;
			final int MIN_BATCH_SIZE = 10;
			final int MAX_VALUES_PER_INSERT = 100; // can

			int batchSize; // hexagons per batch
			if (TOTAL_HEXAGONS >= MAX_BATCH_SIZE) {
				batchSize = MAX_BATCH_SIZE;
			} else if (TOTAL_HEXAGONS >= MIN_BATCH_SIZE) {
				batchSize = MIN_BATCH_SIZE;
			} else {
				throw new Exception("Tessellation size is smaller than required minimum batch size: "
						+ MIN_BATCH_SIZE);
			}

			List<List<Hexagon>> hexagonBatches = Lists.partition(gisHexagons, batchSize);
			System.out.println("Total batches: " + hexagonBatches.size());

			final int totalBatches = hexagonBatches.size();
			// Displaying the sublists
			for (int i = 0; i < totalBatches; i++) {
				if (i % 100 == 0 && i != 0) {
					System.out.println("Reached 100th batch, should executeBatch() now..");
				}

				System.out.println("\n--- Batch " + i + ":");

				List<Hexagon> hexagonBatch = hexagonBatches.get(i);
				for (int ii = 0; ii < hexagonBatch.size(); ii++) {
					// Hexagon hexagon = hexagonBatch.get(ii);
					System.out.println("Hexagon" + ii);
				}
			}

			System.out.println("\nTotal batches: " + totalBatches);

			// connection.setAutoCommit(false);

			// preparedStatement.setInt(1, 20);
			// preparedStatement.setInt(2, 20);
			// preparedStatement.setInt(3, 20);
			// preparedStatement.setDouble(4, 1000);
			// preparedStatement.setString(5, "secret");
			// preparedStatement.setString(6, "secret");
			// preparedStatement.addBatch();

			// int[] updateCounts = preparedStatement.executeBatch();
			// System.out.println(Arrays.toString(updateCounts));
			// connection.commit();
			// connection.setAutoCommit(true);
	/* 	} catch (BatchUpdateException batchUpdateException) {
			printBatchUpdateException(batchUpdateException);
		} catch (SQLException e) {
			printSQLException(e);
		} */
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

	/* Test */
	public static void main(String[] args) {
		PostgresJDBC pg = new PostgresJDBC.Builder()
				.host("10.10.12.197")
				.port(5432)
				.database("spatial_db")
				.authentication("POSTGRES_DWH_USERNAME", "POSTGRES_DWH_PASSWORD")
				.build();

		// pg.testQuery("chanmay_1km_vietnam", 5);

		// pg.createGeometryTable("quan_test_table");

		final Coordinates origin = new Coordinates(106, 15);
		// Coordinates origin = new Coordinates(109.466667, 23.383333);
		final Hexagon hexagon = new Hexagon(origin, 10000);

		final AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);

		final Boundary boundary = new Boundary(
				new Coordinates(102.133333, 8.033333),
				new Coordinates(109.466667, 23.383333));

		tessellation.tessellate(boundary);

		try {
			pg.batchInsert("testTable", tessellation);
			System.out.println(
					"Total hexagons: "
							+ tessellation.getTotalHexagons());
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}