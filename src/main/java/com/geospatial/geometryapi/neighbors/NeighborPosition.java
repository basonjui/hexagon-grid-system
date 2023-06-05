package com.geospatial.geometryapi.neighbors;

public enum NeighborPosition {
	/* The order is important, since we use its ordinal value */
	ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX;

	public int value() {
		return this.ordinal();
	}
}