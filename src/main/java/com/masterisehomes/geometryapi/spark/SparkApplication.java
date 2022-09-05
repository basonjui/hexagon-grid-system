package com.masterisehomes.geometryapi.spark;

/* SimpleApp.java */
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.io.Serializable;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.aggregate.Collect;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.tessellation.Boundary;

import lombok.Getter;
import lombok.Setter;

import com.masterisehomes.geometryapi.tessellation.AxialClockwiseTessellation;
import com.masterisehomes.geometryapi.tessellation.AxialClockwiseTessellationDto;

public class SparkApplication {
	public static void main(String[] args) {
		String logFile = "/home/quan/spark-3.3.0-bin-hadoop3/README.md"; // Should be some file on your
											// system
		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("SparkApplication")
				.getOrCreate();

		// Dataset<String> logData = spark.read().textFile(logFile).cache();

		// long numAs = logData.filter((FilterFunction<String>) s ->
		// s.contains("a")).count();
		// long numBs = logData.filter((FilterFunction<String>) s ->
		// s.contains("b")).count();

		// System.out.println("\n---");
		// System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs +
		// "\n");

		// logData.show(20, false);

		final Coordinates origin = new Coordinates(106, 15);
		// Coordinates origin = new Coordinates(109.466667, 23.383333);
		final Hexagon hexagon = new Hexagon(origin, 2500);

		final AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);

		final Boundary boundary = new Boundary(
				new Coordinates(102.133333, 8.033333),
				new Coordinates(109.466667, 23.383333));

		tessellation.tessellate(boundary);

		final AxialClockwiseTessellationDto dto = new AxialClockwiseTessellationDto(tessellation);

		Encoder<Hexagon> encoder = Encoders
				.bean(Hexagon.class);

		Dataset<Hexagon> ds = spark.createDataset(
				tessellation.getGisHexagons(),
				encoder);

		ds.show();

		spark.stop();
	}
}