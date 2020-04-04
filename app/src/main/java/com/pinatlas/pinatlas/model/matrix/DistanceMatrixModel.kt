package com.pinatlas.pinatlas.model.matrix

class DistanceMatrixModel {
    var status: String = ""
    // Note: underscore separated to match google
    var origin_addresses: ArrayList<String>? = ArrayList()
    var destination_addresses: ArrayList<String>? = ArrayList()
    var rows: ArrayList<Row>? = ArrayList()

    constructor(
        status: String,
        origin_addresses: ArrayList<String>?,
        destination_addresses: ArrayList<String>?
    ) {
        this.status = status
        this.origin_addresses = origin_addresses
        this.destination_addresses = destination_addresses
    }
}

