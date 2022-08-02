package com.masterisehomes.geometryapi.neighbors;

public enum NeighborPosition {
	ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX;

	public static void main(String[] args) {
		for (NeighborPosition position : NeighborPosition.values()) {
			System.out.println(position + ": " + position.ordinal());
		}
	}
}