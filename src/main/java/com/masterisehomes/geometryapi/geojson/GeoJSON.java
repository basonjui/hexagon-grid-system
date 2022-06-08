package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GeoJSON {
    private final FeatureCollection featureCollection;

    private GeoJSON(Builder builder) {
        this.featureCollection = builder.featureCollection;
    }

    public static class Builder {
        private FeatureCollection featureCollection = new FeatureCollection();
        private Feature feature;
        private Geometry geometry;
        private Properties<?> properties;

        public Builder() {}

        public Builder geometry(Geometry geometry) {
            this.geometry = geometry;
            this.properties = new Properties<>();
            return this;
        }

        public Builder properties(Properties<?> properties) {
            this.properties = properties;
            return this;
        }

        public GeoJSON build() {
            this.feature = new Feature();
            this.feature.addGeometry(this.geometry);
            this.feature.addProperties(this.properties);
            this.featureCollection.add(this.feature);
            return new GeoJSON(this);
        }
    }
}
