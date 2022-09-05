package com.masterisehomes.geometryapi.spark;

/* SimpleApp.java */
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.api.java.function.*;

public class SparkApplication {
	public static void main(String[] args) {
		String logFile = "/home/quan/spark-3.3.0-bin-hadoop3/README.md"; // Should be some file on your
											// system
		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("SparkApplication")
				.getOrCreate();
		Dataset<String> logData = spark.read().textFile(logFile).cache();

		long numAs = logData.filter((FilterFunction<String>) s -> s.contains("a")).count();
		long numBs = logData.filter((FilterFunction<String>) s -> s.contains("b")).count();

		for(int i = 0; i < 10; i++) {
			System.out.println("Spark is easyyyyyyyy for Quan Bui");
		}
		// System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);

		spark.stop();
	}
}