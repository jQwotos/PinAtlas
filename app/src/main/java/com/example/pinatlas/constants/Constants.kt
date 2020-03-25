package com.example.pinatlas.constants

/*
enum type is a special data type that enables for a variable to be a set of predefined constants; used to check if we dont want to check it's a string

ex/ inside of itinerary mode, we can either hardcode a string. We could do the same w/ the Adapter.
If we have a typo/want to change, then we have to modify the code everywhere.
Instead, we use enum's as a "variables" (don't have to update code if we
 */

/* Owner: MV */
enum class Constants (val type: String) {
    TRIP_ID("TRIP_ID"),
    PLACE_ID("PLACE_ID"),
    REQUEST_DENIED("REQUEST_DENIED"),
    TRIPS_COLLECTION("trips"),
    PLACES_COLLECTION("places"),
}