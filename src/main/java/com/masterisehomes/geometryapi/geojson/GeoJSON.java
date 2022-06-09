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

        public Builder() {}

        public Builder addFeature(Geometry geometry) {
            Feature feature = new Feature(geometry);
            this.featureCollection.add(feature);
            return this;
        }

        public Builder addProperty() {
            return this;
        }

        public GeoJSON build() {
            return new GeoJSON(this);
        }
    }
}
