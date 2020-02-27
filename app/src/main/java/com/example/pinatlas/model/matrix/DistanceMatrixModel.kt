package com.example.pinatlas.model.matrix

class DistanceMatrixModel {
    var status: String = ""
    // Note: underscore separated to match google
    var origin_addresses: ArrayList<String>? = ArrayList()
    var destination_addresses: ArrayList<String>? = ArrayList()
    var rows: ArrayList<Row>? = ArrayList()
}

