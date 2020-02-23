package com.example.pinatlas.model.matrix

import java.util.*

object Main {
    fun printTravelPrices(travelPrices: Array<IntArray>, numberOfCities: Int) {
        for (i in 0 until numberOfCities) {
            for (j in 0 until numberOfCities) {
                print(travelPrices[i][j])
                if (travelPrices[i][j] / 10 == 0)
                    print("  ")
                else
                    print(' ')
            }
            println("")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val numberOfCities = 10
        val travelPrices = Array(numberOfCities) { IntArray(numberOfCities) }
        for (i in 0 until numberOfCities) {
            for (j in 0..i) {
                val rand = Random()
                if (i == j)
                    travelPrices[i][j] = 0
                else {
                    travelPrices[i][j] = rand.nextInt(100)
                    travelPrices[j][i] = rand.nextInt(100)
                }
            }
        }

        printTravelPrices(travelPrices, numberOfCities)
        val geneticAlgorithm =
            Salesman(numberOfCities, travelPrices, 0, 0)
        val result = geneticAlgorithm.optimize()
        println(result)

    }
}

