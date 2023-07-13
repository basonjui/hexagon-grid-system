# Hexagon Grid System

![GitHub](https://img.shields.io/github/license/basonjui/hexagon-grid-system)
![GitHub repo size](https://img.shields.io/github/repo-size/basonjui/hexagon-grid-system)
![GitHub release (with filter)](https://img.shields.io/github/v/release/basonjui/hexagon-grid-system)
![Maven Central - spark-core](https://img.shields.io/maven-central/v/com.sparkjava/spark-core?versionSuffix=2.9.4&label=spark-core)
![Maven Central - postgresql](https://img.shields.io/maven-central/v/org.postgresql/postgresql?versionSuffix=42.6.0&label=postgresql)

![Screenshot 2023-07-08 at 6 36 16 PM](https://github.com/basonjui/hexagon-grid-system/assets/60636087/8222111b-f5ae-44d7-b1e3-931f3e788295)
*Hexagon grid generated at Vincom Dong Khoi, Ho Chi Minh (circumradius=5000 meters)*

Hexagon Grid System is an API that takes in a pair of WGS84 coordinates (longitude, latitude) and a hexagon's radius parameters to produce one of the several patterns of a hexagonal grid below:

1. Hexagon - a single regular hexagon.
2. Neighbors - a regular hexagon and its 6 nearest neighbors (a group of 7 hexagons).
3. Tessellation - a tiling of regular hexagons over a geographic boundary without gaps or overlaps.

The API can return geospatial data response in GeoJSON ([RFC 7946](https://datatracker.ietf.org/doc/html/rfc7946)) or save the generated geospatial data directly into PostgreSQL database in ([geometries](http://postgis.net/workshops/postgis-intro/geometries.html)) data formats (supported by PostGIS).

The generated hexagonal grid can then be used for various purposes in geospatial computing such as visualization, analytics, and data aggregation.

## Main Concepts

### Cube Coordinate Index (CCI)

The `CubeCoordinatesIndex` class was inspired by Red Blob Game's - [Cube Coordinates](https://www.redblobgames.com/grids/hexagons/#coordinates-cube) concept.

Basically, it divides the hexagonal grid into 3 primary axes (q, r, s) and assigns a unique index (CCI) for each hexagon based on its position within the grid system.

<!-- ![Cube Coordinates - Primary Axes](https://github.com/basonjui/hexagon-grid-system/assets/60636087/6f6bf2b0-f9b4-446a-8640-4f95c96cfd11) -->

<p align="center">
    <img src="https://github.com/basonjui/hexagon-grid-system/assets/60636087/6f6bf2b0-f9b4-446a-8640-4f95c96cfd11" width="50%" height="50%">
</p>

#### Algorithm

1. Each direction on the hex grid is a combination of two directions on the cube grid. For example, north on the hex grid lies between the `+s` and `-r`, so every step north involves adding 1 to s and subtracting 1 from `r`.

    ![Cube Coordinates - Directions](https://github.com/basonjui/hexagon-grid-system/assets/60636087/e003a907-a090-47b4-9da4-17b5ae7fa791)

2. `q + r + s = 0` - the constraint of this coordinate system to preserve its algorithms.

### Hexagon

A regular hexagon is a polygon with 6 equal-length edges (or sides) and six vertices (corners).

* sides also equals the circumradius of the hexagon.

The class `Hexagon` has many properties to store a hexagon's information. Some of the important properties are:

* `centroid`: the center of the hexagon, which represents a pair of WGS84 coordinates (longitude, latitude).
* `circumradius`: the radius of the circumcircle, which is the radius of the circle that passes through all of the vertices of the hexagon.
* `inradius`: the radius of the incircle, which is the radius of the circle that is tangent to each of the sides of the hexagon.
& `vertices`: the 6 vertices (Coordinates) of the hexagon.
* `CCI`: the Cube Coordinates Index of the hexagon in the grid system defined by `CubeCoordinatesIndex` class.

### Neighbors

Given a hexagon, which 6 hexagons are neighboring it?

* The answer is the 6 hexagons that share an edge with it.

In Hexagon Grid System, `Neighbors` is a group of 7 adjacent regular hexagons - the origin hexagon itself and its 6 nearest neighbors.

### Tessellation (regular)

A **tessellation** or **tiling** is the covering of a surface, often a plane, using one or more geometric shapes, called tiles, with no overlaps and no gaps.

In grid systems, the type of tessellation being used is Regular Tessellation - a highly symmetric tessellation made up of congruent regular polygons. Only three regular tessellations exist: those made up of equilateral **triangles**, **squares**, or **hexagons**.

In Hexagon Grid System, we use regular tessellation to tile over a specified geographic `Boundary`.

#### Sample `Boundary` structure in hexagon-grid-system

```json
{
    "boundary": {
        "minLatitude": 10.8163465,
        "minLongitude": 106.661921,
        "maxLatitude": 10.731605,
        "maxLongitude": 106.725970
    }
}
```

#### Algorithm

The tessellation algorithm in Hexagon Grid System is called `CornerEdgeTessellation`, which breaks down the hexagonal grid into 3 important components that can be used as variables for the tessellation algorithm: **Corner**, **Edge**, and **Rings**.

**Rings** are the "hollow-rings" of hexagons wrapped around the center hexagon of the grid (the origin) to form a tessellation. The rings are used to calculate & define the extent of the hexagon grid that is required to fully cover a specific boundary (based on its coverage distance in meters).

<p align="center">
    <img src="https://github.com/basonjui/hexagon-grid-system/assets/60636087/83c06f0c-55fb-4dab-b8ac-7420672f0ad0">
</p>

Next, the algorithm relies on the linear relationship between the **Corner** and **Edge** with respects to the current Ring of the tessellation, to create a tessellation (by filling up the grid iteratively, one Ring at a time).

For each Ring of the tessellation, the algorithm will:

1. Generate 6 Corner hexagons
2. Calculate the required number (n) of Edge hexagons to fill up between the Corner hexagons
3. Generate n Edge hexagons (to form a complete Ring)

![Tessellation - Corner & Edge](https://github.com/basonjui/hexagon-grid-system/assets/60636087/1c57e385-43c4-4fe2-98e9-a95a10722783)

Details of the algorithm are explained within the source code of the CornerEdgeTessellation class.

## Installation

### Set up environment variables (optional)

This is only required when you want to save Tessellation data into your PostgreSQL database (using the endpoint `/database/tessellation`).

To set up your environment variables, create a `.env` file in the root directory (`../hexagongrid`) of the project and add the following variables:

```text
POSTGRES_HOST=
POSTGRES_USERNAME=
POSTGRES_PASSWORD=
POSTGRES_DATABASE=
```

### Install the project using Maven

In the root directory (`../hexagongrid`) of the project, run the following commands to build the project:

1. Clean the `/target` directory, build the project, package it into a JAR file, and install the JAR file into your local Maven repository

    ```console
    mvn clean package && mvn clean install
    ```

2. Run hexagon-grid-system

    ```console
    java -cp target/hexagongrid-1.1.3.jar com.geospatial.hexagongrid.Api
    ```

## API Usages

Hexagon Grid System is currently being implemented as a simple API that can be used to generate hexagon grid data in GeoJSON format given some valid grid configurations in the form of a request payload. You can directly test the API's GeoJSON data response on <https://geojson.io/>.

### /api/hexagon

#### Request

```json
{
    "latitude": 10.7745419,
    "longitude": 106.7018471,
    "radius": 250
}
```

#### Response

The JSON is prettified to help you visualize the GeoJSON structure of a single hexagon.

```json
{
    "type": "FeatureCollection",
    "features": [
        {
            "type": "Feature",
            "geometry": {
                "type": "Polygon",
                "coordinates": [
                    [
                        [
                            106.70072420589484,
                            10.772596990358737
                        ],
                        [
                            106.70296999410515,
                            10.772596990358737
                        ],
                        [
                            106.7040928882103,
                            10.7745419
                        ],
                        [
                            106.70296999410515,
                            10.776486809641261
                        ],
                        [
                            106.70072420589484,
                            10.776486809641261
                        ],
                        [
                            106.69960131178969,
                            10.7745419
                        ],
                        [
                            106.70072420589484,
                            10.772596990358737
                        ]
                    ]
                ]
            },
            "properties": {
                "ccid": {
                    "q": 0,
                    "r": 0,
                    "s": 0
                },
                "centroid": {
                    "longitude": 106.7018471,
                    "latitude": 10.7745419
                },
                "circumradius": 250.0,
                "inradius": 216.50635094610965
            }
        }
    ]
}
```

### /api/neighbors

#### Request

```json
{
    "latitude": 10.7745419,
    "longitude": 106.7018471,
    "radius": 250
}
```

#### Response

JSON is compacted to reduce file size.

```json
{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.70072420589484,10.772596990358737],[106.70296999410515,10.772596990358737],[106.7040928882103,10.7745419],[106.70296999410515,10.776486809641261],[106.70072420589484,10.776486809641261],[106.69960131178969,10.7745419],[106.70072420589484,10.772596990358737]]]},"properties":{"ccid":{"q":0,"r":0,"s":0},"centroid":{"longitude":106.7018471,"latitude":10.7745419},"circumradius":250.0,"inradius":216.50635094610965}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.70072420589484,10.768707171076214],[106.70296999410515,10.768707171076214],[106.7040928882103,10.770652080717475],[106.70296999410515,10.772596990358737],[106.70072420589484,10.772596990358737],[106.69960131178969,10.770652080717475],[106.70072420589484,10.768707171076214]]]},"properties":{"ccid":{"q":0,"r":-1,"s":1},"centroid":{"longitude":106.7018471,"latitude":10.770652080717475},"circumradius":250.0,"inradius":216.50635094610965}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.7040928882103,10.770652080717475],[106.7063386764206,10.770652080717475],[106.70746157052575,10.772596990358737],[106.7063386764206,10.7745419],[106.7040928882103,10.7745419],[106.70296999410515,10.772596990358737],[106.7040928882103,10.770652080717475]]]},"properties":{"ccid":{"q":1,"r":-1,"s":0},"centroid":{"longitude":106.70521578231545,"latitude":10.772596990358737},"circumradius":250.0,"inradius":216.50635094610965}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.7040928882103,10.7745419],[106.7063386764206,10.7745419],[106.70746157052575,10.776486809641261],[106.7063386764206,10.778431719282523],[106.7040928882103,10.778431719282523],[106.70296999410515,10.776486809641261],[106.7040928882103,10.7745419]]]},"properties":{"ccid":{"q":1,"r":0,"s":-1},"centroid":{"longitude":106.70521578231545,"latitude":10.776486809641261},"circumradius":250.0,"inradius":216.50635094610965}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.70072420589484,10.776486809641261],[106.70296999410515,10.776486809641261],[106.7040928882103,10.778431719282523],[106.70296999410515,10.780376628923785],[106.70072420589484,10.780376628923785],[106.69960131178969,10.778431719282523],[106.70072420589484,10.776486809641261]]]},"properties":{"ccid":{"q":0,"r":1,"s":-1},"centroid":{"longitude":106.7018471,"latitude":10.778431719282523},"circumradius":250.0,"inradius":216.50635094610965}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.69735552357939,10.7745419],[106.69960131178969,10.7745419],[106.70072420589484,10.776486809641261],[106.69960131178969,10.778431719282523],[106.69735552357939,10.778431719282523],[106.69623262947424,10.776486809641261],[106.69735552357939,10.7745419]]]},"properties":{"ccid":{"q":-1,"r":1,"s":0},"centroid":{"longitude":106.69847841768454,"latitude":10.776486809641261},"circumradius":250.0,"inradius":216.50635094610965}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.69735552357939,10.770652080717475],[106.69960131178969,10.770652080717475],[106.70072420589484,10.772596990358737],[106.69960131178969,10.7745419],[106.69735552357939,10.7745419],[106.69623262947424,10.772596990358737],[106.69735552357939,10.770652080717475]]]},"properties":{"ccid":{"q":-1,"r":0,"s":1},"centroid":{"longitude":106.69847841768454,"latitude":10.772596990358737},"circumradius":250.0,"inradius":216.50635094610965}}]}
```

### /api/tessellation

#### Request

```json
{
    "latitude": 10.7755,
    "longitude": 106.7021,
    "radius": 5000,
    "boundary": {
        "minLatitude": 10.8163465,
        "minLongitude": 106.661921,
        "maxLatitude": 10.731605,
        "maxLongitude": 106.725970
    }
}
```

#### Response

JSON is compacted to reduce file size.

```json
{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.736601810155294],[106.72455788210299,10.736601810155294],[106.74701576420598,10.7755],[106.72455788210299,10.814398189844704],[106.67964211789702,10.814398189844704],[106.65718423579402,10.7755],[106.67964211789702,10.736601810155294]]]},"properties":{"ccid":{"q":0,"r":0,"s":0},"centroid":{"longitude":106.7021,"latitude":10.7755},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.658805430465884],[106.72455788210299,10.658805430465884],[106.74701576420598,10.69770362031059],[106.72455788210299,10.736601810155294],[106.67964211789702,10.736601810155294],[106.65718423579402,10.69770362031059],[106.67964211789702,10.658805430465884]]]},"properties":{"ccid":{"q":0,"r":-1,"s":1},"centroid":{"longitude":106.7021,"latitude":10.69770362031059},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.74701576420598,10.69770362031059],[106.79193152841195,10.69770362031059],[106.81438941051495,10.736601810155294],[106.79193152841195,10.7755],[106.74701576420598,10.7755],[106.72455788210299,10.736601810155294],[106.74701576420598,10.69770362031059]]]},"properties":{"ccid":{"q":1,"r":-1,"s":0},"centroid":{"longitude":106.76947364630897,"latitude":10.736601810155294},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.74701576420598,10.7755],[106.79193152841195,10.7755],[106.81438941051495,10.814398189844704],[106.79193152841195,10.853296379689409],[106.74701576420598,10.853296379689409],[106.72455788210299,10.814398189844704],[106.74701576420598,10.7755]]]},"properties":{"ccid":{"q":1,"r":0,"s":-1},"centroid":{"longitude":106.76947364630897,"latitude":10.814398189844704},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.814398189844704],[106.72455788210299,10.814398189844704],[106.74701576420598,10.853296379689409],[106.72455788210299,10.892194569534114],[106.67964211789702,10.892194569534114],[106.65718423579402,10.853296379689409],[106.67964211789702,10.814398189844704]]]},"properties":{"ccid":{"q":0,"r":1,"s":-1},"centroid":{"longitude":106.7021,"latitude":10.853296379689409},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.61226847158805,10.7755],[106.65718423579402,10.7755],[106.67964211789702,10.814398189844704],[106.65718423579402,10.853296379689409],[106.61226847158805,10.853296379689409],[106.58981058948505,10.814398189844704],[106.61226847158805,10.7755]]]},"properties":{"ccid":{"q":-1,"r":1,"s":0},"centroid":{"longitude":106.63472635369104,"latitude":10.814398189844704},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.61226847158805,10.69770362031059],[106.65718423579402,10.69770362031059],[106.67964211789702,10.736601810155294],[106.65718423579402,10.7755],[106.61226847158805,10.7755],[106.58981058948505,10.736601810155294],[106.61226847158805,10.69770362031059]]]},"properties":{"ccid":{"q":-1,"r":0,"s":1},"centroid":{"longitude":106.63472635369104,"latitude":10.736601810155294},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.581009050776474],[106.72455788210299,10.581009050776474],[106.74701576420598,10.61990724062118],[106.72455788210299,10.658805430465884],[106.67964211789702,10.658805430465884],[106.65718423579402,10.61990724062118],[106.67964211789702,10.581009050776474]]]},"properties":{"ccid":{"q":0,"r":-2,"s":2},"centroid":{"longitude":106.7021,"latitude":10.61990724062118},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.74701576420598,10.61990724062118],[106.79193152841195,10.61990724062118],[106.81438941051495,10.658805430465884],[106.79193152841195,10.69770362031059],[106.74701576420598,10.69770362031059],[106.72455788210299,10.658805430465884],[106.74701576420598,10.61990724062118]]]},"properties":{"ccid":{"q":1,"r":-2,"s":1},"centroid":{"longitude":106.76947364630897,"latitude":10.658805430465884},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.81438941051495,10.658805430465884],[106.85930517472092,10.658805430465884],[106.88176305682391,10.69770362031059],[106.85930517472092,10.736601810155294],[106.81438941051495,10.736601810155294],[106.79193152841195,10.69770362031059],[106.81438941051495,10.658805430465884]]]},"properties":{"ccid":{"q":2,"r":-2,"s":0},"centroid":{"longitude":106.83684729261793,"latitude":10.69770362031059},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.81438941051495,10.736601810155294],[106.85930517472092,10.736601810155294],[106.88176305682391,10.7755],[106.85930517472092,10.814398189844704],[106.81438941051495,10.814398189844704],[106.79193152841195,10.7755],[106.81438941051495,10.736601810155294]]]},"properties":{"ccid":{"q":2,"r":-1,"s":-1},"centroid":{"longitude":106.83684729261793,"latitude":10.7755},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.81438941051495,10.814398189844704],[106.85930517472092,10.814398189844704],[106.88176305682391,10.853296379689409],[106.85930517472092,10.892194569534114],[106.81438941051495,10.892194569534114],[106.79193152841195,10.853296379689409],[106.81438941051495,10.814398189844704]]]},"properties":{"ccid":{"q":2,"r":0,"s":-2},"centroid":{"longitude":106.83684729261793,"latitude":10.853296379689409},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.74701576420598,10.853296379689409],[106.79193152841195,10.853296379689409],[106.81438941051495,10.892194569534114],[106.79193152841195,10.931092759378819],[106.74701576420598,10.931092759378819],[106.72455788210299,10.892194569534114],[106.74701576420598,10.853296379689409]]]},"properties":{"ccid":{"q":1,"r":1,"s":-2},"centroid":{"longitude":106.76947364630897,"latitude":10.892194569534114},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.892194569534114],[106.72455788210299,10.892194569534114],[106.74701576420598,10.931092759378819],[106.72455788210299,10.969990949223524],[106.67964211789702,10.969990949223524],[106.65718423579402,10.931092759378819],[106.67964211789702,10.892194569534114]]]},"properties":{"ccid":{"q":0,"r":2,"s":-2},"centroid":{"longitude":106.7021,"latitude":10.931092759378819},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.61226847158805,10.853296379689409],[106.65718423579402,10.853296379689409],[106.67964211789702,10.892194569534114],[106.65718423579402,10.931092759378819],[106.61226847158805,10.931092759378819],[106.58981058948505,10.892194569534114],[106.61226847158805,10.853296379689409]]]},"properties":{"ccid":{"q":-1,"r":2,"s":-1},"centroid":{"longitude":106.63472635369104,"latitude":10.892194569534114},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.54489482527909,10.814398189844704],[106.58981058948505,10.814398189844704],[106.61226847158805,10.853296379689409],[106.58981058948505,10.892194569534114],[106.54489482527909,10.892194569534114],[106.52243694317609,10.853296379689409],[106.54489482527909,10.814398189844704]]]},"properties":{"ccid":{"q":-2,"r":2,"s":0},"centroid":{"longitude":106.56735270738207,"latitude":10.853296379689409},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.54489482527909,10.736601810155294],[106.58981058948505,10.736601810155294],[106.61226847158805,10.7755],[106.58981058948505,10.814398189844704],[106.54489482527909,10.814398189844704],[106.52243694317609,10.7755],[106.54489482527909,10.736601810155294]]]},"properties":{"ccid":{"q":-2,"r":1,"s":1},"centroid":{"longitude":106.56735270738207,"latitude":10.7755},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.54489482527909,10.658805430465884],[106.58981058948505,10.658805430465884],[106.61226847158805,10.69770362031059],[106.58981058948505,10.736601810155294],[106.54489482527909,10.736601810155294],[106.52243694317609,10.69770362031059],[106.54489482527909,10.658805430465884]]]},"properties":{"ccid":{"q":-2,"r":0,"s":2},"centroid":{"longitude":106.56735270738207,"latitude":10.69770362031059},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.61226847158805,10.61990724062118],[106.65718423579402,10.61990724062118],[106.67964211789702,10.658805430465884],[106.65718423579402,10.69770362031059],[106.61226847158805,10.69770362031059],[106.58981058948505,10.658805430465884],[106.61226847158805,10.61990724062118]]]},"properties":{"ccid":{"q":-1,"r":-1,"s":2},"centroid":{"longitude":106.63472635369104,"latitude":10.658805430465884},"circumradius":5000.0,"inradius":4330.127018922193}}]}
```

### /database/tessellation

#### Request

```json
{   
    "administrativeName": "hexagongrid_local_test",
    "latitude": 10.7755,
    "longitude": 106.7021,
    "radius": 1000,
    "boundary": {
        "minLatitude": 11.176299,
        "minLongitude": 106.322334,
        "maxLatitude": 10.391811,
        "maxLongitude": 107.036011
    }
}
```

#### Response

```json
{
    "createTessellationTable": {
        "status": "SUCCESS"
    },
    "batchInsertTessellation": {
        "status": "SUCCESS",
        "message": {
            "tableName": "hexagongrid_local_test_tessellation_1000m",
            "totalHexagons": 5419,
            "totalBatchExecutions": 2,
            "elapsedSeconds": 0.672,
            "rowsPerBatch": 5000,
            "rowsInserted": 5419
        }
    },
    "addPrimaryKeyIfNotExists": {
        "status": "SUCCESS",
        "message": "PRIMARY KEY 'hexagongrid_local_test_tessellation_1000m_pkey' added to table 'hexagongrid_local_test_tessellation_1000m'."
    }
}
```

### Database schema (PostGIS)

[PostGIS](http://postgis.net) extends the capabilities of the PostgreSQL relational database by adding support storing, indexing and querying geographic data.

Below is the current schema implementation of Tessellation tables generated from the endpoint `/database/tessellation`.

#### SQL (DDL)

```sql
CREATE TABLE IF NOT EXISTS %s (
        ccid_q          integer                 NOT NULL,
        ccid_r          integer                 NOT NULL,
        ccid_s          integer                 NOT NULL,
        circumradius    float8                  NOT NULL,
        centroid        geometry(POINT, 4326)   NOT NULL,
        geometry        geometry(POLYGON, 4326) NOT NULL
);
```

#### Sample

##### [Well-Known Binary (WKB)](https://libgeos.org/specifications/wkb/) - Default

|ccid_q|ccid_r|ccid_s|circumradius|centroid                                          |geometry                                                                                                                                                                                                                                                          |
|------|------|------|------------|--------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|0     |0     |0     |1000        |0101000020E610000062A1D634EFAC5A40931804560E8D2540|0103000020E61000000100000007000000032CCD9DA5AC5A407CDDA9A412892540C116E0CB38AD5A407CDDA9A412892540218CE96282AD5A40931804560E8D2540C116E0CB38AD5A40AA535E070A912540032CCD9DA5AC5A40AA535E070A912540A3B6C3065CAC5A40931804560E8D2540032CCD9DA5AC5A407CDDA9A412892540|
|0     |-1    |1     |1000        |0101000020E610000062A1D634EFAC5A4064A24FF316852540|0103000020E61000000100000007000000032CCD9DA5AC5A404D67F5411B812540C116E0CB38AD5A404D67F5411B812540218CE96282AD5A4064A24FF316852540C116E0CB38AD5A407BDDA9A412892540032CCD9DA5AC5A407BDDA9A412892540A3B6C3065CAC5A4064A24FF316852540032CCD9DA5AC5A404D67F5411B812540|
|1     |-1    |0     |1000        |0101000020E61000008001F3F9CBAD5A407CDDA9A412892540|0103000020E61000000100000007000000218CE96282AD5A4065A24FF316852540DF76FC9015AE5A4065A24FF3168525403FEC05285FAE5A407CDDA9A412892540DF76FC9015AE5A40931804560E8D2540218CE96282AD5A40931804560E8D2540C116E0CB38AD5A407CDDA9A412892540218CE96282AD5A4065A24FF316852540|
|1     |0     |-1    |1000        |0101000020E61000008001F3F9CBAD5A40AA535E070A912540|0103000020E61000000100000007000000218CE96282AD5A40931804560E8D2540DF76FC9015AE5A40931804560E8D25403FEC05285FAE5A40AA535E070A912540DF76FC9015AE5A40C18EB8B805952540218CE96282AD5A40C18EB8B805952540C116E0CB38AD5A40AA535E070A912540218CE96282AD5A40931804560E8D2540|

##### [Well-Known Text (WKT)](https://libgeos.org/specifications/wkt/)

You can convert geometry columns to the WKT format using PostGIS's spatial function `ST_AsText`

|ccid_q|ccid_r|ccid_s|centroid_wkt                             |geometry_wkt                                                                                                                                                                                                                                                                   |
|------|------|------|--------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|0     |0     |0     |POINT(106.7021 10.7755)                     |POLYGON((106.69760842357941 10.767720361457343,106.70659157642059 10.767720361457343,106.7110831528412 10.7755,106.70659157642059 10.783279638542655,106.69760842357941 10.783279638542655,106.6931168471588 10.7755,106.69760842357941 10.767720361457343))                      |
|0     |-1    |1     |POINT(106.7021 10.759940722914685)          |POLYGON((106.69760842357941 10.752161084372029,106.70659157642059 10.752161084372029,106.7110831528412 10.759940722914685,106.70659157642059 10.767720361457341,106.69760842357941 10.767720361457341,106.6931168471588 10.759940722914685,106.69760842357941 10.752161084372029))|
|1     |-1    |0     |POINT(106.71557472926179 10.767720361457343)|POLYGON((106.7110831528412 10.759940722914687,106.72006630568238 10.759940722914687,106.72455788210299 10.767720361457343,106.72006630568238 10.7755,106.7110831528412 10.7755,106.70659157642059 10.767720361457343,106.7110831528412 10.759940722914687))                       |
|1     |0     |-1    |POINT(106.71557472926179 10.783279638542655)|POLYGON((106.7110831528412 10.7755,106.72006630568238 10.7755,106.72455788210299 10.783279638542655,106.72006630568238 10.791059277085312,106.7110831528412 10.791059277085312,106.70659157642059 10.783279638542655,106.7110831528412 10.7755))                                  |
