package com.example.skyr

abstract class Dao {

    fun resolveResult(updatedRows: Int): Boolean {
        return updatedRows > 0
    }

    fun <T> resolveSingleResult(result: List<T>): T? {
        if (result.isEmpty()) return null
        if (result.size == 1) return result[0]
        throw IllegalStateException("Could not resolve single result")
    }
}
