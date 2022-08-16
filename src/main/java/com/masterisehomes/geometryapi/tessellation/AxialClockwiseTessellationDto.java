package com.masterisehomes.geometryapi.tessellation;

import java.util.List;

import lombok.ToString;
import lombok.Getter;

import com.masterisehomes.geometryapi.hexagon.Hexagon;

@ToString
public class AxialClockwiseTessellationDto {
        @Getter
	private final Hexagon rootHexagon;
	@Getter
	private final double circumradius;
	@Getter
	private final double inradius;
	@Getter
	private final Boundary boundary;
        @Getter
        private final List<Hexagon> hexagons;
        @Getter
        private final List<Hexagon> gisHexagons;

        @Getter
        private final int totalRings;
        @Getter
        private final int totalHexagons;

        AxialClockwiseTessellationDto(AxialClockwiseTessellation tessellation) {
                this.rootHexagon = tessellation.getRootHexagon();
                this.circumradius = tessellation.getCircumradius();
                this.inradius = tessellation.getInradius();
                this.boundary = tessellation.getBoundary();

                this.hexagons = tessellation.getHexagons();
                this.gisHexagons = tessellation.getGisHexagons();

                this.totalRings = tessellation.getTotalRings();
                this.totalHexagons = tessellation.getTotalHexagons();
        }
}
