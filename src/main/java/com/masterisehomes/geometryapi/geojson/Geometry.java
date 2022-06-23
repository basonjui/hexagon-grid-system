package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public abstract class Geometry {
    @Setter protected String type;
    protected List<?> coordinates;

    public Geometry(String type) {
        if(type.equals("Polygon")) {
            this.type = "Polygon";
        } else if (type.equals("MultiPolygon")) {
            this.type = "MultiPolygon";
        } else {
            throw new IllegalArgumentException("Invalid Geometry type.");
        }
    }
}
