package com.masterisehomes.geometryapi.index;

import lombok.Getter;

public class CubeCoordinatesIndex {
    @Getter
    private int q;
    @Getter
    private int r;
    @Getter
    private int s;

    public CubeCoordinatesIndex(CubeCoordinatesIndex previousIndex, HexagonDirection generatedDirection) {
        switch (generatedDirection) {
            case NONE: // default case - where previousIndex == null & generatedDirection == 0 (FYI)
                this.q = 0;
                this.r = 0;
                this.s = 0;
                break;

            case ONE:
                // logic to calculate new index base on previousIndex & generatedDirection
                break;
                
            case TWO:
                // logic to calculate new index base on previousIndex & generatedDirection
                break;

            case THREE:
                // logic to calculate new index base on previousIndex & generatedDirection
                break;

            case FOUR:
                // logic to calculate new index base on previousIndex & generatedDirection
                break;

            case FIVE:
                // logic to calculate new index base on previousIndex & generatedDirection
                break;

            case SIX:
                // logic to calculate new index base on previousIndex & generatedDirection
                break;
        }
    }
}
