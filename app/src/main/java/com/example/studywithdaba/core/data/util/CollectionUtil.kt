package com.example.studywithdaba.core.data.util

fun <T> Set <T>.addAndRemoveExisting(element: T): Set<T> {
    return if(contains(element)) {
        filterNot { it == element }.toSet()
    } else
        plus(element)
}