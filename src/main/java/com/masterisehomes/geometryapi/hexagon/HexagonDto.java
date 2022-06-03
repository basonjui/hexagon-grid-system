package com.masterisehomes.geometryapi.hexagon;

import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

// This class acts as 

@Getter
@ToString
public class HexagonDto {
    private final String type = "Polygon";
    @Setter
    private List<List<Double>> coordinates;

    public HexagonDto() {
    }

    public HexagonDto(Hexagon hexagon) {
        List<Coordinates> verticesCoordinates = hexagon.getVertices();
        this.coordinates = toGeoJsonCoordinates(verticesCoordinates);
    }

    private List<List<Double>> toGeoJsonCoordinates(List<Coordinates> hexVertices) {
        List<List<Double>> coordinatesArray = new ArrayList<List<Double>>();

        hexVertices.forEach((vertex) -> coordinatesArray.add(vertex.toArray()));
        return coordinatesArray;
    }
}
