package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GeoJSON {
    @Getter public final FeatureCollection featureCollection;

    private GeoJSON(Builder builder) {
        this.featureCollection = builder.featureCollection;
    }

    public static class Builder {
        private FeatureCollection featureCollection = new FeatureCollection();
        private Feature feature = new Feature();

        public Builder() {}

        public Builder geometry(Geometry geometry) {
            this.feature.setGeometry(geometry);
            return this;
        }

        public Builder properties() {
            return this;
        }

        public GeoJSON build() {
            this.featureCollection.add(this.feature);
            return new GeoJSON(this);
        }
    }
}
