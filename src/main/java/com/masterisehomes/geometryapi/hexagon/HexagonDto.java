package com.masterisehomes.geometryapi.hexagon;

import java.io.Serializable;
import java.util.List;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class HexagonDto {
    private final String type = "Polygon";
    @Setter private List<Coordinates> coordinates;

    public HexagonDto() {}

    public HexagonDto(Hexagon hexagon) {
        this.coordinates = hexagon.getVertices();
    }

    public static void main(String[] args) {
        Coordinates centroid = new Coordinates(100, 100);
        Hexagon myHex = new Hexagon(centroid, 150);
        HexagonDto myDto = new HexagonDto(myHex);

        System.out.println(myDto.coordinates.get(0).getLatitude());

    }
}
