package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;
import com.masterisehomes.geometryapi.tessellation.CornerEdgeTessellation;

@ToString
public class GeoJsonManager {
	@Getter
	private final FeatureCollection featureCollection = new FeatureCollection();

	public GeoJsonManager(Hexagon hexagon) {
		Feature feature = new Feature(new PolygonGeometry(hexagon));

		feature.addProperty("ccid", hexagon.getCCI());
		feature.addProperty("centroid", hexagon.getCentroid());
		feature.addProperty("circumradius", hexagon.getCircumradius());
		feature.addProperty("inradius", hexagon.getInradius());

		this.featureCollection.addFeature(feature);
	}

	public GeoJsonManager(Neighbors neighbors) {
		List<Hexagon> neighborsHexagons = neighbors.getGisHexagons();

		for (Hexagon hexagon : neighborsHexagons) {
			Feature feature = new Feature(new PolygonGeometry(hexagon));

			feature.addProperty("ccid", hexagon.getCCI());
			feature.addProperty("centroid", hexagon.getCentroid());
			feature.addProperty("circumradius", hexagon.getCircumradius());
			feature.addProperty("inradius", hexagon.getInradius());

			this.featureCollection.addFeature(feature);
		};
	}

	public GeoJsonManager(CornerEdgeTessellation tessellation) {
		final List<Hexagon> tessellationHexagons = tessellation.getGisHexagons();

		for (Hexagon hexagon : tessellationHexagons) {
			Feature feature = new Feature(new PolygonGeometry(hexagon));

			feature.addProperty("ccid", hexagon.getCCI());
			feature.addProperty("centroid", hexagon.getCentroid());
			feature.addProperty("circumradius", hexagon.getCircumradius());
			feature.addProperty("inradius", hexagon.getInradius());

			this.featureCollection.addFeature(feature);
		};
	}

	/* Utility methods */
	public int getHashCode() {
		return this.featureCollection.hashCode();
	}
}