package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

// import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

// import com.google.gson.Gson;

@ToString
public class GeoJsonManager {
    @Getter
    private final FeatureCollection featureCollection = new FeatureCollection();
    private Feature feature;
    private Geometry geometry;

    public GeoJsonManager(Hexagon hexagon) {
        this.geometry = new PolygonGeometry(hexagon);
        this.feature = new Feature(this.geometry);
        this.featureCollection.addFeature(this.feature);
    }

    public GeoJsonManager(Neighbors neighbors) {
        neighbors.getGisHexagons().forEach((id, hexagon) -> {
            this.geometry = new PolygonGeometry(hexagon);
            this.feature = new Feature(this.geometry);
            this.feature.addProperty("id", id);
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

    // public static void main(String[] args) {
    //     Gson gson = new Gson();

    //     Coordinates centroid = new Coordinates(106.7455527, 10.8035896);
    //     Hexagon h = new Hexagon(centroid, 500);
    //     Neighbors n = new Neighbors(h);

    //     GeoJsonManager manager = new GeoJsonManager(n);
    //     System.out.println(gson.toJson(manager.getFeatureCollection()));
    // }
}
