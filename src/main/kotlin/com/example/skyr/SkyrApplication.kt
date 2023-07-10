package com.example.skyr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SkyrApplication

fun main(args: Array<String>) {
    runApplication<SkyrApplication>(*args)
}
