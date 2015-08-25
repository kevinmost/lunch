# Lunch

The server for Lunch, a Tinder-like app to find food in your area.


### Endpoint descriptions

- `/search`: Searches for locations in a given location.
    - Query params:
        - `latitude`: String. Defaults to "40.7529081"
        - `longitude`: String. Defaults to "-73.9739277"
        - `width`: Integer. Optional param that can be used to obtain a `pictureUrlCropped`.
        - `height`: Integer. Optional param that can be used to obtain a `pictureUrlCropped`.

    - Return:
        - JSON containing a single array, `venues`, with all of the individual venues.
        - See [sample-search.json](samples/sample-search.json) for an example made with the API call `/search?width=600&height=800`.
