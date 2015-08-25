# Lunch

The server for Lunch, a Tinder-like app to find food in your area.


### Endpoint descriptions

- `/search`: Searches for locations in a given location.
    - Query params:
        - `latitude`: String. Defaults to "40.7529081"
        - `longitude`: String. Defaults to "-73.9739277"
        - `width` and `height`: Integer. Optional params that can be used to obtain a `pictureUrlCropped`. Both must be specified, or this field will be left null.
        - `minTier` and `maxTier`: Integer. Optional params that can set a minimum or maximum price tier (from 1 to 4 dollar signs). User can specify both, one, or neither.

    - Return:
        - JSON containing a single array, `venues`, with all of the individual venues.
        - See [sample-search.json](samples/sample-search.json) for the API call: `/search?width=600&height=800`.
