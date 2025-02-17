package com.pinatlas.pinatlas.model.matrix
//This code is a modified version of : https://stackabuse.com/traveling-salesman-problem-with-genetic-algorithms-in-java/
import java.util.*

class Salesman(
    private val numberOfCities: Int,
    private val travelPrices: Array<IntArray>,
    private val startingCity: Int,
    private val targetFitness: Int
) {
    private val generationSize: Int
    private val genomeSize: Int
    private val reproductionSize: Int
    private val maxIterations: Int
    private val mutationRate: Float
    private val tournamentSize: Int

    init {
        genomeSize = numberOfCities - 1
        generationSize = 3000 //Changed from previous 5k making it quicker
        reproductionSize = 200
        maxIterations = 1000
        mutationRate = 0.1f
        tournamentSize = 40
    }

    fun initialPopulation(): List<SalesmanGenome> {
        val population = ArrayList<SalesmanGenome>()
        for (i in 0 until generationSize) {
            population.add(
                SalesmanGenome(
                    numberOfCities,
                    travelPrices,
                    startingCity
                )
            )
        }
        return population
    }

    fun selection(population: List<SalesmanGenome>): List<SalesmanGenome> {
        val selected = ArrayList<SalesmanGenome>()
        for (i in 0 until reproductionSize) {
            selected.add(tournamentSelection(population))
        }
        return selected
    }

    //called by initial population
    fun tournamentSelection(population: List<SalesmanGenome>): SalesmanGenome {
        val selected =
            pickNRandomElements<SalesmanGenome>(
                population,
                tournamentSize
            )
        return Collections.min(selected!!)
    }

    fun mutate(salesman: SalesmanGenome): SalesmanGenome {
        val random = Random()
        val mutate = random.nextFloat()
        if (mutate < mutationRate) {
            val genome = salesman.genome
            Collections.swap(genome, random.nextInt(genomeSize), random.nextInt(genomeSize))
            return SalesmanGenome(
                genome,
                numberOfCities,
                travelPrices,
                startingCity
            )
        }
        return salesman
    }

    fun createGeneration(population: List<SalesmanGenome>): List<SalesmanGenome> {
        val generation = ArrayList<SalesmanGenome>()
        var currentGenerationSize = 0
        while (currentGenerationSize < generationSize) {
            val parents =
                pickNRandomElements<SalesmanGenome>(
                    population,
                    2
                )
            val children = crossover(parents!!)
            children[0] = mutate(children[0])
            children[1] = mutate(children[1])
            generation.addAll(children)
            currentGenerationSize += 2
        }
        return generation
    }

    fun crossover(parents: List<SalesmanGenome>): MutableList<SalesmanGenome> {
        val random = Random()
        val breakpoint = random.nextInt(genomeSize)
        val children = ArrayList<SalesmanGenome>()
        var parent1Genome: List<Int> = ArrayList(parents[0].genome)
        val parent2Genome = ArrayList(parents[1].genome)

        for (i in 0 until breakpoint) {
            val newVal: Int
            newVal = parent2Genome.get(i)
            Collections.swap(parent1Genome, parent1Genome.indexOf(newVal), i)
        }
        children.add(
            SalesmanGenome(
                parent1Genome,
                numberOfCities,
                travelPrices,
                startingCity
            )
        )
        parent1Genome = parents[0].genome

        for (i in breakpoint until genomeSize) {
            val newVal = parent1Genome[i]
            Collections.swap(parent2Genome, parent2Genome.indexOf(newVal), i)
        }
        children.add(
            SalesmanGenome(
                parent2Genome,
                numberOfCities,
                travelPrices,
                startingCity
            )
        )

        return children
    }

    fun optimize(): SalesmanGenome {
        var population = initialPopulation() // 3000 possible permutations of the matrix
        var globalBestGenome = population[0] //Set one first to be the best
        for (i in 0 until maxIterations) {   //Run a 1000 times
            val selected = selection(population)    // 200 Instances given  suing tournament method
            population = createGeneration(selected)
            globalBestGenome = Collections.min(population)
            if (globalBestGenome.fitness < targetFitness)
                break
        }
        return globalBestGenome
    }

    companion object {
        fun <E> pickNRandomElements(list: List<E>, n: Int): List<E>? {
            val r = Random()
            val length = list.size

            if (length < n) return null

            for (i in length - 1 downTo length - n) {
                Collections.swap(list, i, r.nextInt(i + 1))
            }
            return list.subList(length - n, length)
        }
    }
}
