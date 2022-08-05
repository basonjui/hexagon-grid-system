package com.masterisehomes.geometryapi.neighbors;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;

/**
 * Unit test for simple App.
 */
public class NeighborsTest {
        /**
         * Rigorous Test :-)
         */
        private Coordinates centroid = new Coordinates(106.7021262, 10.7745382);
        private Hexagon hexagon = new Hexagon(centroid, 500);
        private Neighbors neighbors = new Neighbors(hexagon);
        
        @Test
        public void rootCentroid_equals_inputCentroid() {
                assertTrue(neighbors.getRootHexagon().getCentroid().equals(centroid));
        }
}
