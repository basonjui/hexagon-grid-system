# Geometry API

Geometry API is a Microservice API that takes in WGS84 coordinates (projected) and a radius parameters, which then will be used as the input centroid and radius for the to-be-generated hexagons inside the program. 

The program may use the provided input to generate a Hexagon, a Hexagon with 6 Neighbors, or a full Tessellation of Hexagons for a specific chosen boundary (not yet implemented).

Finally, this microservice will return data in GeoJSON format - which is implemented following the The GeoJSON Specification (RFC 7946).
https://datatracker.ietf.org/doc/html/rfc7946

## Architecture

### UML package diagram
![Geometry API - Package UML (1)](https://user-images.githubusercontent.com/60636087/181493724-9a59b863-7264-4930-99dd-2d8e0f6a5363.png)

## Dependencies


## About Geometry API v0.6 releases

This is a major release, it is an initial version of a ready-to-scale local API that computes Hexagon's coordinates and return data in GeoJSON format.


## Installation (example)

Use the package manager [pip](https://pip.pypa.io/en/stable/) to install foobar.

```bash
pip install foobar
```


## Usage
AWS API Gateway:
- https://fo65waqg5i.execute-api.ap-southeast-1.amazonaws.com/hexagon
- https://fo65waqg5i.execute-api.ap-southeast-1.amazonaws.com/neighbors

### Request payload
The `/api/hexagon` route takes in the following parameters (sample geographic coordinates, `radius` is randomed though): 
```json
{"latitude": 108.28125, "longitude": 65.94647177615738, "radius": 2}
```


### Response
geometryapi then parses the JSON data into the program, then computes a Hexagon's coordinates of each vertex and returns all the vertices in a full valid GeoJSON data output:
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
              107.28125,
              64.2144209685885
            ],
            [
              109.28125,
              64.2144209685885
            ],
            [
              110.28125,
              65.94647177615738
            ],
            [
              109.28125,
              67.67852258372626
            ],
            [
              107.28125,
              67.67852258372626
            ],
            [
              106.28125,
              65.94647177615738
            ],
            [
              107.28125,
              64.2144209685885
            ]
          ]
        ]
      },
      "properties": {}
    }
  ]
}
```

You can test the above generated hexagon data output on https://geojson.io/.
