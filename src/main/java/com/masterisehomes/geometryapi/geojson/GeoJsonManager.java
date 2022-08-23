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
	private Feature feature;

	public GeoJsonManager(Hexagon hexagon) {
		this.feature = new Feature(new PolygonGeometry(hexagon));

		this.feature.addProperty("id", 0);
		this.feature.addProperty("ccid", hexagon.getCCI());
		this.feature.addProperty("latitude", hexagon.getCentroid().getLatitude());
		this.feature.addProperty("longitude", hexagon.getCentroid().getLongitude());
		this.feature.addProperty("circumradius", hexagon.getCircumradius());

		this.featureCollection.addFeature(this.feature);
	}

	public GeoJsonManager(Neighbors neighbors) {
		List<Hexagon> gisHexagons = neighbors.getGisHexagons();

		for (int i = 0; i < gisHexagons.size(); i++) {
			Hexagon hexagon = gisHexagons.get(i);

			this.feature = new Feature(new PolygonGeometry(hexagon));
			this.feature.addProperty("id", i);
			this.feature.addProperty("ccid", hexagon.getCCI());
			this.feature.addProperty("latitude", hexagon.getCentroid().getLatitude());
			this.feature.addProperty("longitude", hexagon.getCentroid().getLongitude());
			this.feature.addProperty("circumradius", hexagon.getCircumradius());

			this.featureCollection.addFeature(this.feature);
		};
	}

	public GeoJsonManager(AxialClockwiseTessellation tessellation) {
		List<Hexagon> gisHexagons = tessellation.getGisHexagons();

		for (int i = 0; i < gisHexagons.size(); i++) {
			Hexagon hexagon = gisHexagons.get(i);

			this.feature = new Feature(new PolygonGeometry(hexagon));
			this.feature.addProperty("id", i);
			this.feature.addProperty("ccid", hexagon.getCCI());
			this.feature.addProperty("latitude", hexagon.getCentroid().getLatitude());
			this.feature.addProperty("longitude", hexagon.getCentroid().getLongitude());
			this.feature.addProperty("circumradius", hexagon.getCircumradius());

			this.featureCollection.addFeature(this.feature);
		};
	}

	/* Utility methods */
	public int getHashCode() {
		return this.featureCollection.hashCode();
	}
}