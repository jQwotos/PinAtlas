package com.example.pinatlas.model

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
class Place {
    var name: String? = null
    var placeId: String? = null

    @ServerTimestamp
    var timestamp: Date? = null

    constructor() {}

    constructor(name: String, placeId: String) {
        this.name = name
        this.placeId = placeId
    }
}