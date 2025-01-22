package com.taxida.driver.presentation

fun main() {
    println("main")
    var fooList = listOf(-5, -3, -1, 0, 1, 3, 5)
    fooList = fooList.shuffled()
    println(fooList)
    fooList = fooList.sortedBy { it }
    println(fooList)


}

fun tribonacci(signature: DoubleArray, n: Int): DoubleArray {
    if (n == 0) return doubleArrayOf()
    val result: MutableList<Double> = mutableListOf()
    for (i in 0..<n) {
        if (i < signature.size) result.add(signature[i])
    }
    while (result.size < n) {
        result.add(
            result[result.size - 1] + result[result.size - 2] + result[result.size - 3]
        )
    }
    return result.toDoubleArray()
}

fun getAge(yearsOld: String): Int = yearsOld.take(1).toInt()

fun checkForFactor(base: Int, factor: Int): Boolean = base % factor == 0

fun test1DefaultParam(a: Int = 0, b: Int): Int = a + b

fun testReturnType() = println("testReturnType")

class TestClass(
    val a: Int,
    val b: Int = a + 2,
) {
    lateinit var c: String

    init {
        c = (a + b).toString()
    }

    fun test() {

    }

    companion object {
        fun test2() {}
        const val d = 5
    }
}

class TestClass2() {
    var a: Int = 0

    init {
        a = TestClass.d
        val a3: Int
        val a1: Int = -5
        val a2: Int = -5
        a3 = -5
//        val a2: UInt = a1
    }
}

inline fun someHighOrderFunction(
    crossinline someLambda: (String) -> Unit,
    noinline someOtherLambda: (String) -> Unit,
) {

    anotherHigherOrderFunction {
        someLambda("")
    }

    anotherHigherOrderFunction {
        someOtherLambda("")
    }
}

fun anotherHigherOrderFunction(anotherLambda: () -> Unit) {
    anotherLambda()
}

class Student private constructor(val name: String)

fun equality() {
    val a = "hello"
    val b = String("hello".toCharArray())
    println(a == b) //true
    println(a === b) // false
    val c = a
    println(c === a) // true
}

inline fun testInline(
    someLambda: () -> Unit,
    crossinline crossLinedLambda: () -> Unit,
    noinline noLinedLambda: () -> Unit
) {
    println("testInline before invoking someLambda")
    someLambda()
    println("testInline after invoking someLambda")
    println("testInline before invoking crossLinedLambda")
    crossLinedLambda()
    println("testInline after invoking crossLinedLambda")
    println("testInline before invoking noLinedLambda")
    noLinedLambda()
    higherOrderFunction(noLinedLambda)
    println("testInline after invoking noLinedLambda")
}

fun higherOrderFunction(lambda: () -> Unit) {
    lambda()
}

class Person(val pets: MutableList<Pet> = mutableListOf())

val me = Person()

class Pet(name: String) {
    constructor(name: String, owner: Person = me) : this(name) {
        owner.pets.add(this) // adds this pet to the list of its owner's pets
    }

    constructor(name: String, owner: Int = 5) : this(name) {

    }
}

val jimmy = Pet(name = "")
