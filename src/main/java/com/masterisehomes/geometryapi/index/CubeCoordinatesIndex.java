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
        // Flat-top orientation of Hexagon
        
        // For every Hexagonal Direction, 2 elements of set {q, r, s} will +/- 1
        switch (direction) {
            case ZERO: // default case - where previousCCI == null & direction == 0
                this.q = 0;
                this.r = 0;
                this.s = 0;
                break;

            case ONE:
                // s+, r-
                this.q = previousCCI.getQ();
                this.r = previousCCI.getR() - 1;
                this.s = previousCCI.getS() + 1;
                break;
                
            case TWO:
                // q+, r-
                this.q = previousCCI.getQ() + 1;
                this.r = previousCCI.getR() - 1;
                this.s = previousCCI.getS();
                break;

            case THREE:
                // q+, s-
                this.q = previousCCI.getQ() + 1;
                this.r = previousCCI.getR();
                this.s = previousCCI.getS() - 1;
                break;

            case FOUR:
                // r+, s-
                this.q = previousCCI.getQ();
                this.r = previousCCI.getR() + 1;
                this.s = previousCCI.getS() - 1;
                break;

            case FIVE:
                // q-, r+
                this.q = previousCCI.getQ() - 1;
                this.r = previousCCI.getR() + 1;
                this.s = previousCCI.getS();
                break;

            case SIX:
                // q-, s+
                this.q = previousCCI.getQ() - 1;
                this.r = previousCCI.getR();
                this.s = previousCCI.getS() + 1;
                break;
        }
    }

    public String getIndex() {
        return String.format("(q=%s, r=%s, s=%s)", this.q, this.r, this.s); 
    }
}
