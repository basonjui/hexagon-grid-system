package com.masterisehomes.geometryapi.hexagon;

import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HexagonDto {
    private final String type = "Polygon";
    @Setter
    private List<List<Double>> coordinates;

    public HexagonDto() {
    }

    public HexagonDto(Hexagon hexagon) {
        this.coordinates = convertToArray(hexagon.getVertices());
    }

    public List<List<Double>> convertToArray(List<Coordinates> hexVertices) {
        List<List<Double>> coordinatesArray = new ArrayList<>();

        hexVertices.forEach((vertex) -> coordinatesArray.add(vertex.toArray()));
        return coordinatesArray;
    }
}
