package com.masterisehomes.geometryapi.tessellation;

import java.util.List;

import lombok.ToString;
import lombok.Getter;
import com.google.gson.JsonObject;

import com.google.gson.Gson;
import com.masterisehomes.geometryapi.neighbors.Neighbors;
import com.masterisehomes.geometryapi.geodesy.Harversine;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;

@ToString
public class AxialClockwiseTessellationDto {
        @Getter
        private final AxialClockwiseTessellation tessellation;

        @Getter
        private final Coordinates rootCentroid;
	@Getter
	private final double circumradius;
        @Getter
	private final double inradius;

        @Getter
	private final Hexagon rootHexagon;
	@Getter
	private final Boundary boundary;

        @Getter
        private final List<Hexagon> hexagons;
        @Getter
        private final List<Hexagon> gisHexagons;

        @Getter
        private final int totalRings;
        @Getter
        private final int totalHexagons;

        public AxialClockwiseTessellationDto(AxialClockwiseTessellation tessellation) {
                this.tessellation = tessellation;

                this.rootHexagon = tessellation.getRootHexagon();
                this.rootCentroid = this.rootHexagon.getCentroid();
                this.circumradius = tessellation.getCircumradius();
                this.inradius = tessellation.getInradius();
                this.boundary = tessellation.getBoundary();

                this.hexagons = tessellation.getHexagons();
                this.gisHexagons = tessellation.getGisHexagons();

                this.totalRings = tessellation.getTotalRings();
                this.totalHexagons = tessellation.getTotalHexagons();
        }

        public AxialClockwiseTessellationDto(JsonObject payload) {
                /* Get data from payload */
                final double rootLatitude = payload.get("latitude").getAsDouble();
		final double rootLongitude = payload.get("longitude").getAsDouble();
		this.rootCentroid = new Coordinates(rootLongitude, rootLatitude);

		this.circumradius = payload.get("radius").getAsDouble();
		this.rootHexagon = new Hexagon(this.rootCentroid, this.circumradius);
                this.inradius = this.rootHexagon.getInradius();

                final double minLat = payload.get("boundary")
                                .getAsJsonObject()
                                .get("min_latitude")
                                .getAsDouble();
                final double minLng = payload.get("boundary")
                                .getAsJsonObject()
                                .get("min_longitude")
                                .getAsDouble();
                final double maxLat = payload.get("boundary")
                                .getAsJsonObject()
                                .get("max_latitude")
                                .getAsDouble();
                final double maxLng = payload.get("boundary")
                                .getAsJsonObject()
                                .get("max_longitude")
                                .getAsDouble();
                final Coordinates minCoordinates = new Coordinates(minLng, minLat);
                final Coordinates maxCoordinates = new Coordinates(maxLng, maxLat);
                this.boundary = new Boundary(minCoordinates, maxCoordinates);

                /* Tessellate */
                AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(rootHexagon);
                this.tessellation = tessellation;
                this.tessellation.tessellate(this.boundary);

                this.hexagons = this.tessellation.getHexagons();
                this.gisHexagons = this.tessellation.getGisHexagons();

                this.totalRings = this.tessellation.getTotalRings();
                this.totalHexagons = this.tessellation.getTotalHexagons();
	}


	public static void main(String[] args) {
		Gson gson = new Gson();

		Coordinates origin = new Coordinates(107, 23);

		Hexagon hexagon = new Hexagon(origin, 250);
		Neighbors neighbors = new Neighbors(hexagon);

		AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);

		Boundary boundary = new Boundary(
				new Coordinates(106, 20),
						new Coordinates(109.466667, 23.383333));

		// Test harversine
		double greatCircleDistance = Harversine.distance(boundary.getMinLat(), boundary.getMinLng(),
				boundary.getMaxLat(), boundary.getMaxLng());

		tessellation.tessellate(boundary);

                AxialClockwiseTessellationDto dto = new AxialClockwiseTessellationDto(tessellation);

		System.out.println("Great-circle distance: " + greatCircleDistance);
		System.out.println("Total rings: " + dto.getTessellation().getTotalRings());
		System.out.println("Total hexagons: " + dto.getTessellation().getTotalHexagons());
		System.out.println("Boundary: " + dto.getTessellation().getBoundary() + "\n");

		GeoJsonManager tessellationManager = new GeoJsonManager(tessellation);
	}
}
