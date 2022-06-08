package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;

@Getter
public class GeoJSON {
    private final FeatureCollection featureCollection;
    private final Feature feature;
    private final Geometry geometry;
    private final Properties<?> properties;

    private GeoJSON(Builder builder) {
        this.featureCollection = builder.featureCollection;
        this.feature = builder.feature;
        this.geometry = builder.geometry;
        this.properties = builder.properties;
    }
    
    public static class Builder {
        private FeatureCollection featureCollection;
        private Feature feature;
        private Geometry geometry;
        private Properties<?> properties;

        public Builder() {
            this.featureCollection = new FeatureCollection();
        }

        public Builder featureCollection(FeatureCollection collection) {
            this.featureCollection = collection;
            return this;
        }

        public Builder feature(Feature feature) {
            this.feature = feature;
            return this;
        }

        public Builder geometry(Geometry geometry) {
            this.geometry = geometry;
            return this;
        }

        public Builder properties(Properties<?> properties) {
            this.properties = properties;
            return this;
        }

        public GeoJSON build() {
            return new GeoJSON(this);
        }

    }
}
