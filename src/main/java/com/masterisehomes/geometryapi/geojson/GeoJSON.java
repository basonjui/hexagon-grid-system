package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GeoJSON {
    @Getter private final FeatureCollection featureCollection;

    private GeoJSON(Builder builder) {
        this.featureCollection = builder.featureCollection;
    }

    public static class Builder {
        private FeatureCollection featureCollection = new FeatureCollection();
        private Feature feature;

        public Builder() {}

        public Builder geometry(Geometry geometry) {
            this.feature = new Feature(geometry);
            return this;
        }

        public Builder addProperty() {
            return this;
        }

        public GeoJSON build() {
            this.featureCollection.add(this.feature);
            return new GeoJSON(this);
        }
    }
}
