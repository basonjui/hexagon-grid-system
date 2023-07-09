package com.geospatial.hexagongrid.tessellation;

import java.util.List;
import java.io.Serializable;

import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

import com.geospatial.hexagongrid.hexagon.Coordinates;
import com.geospatial.hexagongrid.hexagon.Hexagon;
import com.google.gson.JsonObject;

@Getter
@Setter
@ToString
public class CornerEdgeTessellationDto implements Serializable {
        private CornerEdgeTessellation tessellation;

        private Coordinates rootCentroid;
	private double circumradius;
	private double inradius;

	private Hexagon rootHexagon;
	private Boundary boundary;

        private List<Hexagon> hexagons;
        private List<Hexagon> gisHexagons;

        private int totalRings;
        private int totalHexagons;

        public CornerEdgeTessellationDto(CornerEdgeTessellation tessellation) {
                // Note that for this constructor, tessellation has already called .tessellate()
                this.tessellation = tessellation;

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

        public CornerEdgeTessellationDto(JsonObject payload) {
                /* Parse centroid data from payload */
                final double rootLatitude = payload.get("latitude").getAsDouble();
		final double rootLongitude = payload.get("longitude").getAsDouble();

		this.rootCentroid = new Coordinates(rootLongitude, rootLatitude);
		this.circumradius = payload.get("radius").getAsDouble();
		this.rootHexagon = new Hexagon(rootCentroid, circumradius);
                this.inradius = rootHexagon.getInradius();

                /* Parse boundary data from payload */
                final JsonObject boundaryJsonObject = payload.get("boundary").getAsJsonObject();

                final double minLat = boundaryJsonObject.get("minLatitude").getAsDouble();
                final double minLng = boundaryJsonObject.get("minLongitude").getAsDouble();
                final double maxLat = boundaryJsonObject.get("maxLatitude").getAsDouble();
                final double maxLng = boundaryJsonObject.get("maxLongitude").getAsDouble();

                final Coordinates minCoordinates = new Coordinates(minLng, minLat);
                final Coordinates maxCoordinates = new Coordinates(maxLng, maxLat);
                this.boundary = new Boundary(minCoordinates, maxCoordinates);

                /* Tessellation */
                final CornerEdgeTessellation tessellation = new CornerEdgeTessellation(rootHexagon);
                tessellation.tessellate(boundary);
                this.tessellation = tessellation;

                this.hexagons = tessellation.getHexagons();
                this.gisHexagons = tessellation.getGisHexagons();

                this.totalRings = tessellation.getTotalRings();
                this.totalHexagons = tessellation.getTotalHexagons();
	}
}
