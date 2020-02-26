package com.example.pinatlas.model.matrix

import java.util.*


class SalesmanGenome: Comparable<Any> {
    var genome: List<Int>
        internal set
    internal var travelPrices: Array<IntArray>
    var startingCity: Int = 0
        internal set
    internal var numberOfCities = 0
    var fitness: Int = 0

    //Random genome- USed in initial population
    constructor(numberOfCities: Int, travelPrices: Array<IntArray>, startingCity: Int) {
        this.travelPrices = travelPrices
        this.startingCity = startingCity
        this.numberOfCities = numberOfCities
        genome = randomSalesman()
        fitness = this.calculateFitness()
    }

    //generates a user defined genome
    constructor(
        permutationOfCities: List<Int>,
        numberOfCities: Int,
        travelPrices: Array<IntArray>,
        startingCity: Int
    ) {
        genome = permutationOfCities
        this.travelPrices = travelPrices
        this.startingCity = startingCity
        this.numberOfCities = numberOfCities
        fitness = this.calculateFitness()
    }

    fun calculateFitness(): Int {
        var fitness = 0
        var currentCity = startingCity
        for (gene in genome) {
            fitness += travelPrices[currentCity][gene]
            currentCity = gene
        }
        fitness += travelPrices[genome[numberOfCities - 2]][startingCity]
        return fitness
    }

    private fun randomSalesman(): List<Int> {
        val result = ArrayList<Int>()
        for (i in 0 until numberOfCities) {
            if (i != startingCity)
                result.add(i)
        }
        Collections.shuffle(result)
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Path: ")
        sb.append(startingCity)
        for (gene in genome) {
            sb.append(" ")
            sb.append(gene)
        }
        sb.append(" ")
        sb.append(startingCity)
        sb.append("\nLength: ")
        sb.append(this.fitness)
        return sb.toString()
    }


    override operator fun compareTo(o: Any): Int {
        val genome = o as SalesmanGenome
        return if (this.fitness > genome.fitness)
            1
        else if (this.fitness < genome.fitness)
            -1
        else
            0
    }
}