package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

@ToString
public class GeoJsonManager {
    @Getter
    private final FeatureCollection featureCollection = new FeatureCollection();
    private Feature feature;
    private Geometry geometry;

    public GeoJsonManager(Hexagon hexagon) {
        this.geometry = new PolygonGeometry(hexagon);
        this.feature = new Feature(this.geometry);
        this.feature.addProperty("latitude", hexagon.getCentroid().getLatitude());
        this.feature.addProperty("longitude", hexagon.getCentroid().getLongitude());
        this.feature.addProperty("circumradius", hexagon.getCircumradius());
        this.featureCollection.addFeature(this.feature);
    }

    public GeoJsonManager(Neighbors neighbors) {
        neighbors.getGisHexagons().forEach((id, hexagon) -> {
            this.geometry = new PolygonGeometry(hexagon);
            this.feature = new Feature(this.geometry);
            this.feature.addProperty("id", id);
            this.feature.addProperty("ccid", hexagon.getCCI());
            this.feature.addProperty("latitude", hexagon.getCentroid().getLatitude());
            this.feature.addProperty("longitude", hexagon.getCentroid().getLongitude());
            this.feature.addProperty("circumradius", hexagon.getCircumradius());
            this.featureCollection.addFeature(this.feature);
        });
    }

    // Utility methods
    public int getHashCode() {
        return this.featureCollection.hashCode();
    }
}
