package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;
import com.masterisehomes.geometryapi.tessellation.AxialClockwiseTessellation;

@ToString
public class GeoJsonManager {
	@Getter
	private final FeatureCollection featureCollection = new FeatureCollection();

	public GeoJsonManager(Hexagon hexagon) {
		Feature feature = new Feature(new PolygonGeometry(hexagon));

		feature.addProperty("id", 0);
		feature.addProperty("ccid", hexagon.getCCI());
		feature.addProperty("latitude", hexagon.getCentroid().getLatitude());
		feature.addProperty("longitude", hexagon.getCentroid().getLongitude());
		feature.addProperty("circumradius", hexagon.getCircumradius());

		this.featureCollection.addFeature(feature);
	}

	public GeoJsonManager(Neighbors neighbors) {
		List<Hexagon> gisHexagons = neighbors.getGisHexagons();

		for (int i = 0; i < gisHexagons.size(); i++) {
			Hexagon hexagon = gisHexagons.get(i);

			Feature feature = new Feature(new PolygonGeometry(hexagon));
			feature.addProperty("id", i);
			feature.addProperty("ccid", hexagon.getCCI());
			feature.addProperty("latitude", hexagon.getCentroid().getLatitude());
			feature.addProperty("longitude", hexagon.getCentroid().getLongitude());
			feature.addProperty("circumradius", hexagon.getCircumradius());

			this.featureCollection.addFeature(feature);
		};
	}

	public GeoJsonManager(List<Hexagon> gisHexagons) {
		for (int i = 0; i < gisHexagons.size(); i++) {
			Hexagon hexagon = gisHexagons.get(i);

			Feature feature = new Feature(new PolygonGeometry(hexagon));
			feature.addProperty("id", i);
			feature.addProperty("ccid", hexagon.getCCI());
			feature.addProperty("latitude", hexagon.getCentroid().getLatitude());
			feature.addProperty("longitude", hexagon.getCentroid().getLongitude());
			// this.feature.addProperty("circumradius", hexagon.getCircumradius());

			this.featureCollection.addFeature(feature);
		};
	}

	/* Utility methods */
	public int getHashCode() {
		return this.featureCollection.hashCode();
	}
}