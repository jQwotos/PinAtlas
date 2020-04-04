package com.pinatlas.pinatlas.model

class BusyData {
    var busyTimes: ArrayList<Timings>? = null      // How busy a place is on a Day of the week
    var waitTimes: ArrayList<Timings>? = null      // How long are wait times in minutes
    var avgSpentTimes: ArrayList<Int>? = null      // Range of average time spent in minutes, between [0] to [1] minutes spent

    class Timings {
        var name: String? = null
        var data: ArrayList<Int>? = arrayListOf() // an array of the data starting from hour 0 to hour 24
    }
}