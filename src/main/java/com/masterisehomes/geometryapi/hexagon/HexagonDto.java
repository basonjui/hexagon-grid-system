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
    @Setter private List<Coordinates> vertices;

    public HexagonDto() {}

    public HexagonDto(Hexagon hexagon) {
        this.vertices = hexagon.getVertices();
    }
}
