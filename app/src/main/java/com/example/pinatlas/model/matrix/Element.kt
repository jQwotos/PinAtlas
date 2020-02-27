package com.example.pinatlas.model.matrix

import com.google.firebase.firestore.model.value.IntegerValue

class Element {
    var status: String = ""
    var duration: Duration? = null
    var distance: Distance? = null
    var priority: Int = 0
}