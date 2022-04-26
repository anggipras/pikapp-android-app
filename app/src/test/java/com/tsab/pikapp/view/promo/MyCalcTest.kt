package com.tsab.pikapp.view.promo

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class MyCalcTest {
    private lateinit var myCalc : MyCalc

    @Before
    fun setUp() {
        myCalc = MyCalc()
    }

    @Test
    fun calculateCircumstance_radiusGiven_returnCorrectResult() {
        val result: Double = myCalc.calculateCircumstance(2.1)
        assertThat(result).isEqualTo(13.188)
    }

    @Test
    fun calculateArea_radiusGiven_returnCorrectResult() {
        val result: Double = myCalc.calculateCircumstance(2.0)
        assertThat(result).isEqualTo(12.56)
    }
}