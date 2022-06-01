package com.masterisehomes.geometryapi.hexagon;

import java.util.List;

public class HexagonDto {
    private final String type = "Polygon";
    private List<Coordinates> coordinates;

    public String toString() {
        return "type: " + type + ", coordinates: " + coordinates;
    }
}
