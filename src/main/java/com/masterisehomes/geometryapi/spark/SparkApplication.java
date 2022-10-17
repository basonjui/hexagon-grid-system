package com.masterisehomes.geometryapi.spark;

import java.util.concurrent.TimeoutException;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.tessellation.Boundary;

import com.masterisehomes.geometryapi.tessellation.AxialClockwiseTessellation;
import com.masterisehomes.geometryapi.tessellation.AxialClockwiseTessellationDto;

import com.masterisehomes.geometryapi.utils.JVMUtils;

public class SparkApplication {
	public static void main(String[] args) throws TimeoutException {
		SparkSession spark = SparkSession.builder()
				.master("local[*]")
				.appName("SparkApplication")
				.getOrCreate();

		final Coordinates origin = new Coordinates(106, 15);
		final Hexagon hexagon = new Hexagon(origin, 5000);
		final Boundary boundary = new Boundary(
				new Coordinates(102.133333, 8.033333),
				new Coordinates(109.466667, 23.383333));

		final AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);
		tessellation.tessellate(boundary);

		final AxialClockwiseTessellationDto dto = new AxialClockwiseTessellationDto(tessellation);

		Encoder<Hexagon> hexagonEncoder = Encoders.bean(Hexagon.class);
		Dataset<Hexagon> ds = spark.createDataset(dto.getGisHexagons(), hexagonEncoder);

		System.out.println("Total rows: " + ds.count());
		ds.show(10);

		JVMUtils.printMemories("MB");

		// try {
		// 	System.out.print("\nPress enter to stop Spark: ");
		// 	System.in.read();
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }

		spark.stop();
	}
}