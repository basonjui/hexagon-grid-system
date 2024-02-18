package com.geospatial.hexagongrid.index;

import java.io.Serializable;
import java.security.InvalidParameterException;

import com.geospatial.hexagongrid.neighbors.NeighborPosition;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class CubeCoordinatesIndex implements Serializable {
	private final int q;
	private final int r;
	private final int s;

	public CubeCoordinatesIndex(CubeCoordinatesIndex previousCCI, NeighborPosition position) {
		/*
		 * Flat-top orientation of Hexagon, order from left-right
		 * For every NeighborPosition, 2 elements of set {q, r, s} will +/- 1
		 */
		switch (position) {
			case ZERO:
				// Position ZERO is ONLY acceptable when previousCCI == null
				if (previousCCI == null) {
					this.q = 0;
					this.r = 0;
					this.s = 0;
				} else {
					throw new IllegalArgumentException(
							"Expect previousCCI to be null, currently: " + previousCCI);
				}
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

			default:
				throw new InvalidParameterException("Invalid NeighborPosition: " + position);
		}
	}
}
