package com.masterisehomes.geometryapi;

import com.masterisehomes.geometryapi.hexagon.HexagonDto;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import java.util.List;

public class Sandbox {
    public static void main(String[] args) {
        Coordinates centroid = new Coordinates(100, 100);
        Hexagon myHex = new Hexagon(centroid, 50);
        HexagonDto myDto = new HexagonDto(myHex);

        System.out.println(myDto);
    }
}
