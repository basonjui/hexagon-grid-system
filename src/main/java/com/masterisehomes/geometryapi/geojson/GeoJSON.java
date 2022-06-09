package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GeoJSON {
    public final FeatureCollection featureCollection;

    private GeoJSON(Builder builder) {
        this.featureCollection = builder.featureCollection;
    }

    public static class Builder {
        private FeatureCollection featureCollection = new FeatureCollection();
        private Feature feature = new Feature();
        private Geometry geometry;
        private Property<?> properties;

        public Builder() {}

        public Builder geometry(Geometry geometry) {
            this.geometry = geometry;
            this.feature.setGeometry(this.geometry);
            return this;
        }

        public Builder properties(Property<?> properties) {
            this.properties = properties;
            return this;
        }

        public GeoJSON build() {
            this.featureCollection.add(this.feature);
            return new GeoJSON(this);
        }
    }
}
