package org.tdv.tdvbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TdvBackendApplication

fun main(args: Array<String>) {
    runApplication<TdvBackendApplication>(*args)
}
