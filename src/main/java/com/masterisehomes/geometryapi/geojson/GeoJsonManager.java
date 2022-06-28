package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

@ToString
public class GeoJsonManager {
    @Getter
    private final FeatureCollection featureCollection = new FeatureCollection();
    @Getter
    private final Feature feature;
    @Getter
    private final Geometry geometry;

    public GeoJsonManager(Hexagon hexagon) {
        this.geometry = new HexagonGeometry(hexagon);
        this.feature = new Feature(this.geometry);
        this.featureCollection.add(this.feature);
    }

    public GeoJsonManager(Neighbors neighbors) {
        this.geometry = new NeighborsGeometry(neighbors);
        this.feature = new Feature(this.geometry);
        this.featureCollection.add(this.feature);
    }

    /*
     * GeoJSON methods
     * For future, in case we deal with GeometryCollection (a Geometry type)
     * 
     * public Feature getFeatureById(int index) {
     *      return this.featureCollection.get(index);
     * }
     */

    // Utility methods
    public int getHashCode() {
        return this.featureCollection.hashCode();
    }
}
