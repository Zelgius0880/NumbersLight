package com.florian.numberslight.model

data class Number(
    override val name: String,
    override val image: String,
    val text: String,
) : INumber