
package com.example.mycalculator1

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {

    private lateinit var tvFormula: TextView
    private lateinit var tvResult: TextView

    private var currentFormula = ""
    private var isResultShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reui)

        initViews()
        setupListeners()
        updateDisplay()
    }

    private fun initViews() {
        tvFormula = findViewById(R.id.tvFormula)
        tvResult = findViewById(R.id.tvResult)
    }

    private fun setupListeners() {
        // Number buttons
        val numberButtons = mapOf(
            R.id.btn0 to "0",
            R.id.btn1 to "1",
            R.id.btn2 to "2",
            R.id.btn3 to "3",
            R.id.btn4 to "4",
            R.id.btn5 to "5",
            R.id.btn6 to "6",
            R.id.btn7 to "7",
            R.id.btn8 to "8",
            R.id.btn9 to "9",
            R.id.btnDot to "."
        )

        for ((id, value) in numberButtons) {
            findViewById<TextView>(id).setOnClickListener {
                onNumberClicked(value)
            }
        }

        // Operator buttons
        val operatorButtons = mapOf(
            R.id.btnPlus to "+",
            R.id.btnMinus to "-",
            R.id.btnMultiply to "×",
            R.id.btnDivide to "÷"
        )

        for ((id, op) in operatorButtons) {
            findViewById<TextView>(id).setOnClickListener {
                onOperatorClicked(op)
            }
        }

        // Action buttons
        findViewById<TextView>(R.id.btnAC).setOnClickListener {
            onClearAllClicked()
        }

        findViewById<TextView>(R.id.btnC).setOnClickListener {
            onClearEntryClicked()
        }

        findViewById<FrameLayout>(R.id.btnBackspace).setOnClickListener {
            onBackspaceClicked()
        }

        findViewById<TextView>(R.id.btnEquals).setOnClickListener {
            onEqualsClicked()
        }

        findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            onClearAllClicked()
        }
    }

    private fun onNumberClicked(number: String) {
        if (isResultShown) {
            currentFormula = ""
            isResultShown = false
        }
        currentFormula += number
        updateDisplay()
    }

    private fun onOperatorClicked(operator: String) {
        if (currentFormula.isEmpty()) {
            if (operator == "-") {
                currentFormula += operator
                isResultShown = false
                updateDisplay()
            }
            return
        }

        isResultShown = false
        val lastChar = currentFormula.last()
        if (isOperator(lastChar)) {
            // Replace trailing operator
            currentFormula = currentFormula.dropLast(1) + operator
        } else {
            currentFormula += operator
        }
        updateDisplay()
    }

    private fun onClearAllClicked() {
        currentFormula = ""
        isResultShown = false
        tvFormula.text = ""
        tvResult.text = "0"
    }

    private fun onClearEntryClicked() {
        if (currentFormula.isNotEmpty()) {
            val lastOpIndex = currentFormula.indexOfLast { isOperator(it) }
            if (lastOpIndex != -1) {
                currentFormula = currentFormula.substring(0, lastOpIndex + 1)
            } else {
                currentFormula = ""
            }
            isResultShown = false
            updateDisplay()
        }
    }

    private fun onBackspaceClicked() {
        if (currentFormula.isNotEmpty()) {
            currentFormula = currentFormula.dropLast(1)
            isResultShown = false
            updateDisplay()
        }
    }

    private fun onEqualsClicked() {
        if (currentFormula.isNotEmpty()) {
            val result = calculateResult(currentFormula)
            if (result != null) {
                tvResult.text = result
                isResultShown = true
            }
        }
    }

    private fun updateDisplay() {
        tvFormula.text = currentFormula
        if (currentFormula.isEmpty()) {
            tvResult.text = "0"
            return
        }

        val result = calculateResult(currentFormula)
        if (result != null) {
            tvResult.text = result
        }
    }

    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '×' || c == '÷'
    }

    private fun calculateResult(expression: String): String? {
        try {
            // Clean ending operators before evaluation
            var expr = expression
            while (expr.isNotEmpty() && isOperator(expr.last())) {
                expr = expr.dropLast(1)
            }
            if (expr.isEmpty()) return null

            val result = evaluateSimpleExpression(expr) ?: return null
            val df = DecimalFormat("#.########")
            return df.format(result)
        } catch (e: Exception) {
            return null
        }
    }

    private fun evaluateSimpleExpression(expression: String): Double? {
        val tokens = mutableListOf<String>()
        var currentToken = StringBuilder()

        for (i in expression.indices) {
            val c = expression[i]
            if (isOperator(c)) {
                if (currentToken.isNotEmpty()) {
                    tokens.add(currentToken.toString())
                    currentToken = StringBuilder()
                } else if (c == '-' && (tokens.isEmpty() || isOperator(tokens.last().first()))) {
                    // Unary minus
                    currentToken.append(c)
                    continue
                }
                tokens.add(c.toString())
            } else {
                currentToken.append(c)
            }
        }
        if (currentToken.isNotEmpty()) {
            tokens.add(currentToken.toString())
        }

        if (tokens.isEmpty()) return null

        // First pass: Multiplication & Division
        val pass1 = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "×" || token == "÷") {
                if (pass1.isEmpty() || i + 1 >= tokens.size) return null
                val prevNum = pass1.removeAt(pass1.size - 1).toDoubleOrNull() ?: return null
                val nextNum = tokens[i + 1].toDoubleOrNull() ?: return null
                val res = if (token == "×") prevNum * nextNum else {
                    if (nextNum == 0.0) return null
                    prevNum / nextNum
                }
                pass1.add(res.toString())
                i += 2
            } else {
                pass1.add(token)
                i++
            }
        }

        // Second pass: Addition & Subtraction
        if (pass1.isEmpty()) return null
        var total = pass1[0].toDoubleOrNull() ?: return null

        var j = 1
        while (j < pass1.size) {
            val op = pass1[j]
            if (j + 1 >= pass1.size) break
            val nextVal = pass1[j + 1].toDoubleOrNull() ?: return null
            if (op == "+") {
                total += nextVal
            } else if (op == "-") {
                total -= nextVal
            }
            j += 2
        }

        return total
    }
}