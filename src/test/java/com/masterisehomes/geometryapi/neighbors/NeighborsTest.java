package com.masterisehomes.geometryapi.neighbors;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        public void rootCentroid_EQUALS_inputCentroid() {
                Coordinates neighborsCentroid = neighbors.getRootHexagon().getCentroid();
                assertTrue(neighborsCentroid.equals(centroid));
        }

        @Test
        public void COS_30_DEG__EQUALS__SQRT3_DIVIDE_2() {
                final double COS_30_DEG = Math.cos(Math.toRadians(30));
                final double SQRT3_DIVIDE_2 = Math.sqrt(3);

                final double THRESHOLD = 0.000001;
                final double difference = COS_30_DEG - SQRT3_DIVIDE_2;

                assertTrue(difference < THRESHOLD);
        }

        @Test
        public void generatePNthCentroid_EQUALS_generateCentroids() {
                final Coordinates CENTROID = hexagon.getCentroid();
                final double INRADIUS = hexagon.getInradius();
                
                final List<Coordinates> neighborsCentroids = neighbors.getCentroids();

                // Centroids
                final Coordinates CENTROID_1 = neighborsCentroids.get(1);
                final Coordinates CENTROID_2 = neighborsCentroids.get(2);
                final Coordinates CENTROID_3 = neighborsCentroids.get(3);
                final Coordinates CENTROID_4 = neighborsCentroids.get(4);
                final Coordinates CENTROID_5 = neighborsCentroids.get(5);
                final Coordinates CENTROID_6 = neighborsCentroids.get(6);

                // PNth Centroids
                final Coordinates P_CENTROID_1 = Neighbors.generateP1Centroid(CENTROID, INRADIUS);
                final Coordinates P_CENTROID_2 = Neighbors.generateP2Centroid(CENTROID, INRADIUS);
                final Coordinates P_CENTROID_3 = Neighbors.generateP3Centroid(CENTROID, INRADIUS);
                final Coordinates P_CENTROID_4 = Neighbors.generateP4Centroid(CENTROID, INRADIUS);
                final Coordinates P_CENTROID_5 = Neighbors.generateP5Centroid(CENTROID, INRADIUS);
                final Coordinates P_CENTROID_6 = Neighbors.generateP6Centroid(CENTROID, INRADIUS);

                assertAll("Compare Centroids to PNthCentroids",
                        () -> assertTrue(CENTROID_1.equals(P_CENTROID_1)),
                        () -> assertTrue(CENTROID_2.equals(P_CENTROID_2)),
                        () -> assertTrue(CENTROID_3.equals(P_CENTROID_3)),
                        () -> assertTrue(CENTROID_4.equals(P_CENTROID_4)),
                        () -> assertTrue(CENTROID_5.equals(P_CENTROID_5)),
                        () -> assertTrue(CENTROID_6.equals(P_CENTROID_6))
                );
        }
}
