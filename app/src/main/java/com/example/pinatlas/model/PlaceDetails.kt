package com.example.pinatlas.model

class PlaceDetails {
    var Place: Place? = null
    var busyDay: Int = 0

    constructor(Place: Place?, busyDay: Int) {
        this.Place = Place
        this.busyDay = busyDay
    }

    constructor() {
    }
}