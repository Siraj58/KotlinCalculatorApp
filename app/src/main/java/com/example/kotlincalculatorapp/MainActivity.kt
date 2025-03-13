package com.example.kotlincalculatorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    open class CalcException(message: String) : Exception(message)
    private lateinit var workingsTV: TextView
    private lateinit var resultsTV: TextView

    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingsTV = findViewById(R.id.workingsTV)
        resultsTV = findViewById(R.id.resultsTV)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal) {
                    workingsTV.append(view.text)
                    canAddDecimal = false
                }
            } else {
                workingsTV.append(view.text)
            }
            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = workingsTV.text.length
        if (length > 0) {
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)
        }
    }

    fun equalsAction(view: View) {
        resultsTV.text = calculateResults()
        workingsTV.text = ""
    }

    private fun calculateResults(): String {
        try {
            // Code that may throw an exception
            val digitsOperators = parseDigitsOperators()
            if (digitsOperators.isEmpty()) return ""

            val timesDivision = processMultiplicationDivision(digitsOperators)
            if (timesDivision.isEmpty()) return ""

            // Now process addition and subtraction
            return processAdditionSubtraction(timesDivision).toString()

        } catch (e: CalcException) {
            // Code for handling the exception
            println("Caught a CalcException: ${e.message}")
            return ""  // Return an empty string or appropriate fallback value in case of error
        }

    }

    private fun processAdditionSubtraction(elements: MutableList<Any>): Float {
        var result = elements[0] as Float

        for (i in 1 until elements.size step 2) {
            val operator = elements[i] as Char
            val nextValue = elements[i + 1] as Float
            when (operator) {
                '+' -> result += nextValue
                '-' -> result -= nextValue
            }
        }
        return result
    }

    private fun processMultiplicationDivision(elements: MutableList<Any>): MutableList<Any> {
        val list = elements.toMutableList()
        var i = 0
        while (i < list.size) {
            if (list[i] is Char && i > 0 && i < list.size - 1) {
                val operator = list[i] as Char
                if (operator == 'x' || operator == '/') {
                    val prev = list[i - 1] as Float
                    val next = list[i + 1] as Float
                    val result = if (operator == 'x') prev * next else prev / next
                    list[i - 1] = result
                    list.removeAt(i)
                    list.removeAt(i)
                    i--
                }
            }
            i++
        }
        return list
    }

    private fun parseDigitsOperators(): MutableList<Any> {
    try{
        val list = mutableListOf<Any>()
        var currentDigit = ""

        for (character in workingsTV.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
            }
        }
        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }
        return list
    } catch (e: CalcException) {
        // Code for handling the exception
        val list = mutableListOf<Any>()
        println("Caught a CalcException: ${e.message}")
        return list  // Return an empty string or appropriate fallback value in case of error
    }

    }
}
