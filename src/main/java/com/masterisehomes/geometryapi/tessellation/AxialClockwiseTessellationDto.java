package com.masterisehomes.geometryapi.tessellation;

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

        AxialClockwiseTessellationDto(AxialClockwiseTessellation tessellation) {
                this.rootHexagon = tessellation.getRootHexagon();
                this.circumradius = tessellation.getCircumradius();
                this.inradius = tessellation.getInradius();
                this.boundary = tessellation.getBoundary();
        }
}
