package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

@ToString
public class GeoJsonManager {
    @Getter
    private final FeatureCollection featureCollection = new FeatureCollection();
    private final Feature feature;
    private final Geometry geometry;

    public GeoJsonManager(Hexagon hexagon) {
        this.geometry = new HexagonGeometry(hexagon);
        this.feature = new Feature(this.geometry);
        this.featureCollection.addFeature(this.feature);
    }

    public GeoJsonManager(Neighbors neighbors) {
        this.geometry = new NeighborsGeometry(neighbors);
        this.feature = new Feature(this.geometry);
        this.featureCollection.addFeature(this.feature);
    }

    // Utility methods
    public int getHashCode() {
        return this.featureCollection.hashCode();
    }

    public static void main(String[] args) {
        Coordinates centroid = new Coordinates(100, 100);
        Hexagon hex = new Hexagon(centroid, 50);    
        
        GeoJsonManager manager = new GeoJsonManager(hex);
        System.out.println(
            manager.getFeatureCollection().getFeature(0).getGeometry() instanceof HexagonGeometry
        );
    }
}
