package com.xenotactic.gamelogic.utils

fun <T> sequenceOfNullable(element: T?) =
    if (element == null) emptySequence<T>() else sequenceOf(element)
