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

    public CubeCoordinatesIndex(CubeCoordinatesIndex creatorCCI, HexagonDirection direction) {
        switch (direction) {
            case NONE: // default case - where creatorCCI == null & direction == 0 (FYI)
                this.q = 0;
                this.r = 0;
                this.s = 0;
                break;

            case ONE:
                // logic to calculate new index base on Hexagon's creatorCCI & direction
                this.q = creatorCCI.getQ();
                this.r = creatorCCI.getR() - 1;
                this.s = creatorCCI.getS() + 1;
                break;
                
            case TWO:
                // logic to calculate new index base on Hexagon's creatorCCI & direction
                break;

            case THREE:
                // logic to calculate new index base on Hexagon's creatorCCI & direction
                break;

            case FOUR:
                // logic to calculate new index base on Hexagon's creatorCCI & direction
                break;

            case FIVE:
                // logic to calculate new index base on Hexagon's creatorCCI & direction
                break;

            case SIX:
                // logic to calculate new index base on Hexagon's creatorCCI & direction
                break;
        }
    }

    public String getIndex() {
        return String.format("(q=%s, r=%s, s=%s)", this.q, this.r, this.s); 
    }
}
