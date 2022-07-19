package com.masterisehomes.geometryapi.index;

import lombok.Getter;
import lombok.ToString;

@ToString
public class CubeCoordinatesIndex {
    @Getter
    private int q;
    @Getter
    private int r;
    @Getter
    private int s;

    public CubeCoordinatesIndex(CubeCoordinatesIndex previousCCI, HexagonDirection direction) {
        switch (direction) {
            case ZERO: // default case - where previousCCI == null & direction == 0 (FYI)
                this.q = 0;
                this.r = 0;
                this.s = 0;
                break;

            case ONE:
                // logic to calculate new index base on Hexagon's previousCCI & direction
                this.q = previousCCI.getQ();
                this.r = previousCCI.getR() - 1;
                this.s = previousCCI.getS() + 1;
                break;
                
            case TWO:
                // logic to calculate new index base on Hexagon's previousCCI & direction
                break;

            case THREE:
                // logic to calculate new index base on Hexagon's previousCCI & direction
                break;

            case FOUR:
                // logic to calculate new index base on Hexagon's previousCCI & direction
                break;

            case FIVE:
                // logic to calculate new index base on Hexagon's previousCCI & direction
                break;

            case SIX:
                // logic to calculate new index base on Hexagon's previousCCI & direction
                break;
        }
    }

    public String getIndex() {
        return String.format("(q=%s, r=%s, s=%s)", this.q, this.r, this.s); 
    }
}
