package com.masterisehomes.geometryapi.tessellation;

import java.util.List;
import java.io.Serializable;

import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

import com.google.gson.JsonObject;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;

@Getter
@Setter
@ToString
public class AxialClockwiseTessellationDto implements Serializable {
        private Coordinates rootCentroid;
	private double circumradius;
	private double inradius;

	private Hexagon rootHexagon;
	private Boundary boundary;

        private List<Hexagon> hexagons;
        private List<Hexagon> gisHexagons;

        private int totalRings;
        private int totalHexagons;

        public AxialClockwiseTessellationDto(AxialClockwiseTessellation tessellation) {
                this.rootHexagon = tessellation.getRootHexagon();
                this.rootCentroid = tessellation.getRootHexagon().getCentroid();
                this.circumradius = tessellation.getCircumradius();
                this.inradius = tessellation.getInradius();
                this.boundary = tessellation.getBoundary();

                this.hexagons = tessellation.getHexagons();
                this.gisHexagons = tessellation.getGisHexagons();

                this.totalRings = tessellation.getTotalRings();
                this.totalHexagons = tessellation.getTotalHexagons();
        }

        public AxialClockwiseTessellationDto(JsonObject payload) {
                /* Parse centroid data from payload */
                final double rootLatitude = payload.get("latitude").getAsDouble();
		final double rootLongitude = payload.get("longitude").getAsDouble();

		this.rootCentroid = new Coordinates(rootLongitude, rootLatitude);
		this.circumradius = payload.get("radius").getAsDouble();

		this.rootHexagon = new Hexagon(this.rootCentroid, this.circumradius);
                this.inradius = this.rootHexagon.getInradius();

                /* Parse boundary data from payload */
                final JsonObject boundaryObj = payload.get("boundary").getAsJsonObject();

                final double minLat = boundaryObj.get("min_latitude").getAsDouble();
                final double minLng = boundaryObj.get("min_longitude").getAsDouble();
                final double maxLat = boundaryObj.get("max_latitude").getAsDouble();
                final double maxLng = boundaryObj.get("max_longitude").getAsDouble();

                final Coordinates minCoordinates = new Coordinates(minLng, minLat);
                final Coordinates maxCoordinates = new Coordinates(maxLng, maxLat);
                this.boundary = new Boundary(minCoordinates, maxCoordinates);

                /* Tessellation */
                final AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(rootHexagon);
                tessellation.tessellate(this.boundary);

                this.hexagons = tessellation.getHexagons();
                this.gisHexagons = tessellation.getGisHexagons();

                this.totalRings = tessellation.getTotalRings();
                this.totalHexagons = tessellation.getTotalHexagons();
	}
}
