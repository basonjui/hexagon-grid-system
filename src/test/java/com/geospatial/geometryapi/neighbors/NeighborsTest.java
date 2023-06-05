package com.geospatial.geometryapi.neighbors;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.geospatial.geometryapi.hexagon.Coordinates;
import com.geospatial.geometryapi.hexagon.Hexagon;
import com.geospatial.geometryapi.neighbors.Neighbors;

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
                final double THRESHOLD = 0.000001;

                final double difference = Math.abs(
                        Math.cos(Math.toRadians(30)) - (Math.abs(Math.sqrt(3) / 2)));

                assertTrue(difference < THRESHOLD);
        }

        @Test
        public void generatePNthCentroid_EQUALS_generateCentroids() {
                final Coordinates CENTROID = this.centroid;
                final double INRADIUS = hexagon.getInradius();
                
                // Centroids
                final List<Coordinates> neighborsCentroids = neighbors.getCentroids();

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

        @Test
        public void generatePNthGisCentroid_EQUALS_generateGisCentroids() {
                final Coordinates CENTROID = this.centroid;
                final double INRADIUS = hexagon.getInradius();

                // GIS Centroids
                final List<Coordinates> neighborsCentroids = neighbors.getGisCentroids();

                final Coordinates GIS_CENTROID_1 = neighborsCentroids.get(1);
                final Coordinates GIS_CENTROID_2 = neighborsCentroids.get(2);
                final Coordinates GIS_CENTROID_3 = neighborsCentroids.get(3);
                final Coordinates GIS_CENTROID_4 = neighborsCentroids.get(4);
                final Coordinates GIS_CENTROID_5 = neighborsCentroids.get(5);
                final Coordinates GIS_CENTROID_6 = neighborsCentroids.get(6);

                // PNth GIS Centroids
                final Coordinates P_GIS_CENTROID_1 = Neighbors.generateP1GisCentroid(CENTROID, INRADIUS);
                final Coordinates P_GIS_CENTROID_2 = Neighbors.generateP2GisCentroid(CENTROID, INRADIUS);
                final Coordinates P_GIS_CENTROID_3 = Neighbors.generateP3GisCentroid(CENTROID, INRADIUS);
                final Coordinates P_GIS_CENTROID_4 = Neighbors.generateP4GisCentroid(CENTROID, INRADIUS);
                final Coordinates P_GIS_CENTROID_5 = Neighbors.generateP5GisCentroid(CENTROID, INRADIUS);
                final Coordinates P_GIS_CENTROID_6 = Neighbors.generateP6GisCentroid(CENTROID, INRADIUS);

                assertAll("Compare Centroids to PNthCentroids",
                        () -> assertTrue(GIS_CENTROID_1.equals(P_GIS_CENTROID_1)),
                        () -> assertTrue(GIS_CENTROID_2.equals(P_GIS_CENTROID_2)),
                        () -> assertTrue(GIS_CENTROID_3.equals(P_GIS_CENTROID_3)),
                        () -> assertTrue(GIS_CENTROID_4.equals(P_GIS_CENTROID_4)),
                        () -> assertTrue(GIS_CENTROID_5.equals(P_GIS_CENTROID_5)),
                        () -> assertTrue(GIS_CENTROID_6.equals(P_GIS_CENTROID_6))
                );
        }
}
