package com.geospatial.geometryapi.spark;

import java.util.concurrent.TimeoutException;

import org.apache.spark.sql.SparkSession;

import com.geospatial.geometryapi.hexagon.Coordinates;
import com.geospatial.geometryapi.hexagon.Hexagon;
import com.geospatial.geometryapi.tessellation.Boundary;
import com.geospatial.geometryapi.tessellation.CornerEdgeTessellation;
import com.geospatial.geometryapi.tessellation.CornerEdgeTessellationDto;
import com.geospatial.geometryapi.utils.JVMUtils;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;

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

		final CornerEdgeTessellation tessellation = new CornerEdgeTessellation(hexagon);
		tessellation.tessellate(boundary);

		final CornerEdgeTessellationDto dto = new CornerEdgeTessellationDto(tessellation);

		Encoder<Hexagon> hexagonEncoder = Encoders.bean(Hexagon.class);
		Dataset<Hexagon> ds = spark.createDataset(dto.getGisHexagons(), hexagonEncoder);

		System.out.println("Total rows: " + ds.count());
		ds.show(10);

		JVMUtils.printMemoryUsages("MB");

		// try {
		// 	System.out.print("\nPress enter to stop Spark: ");
		// 	System.in.read();
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }

		spark.stop();
	}
}