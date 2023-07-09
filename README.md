# GeometryApi

![Screenshot 2023-07-08 at 6 36 16 PM](https://github.com/basonjui/geometryapi/assets/60636087/8222111b-f5ae-44d7-b1e3-931f3e788295)

*Tessellation at Vincom Dong Khoi, radius (of the hexagon) = 5000 meters.*

GeometryApi is a Hexagonal Grid System API that takes in a pair of WGS84 coordinates (longitude, latitude) and a hexagon's radius parameters to produce one of the below patterns of hexagonal grids:

1. Hexagon - a single regular hexagon.
2. Neighbors - a group of 7 adjacent regular hexagons.
3. Tessellation - a grid of regular hexagons that tile over a Boundary.

The API returns geospatial data output in GeoJSON ([RFC 7946](https://datatracker.ietf.org/doc/html/rfc7946)) or PostGIS ([geometries](http://postgis.net/workshops/postgis-intro/geometries.html)) data formats, which can be used for multiple purposes in geospatial computing such as visualization, analytics, and data aggregation.

## Main Concepts

### Cube Coordinate Index

The `CubeCoordinatesIndex` class was inspired by Red Blob Game's - [Cube Coordinates](https://www.redblobgames.com/grids/hexagons/#coordinates-cube) concept.

Basically, it divides the hexagonal grid into 3 primary axes (q, r, s) and assigns a unique index (CCI) based on its position within the grid system.

<img width="417" alt="Screenshot 2023-07-09 at 4 51 33 PM" src="https://github.com/basonjui/geometryapi/assets/60636087/6f6bf2b0-f9b4-446a-8640-4f95c96cfd11">

#### How it works

1. Each direction on the hex grid is a combination of two directions on the cube grid. For example, north on the hex grid lies between the `+s` and `-r`, so every step north involves adding 1 to s and subtracting 1 from `r`.

    <img width="534" alt="Screenshot 2023-07-09 at 4 50 09 PM" src="https://github.com/basonjui/geometryapi/assets/60636087/e003a907-a090-47b4-9da4-17b5ae7fa791">

2. `q + r + s = 0` - the constraint of this coordinate system to preserve its algorithmic properties.

### Hexagon

A regular hexagon is a polygon with six edges (sides) of equal length (also equals circumradius) and six vertices (corners).

In geometryapi, the class Hexagon holds several properties, however, the most important ones are:

- `centroid`: the center of the hexagon, which represent a pair of WGS84 coordinates (longitude, latitude).
- `circumradius`: the radius of the circumcircle, which is the radius of the circle that passes through all of the vertices of the hexagon.
- `inradius`: the radius of the incircle, which is the radius of the circle that is tangent to each of the sides of the hexagon.
- `CCI`: the index of the hexagon in the grid system defined by `CubeCoordinatesIndex` class.

### Neighbors

Given a hex, which 6 hexes are neighboring it? The answer is the 6 hexes that share an edge with it.

In geometryapi, `Neighbors` is a group of 7 adjacent regular hexagons - the hexagon itself and its 6 neighbors.

### CornerEdgeTessellation (Tessellation)

Tessellation is the process of creating a two-dimensional plane using the repetition of a geometric shape with no overlaps and no gaps. In geometryapi, tessellation is done by tiling over a specified `Boundary`.

A sample JSON request that specifies the boundary of a Tessellation:

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

The specific algorithm used in geometryapi is named `CornerEdgeTessellation`, which relies on the linear relationship between the Corner and Edge of the hexagonal grid to generate the tessellation. Details of the algorithm is commented in the source code of CornerEdgeTessellation class.

## Installation

### Environment Variables (Optional)

This is only required when you want to save Tessellation data into your PostgreSQL database (using the endpoint `/database/tessellation`).

To set up your environment variables, create a `.env` file in the root directory (`../geometryapi`) of the project and add the following variables:

```text
POSTGRES_HOST=
POSTGRES_USERNAME=
POSTGRES_PASSWORD=
POSTGRES_DATABASE=
```

### Maven

In the root directory (`../geometryapi`) of the project, run the following commands to build the project:

1. Clean the `/target` directory, build the project, and package it into a JAR file, and install the JAR file into your local Maven repository

    ```console
    mvn clean package && mvn clean install
    ```

2. Run geometryapi

    ```console
    java -cp target/geometryapi-1.1.2.jar com.geospatial.geometryapi.App
    ```

## Usages

GeometryApi is a local API that can be used to generate Hexagon's coordinates and return data in GeoJSON format. You can test the GeoJSON data output on <https://geojson.io/>.

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

Raw JSON response (compacted to reduce file size).

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

Raw JSON response (compacted to reduce file size).

```json
{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.736601810155294],[106.72455788210299,10.736601810155294],[106.74701576420598,10.7755],[106.72455788210299,10.814398189844704],[106.67964211789702,10.814398189844704],[106.65718423579402,10.7755],[106.67964211789702,10.736601810155294]]]},"properties":{"ccid":{"q":0,"r":0,"s":0},"centroid":{"longitude":106.7021,"latitude":10.7755},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.658805430465884],[106.72455788210299,10.658805430465884],[106.74701576420598,10.69770362031059],[106.72455788210299,10.736601810155294],[106.67964211789702,10.736601810155294],[106.65718423579402,10.69770362031059],[106.67964211789702,10.658805430465884]]]},"properties":{"ccid":{"q":0,"r":-1,"s":1},"centroid":{"longitude":106.7021,"latitude":10.69770362031059},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.74701576420598,10.69770362031059],[106.79193152841195,10.69770362031059],[106.81438941051495,10.736601810155294],[106.79193152841195,10.7755],[106.74701576420598,10.7755],[106.72455788210299,10.736601810155294],[106.74701576420598,10.69770362031059]]]},"properties":{"ccid":{"q":1,"r":-1,"s":0},"centroid":{"longitude":106.76947364630897,"latitude":10.736601810155294},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.74701576420598,10.7755],[106.79193152841195,10.7755],[106.81438941051495,10.814398189844704],[106.79193152841195,10.853296379689409],[106.74701576420598,10.853296379689409],[106.72455788210299,10.814398189844704],[106.74701576420598,10.7755]]]},"properties":{"ccid":{"q":1,"r":0,"s":-1},"centroid":{"longitude":106.76947364630897,"latitude":10.814398189844704},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.814398189844704],[106.72455788210299,10.814398189844704],[106.74701576420598,10.853296379689409],[106.72455788210299,10.892194569534114],[106.67964211789702,10.892194569534114],[106.65718423579402,10.853296379689409],[106.67964211789702,10.814398189844704]]]},"properties":{"ccid":{"q":0,"r":1,"s":-1},"centroid":{"longitude":106.7021,"latitude":10.853296379689409},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.61226847158805,10.7755],[106.65718423579402,10.7755],[106.67964211789702,10.814398189844704],[106.65718423579402,10.853296379689409],[106.61226847158805,10.853296379689409],[106.58981058948505,10.814398189844704],[106.61226847158805,10.7755]]]},"properties":{"ccid":{"q":-1,"r":1,"s":0},"centroid":{"longitude":106.63472635369104,"latitude":10.814398189844704},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.61226847158805,10.69770362031059],[106.65718423579402,10.69770362031059],[106.67964211789702,10.736601810155294],[106.65718423579402,10.7755],[106.61226847158805,10.7755],[106.58981058948505,10.736601810155294],[106.61226847158805,10.69770362031059]]]},"properties":{"ccid":{"q":-1,"r":0,"s":1},"centroid":{"longitude":106.63472635369104,"latitude":10.736601810155294},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.581009050776474],[106.72455788210299,10.581009050776474],[106.74701576420598,10.61990724062118],[106.72455788210299,10.658805430465884],[106.67964211789702,10.658805430465884],[106.65718423579402,10.61990724062118],[106.67964211789702,10.581009050776474]]]},"properties":{"ccid":{"q":0,"r":-2,"s":2},"centroid":{"longitude":106.7021,"latitude":10.61990724062118},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.74701576420598,10.61990724062118],[106.79193152841195,10.61990724062118],[106.81438941051495,10.658805430465884],[106.79193152841195,10.69770362031059],[106.74701576420598,10.69770362031059],[106.72455788210299,10.658805430465884],[106.74701576420598,10.61990724062118]]]},"properties":{"ccid":{"q":1,"r":-2,"s":1},"centroid":{"longitude":106.76947364630897,"latitude":10.658805430465884},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.81438941051495,10.658805430465884],[106.85930517472092,10.658805430465884],[106.88176305682391,10.69770362031059],[106.85930517472092,10.736601810155294],[106.81438941051495,10.736601810155294],[106.79193152841195,10.69770362031059],[106.81438941051495,10.658805430465884]]]},"properties":{"ccid":{"q":2,"r":-2,"s":0},"centroid":{"longitude":106.83684729261793,"latitude":10.69770362031059},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.81438941051495,10.736601810155294],[106.85930517472092,10.736601810155294],[106.88176305682391,10.7755],[106.85930517472092,10.814398189844704],[106.81438941051495,10.814398189844704],[106.79193152841195,10.7755],[106.81438941051495,10.736601810155294]]]},"properties":{"ccid":{"q":2,"r":-1,"s":-1},"centroid":{"longitude":106.83684729261793,"latitude":10.7755},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.81438941051495,10.814398189844704],[106.85930517472092,10.814398189844704],[106.88176305682391,10.853296379689409],[106.85930517472092,10.892194569534114],[106.81438941051495,10.892194569534114],[106.79193152841195,10.853296379689409],[106.81438941051495,10.814398189844704]]]},"properties":{"ccid":{"q":2,"r":0,"s":-2},"centroid":{"longitude":106.83684729261793,"latitude":10.853296379689409},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.74701576420598,10.853296379689409],[106.79193152841195,10.853296379689409],[106.81438941051495,10.892194569534114],[106.79193152841195,10.931092759378819],[106.74701576420598,10.931092759378819],[106.72455788210299,10.892194569534114],[106.74701576420598,10.853296379689409]]]},"properties":{"ccid":{"q":1,"r":1,"s":-2},"centroid":{"longitude":106.76947364630897,"latitude":10.892194569534114},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.67964211789702,10.892194569534114],[106.72455788210299,10.892194569534114],[106.74701576420598,10.931092759378819],[106.72455788210299,10.969990949223524],[106.67964211789702,10.969990949223524],[106.65718423579402,10.931092759378819],[106.67964211789702,10.892194569534114]]]},"properties":{"ccid":{"q":0,"r":2,"s":-2},"centroid":{"longitude":106.7021,"latitude":10.931092759378819},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.61226847158805,10.853296379689409],[106.65718423579402,10.853296379689409],[106.67964211789702,10.892194569534114],[106.65718423579402,10.931092759378819],[106.61226847158805,10.931092759378819],[106.58981058948505,10.892194569534114],[106.61226847158805,10.853296379689409]]]},"properties":{"ccid":{"q":-1,"r":2,"s":-1},"centroid":{"longitude":106.63472635369104,"latitude":10.892194569534114},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.54489482527909,10.814398189844704],[106.58981058948505,10.814398189844704],[106.61226847158805,10.853296379689409],[106.58981058948505,10.892194569534114],[106.54489482527909,10.892194569534114],[106.52243694317609,10.853296379689409],[106.54489482527909,10.814398189844704]]]},"properties":{"ccid":{"q":-2,"r":2,"s":0},"centroid":{"longitude":106.56735270738207,"latitude":10.853296379689409},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.54489482527909,10.736601810155294],[106.58981058948505,10.736601810155294],[106.61226847158805,10.7755],[106.58981058948505,10.814398189844704],[106.54489482527909,10.814398189844704],[106.52243694317609,10.7755],[106.54489482527909,10.736601810155294]]]},"properties":{"ccid":{"q":-2,"r":1,"s":1},"centroid":{"longitude":106.56735270738207,"latitude":10.7755},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.54489482527909,10.658805430465884],[106.58981058948505,10.658805430465884],[106.61226847158805,10.69770362031059],[106.58981058948505,10.736601810155294],[106.54489482527909,10.736601810155294],[106.52243694317609,10.69770362031059],[106.54489482527909,10.658805430465884]]]},"properties":{"ccid":{"q":-2,"r":0,"s":2},"centroid":{"longitude":106.56735270738207,"latitude":10.69770362031059},"circumradius":5000.0,"inradius":4330.127018922193}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[106.61226847158805,10.61990724062118],[106.65718423579402,10.61990724062118],[106.67964211789702,10.658805430465884],[106.65718423579402,10.69770362031059],[106.61226847158805,10.69770362031059],[106.58981058948505,10.658805430465884],[106.61226847158805,10.61990724062118]]]},"properties":{"ccid":{"q":-1,"r":-1,"s":2},"centroid":{"longitude":106.63472635369104,"latitude":10.658805430465884},"circumradius":5000.0,"inradius":4330.127018922193}}]}
```

### /database/tessellation

#### Request

```json
{   
    "administrativeName": "geometryapi_local_test",
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
            "tableName": "geometryapi_local_test_tessellation_1000m",
            "totalHexagons": 5419,
            "totalBatchExecutions": 2,
            "elapsedSeconds": 0.672,
            "rowsPerBatch": 5000,
            "rowsInserted": 5419
        }
    },
    "addPrimaryKeyIfNotExists": {
        "status": "SUCCESS",
        "message": "PRIMARY KEY 'geometryapi_local_test_tessellation_1000m_pkey' added to table 'geometryapi_local_test_tessellation_1000m'."
    }
}
```
