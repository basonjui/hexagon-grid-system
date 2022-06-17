# Geometry API

About the API...
- Purpose
- Goal
- Architecture
- Dependencies

## About Geometry API v0.6 releases

This is a major release, it is an initial version of a ready-to-scale local API that computes Hexagon's coordinates and return data in GeoJSON format.

### API input
The `/api/hexagon` route takes in the following parameters (sample geographic coordinates, `radius` is randomed though): 
```json
{"latitude": 108.28125, "longitude": 65.94647177615738, "radius": 2}
```

### API output
It then parse the JSON data into the program, then computes a Hexagon's coordinates of each vertex and returns all the vertices in a full valid GeoJSON data output:
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
