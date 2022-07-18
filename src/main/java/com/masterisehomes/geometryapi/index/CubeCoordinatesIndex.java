package com.masterisehomes.geometryapi.index;

import lombok.Getter;

public class CubeCoordinatesIndex {
    @Getter
    private int q;
    @Getter
    private int r;
    @Getter
    private int s;

    public CubeCoordinatesIndex(CubeCoordinatesIndex previousIndex, int hexagonDirection) {
        switch (hexagonDirection) {
            case 0: // default case - where previousIndex == null & hexagonDirection == 0 (FYI)
                this.q = 0;
                this.r = 0;
                this.s = 0;
                break;

            case 1:
                // logic to calculate new index base on previousIndex & hexagonDirection
                break;
        }
    }
}
