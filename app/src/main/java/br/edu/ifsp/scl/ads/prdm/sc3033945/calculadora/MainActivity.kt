package br.edu.ifsp.scl.ads.prdm.sc3033945.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var previous: String = ""
    private var operator: String? = null
    private var currentValue: String = ""
    private var waitingForOperand: Boolean = false
    private var justCalculated: Boolean = false

    private lateinit var tvDisplayValues: TextView
    private val decimalFormat = DecimalFormat("#.##########", DecimalFormatSymbols(Locale("pt", "BR")))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tvDisplayValues = findViewById(R.id.tvDisplayValues)
        tvDisplayValues.text = "0"

        setupNumberButtonsClick(
            findViewById(R.id.btnZero),
            findViewById(R.id.btnOne),
            findViewById(R.id.btnTwo),
            findViewById(R.id.btnThree),
            findViewById(R.id.btnFour),
            findViewById(R.id.btnFive),
            findViewById(R.id.btnSix),
            findViewById(R.id.btnSeven),
            findViewById(R.id.btnEight),
            findViewById(R.id.btnNine)
        )

        setupOperatorButtonsClick(
            findViewById(R.id.btnSum),
            findViewById(R.id.btnSubtraction),
            findViewById(R.id.btnMultiplication),
            findViewById(R.id.btnDivision)
        )

        attachDecimalClickListener(findViewById(R.id.btnComma))
        attachEqualsClickListener(findViewById(R.id.btnEquals))
        attachClearClickListener(findViewById(R.id.btnClear))


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNumberButtonsClick(vararg buttons: Button) {
        buttons.forEach { button -> attachNumberClickListener(button) }
    }

    private fun setupOperatorButtonsClick(vararg buttons: Button) {
        buttons.forEach { button -> attachOperatorClickListener(button) }
    }

    private fun attachNumberClickListener(button: Button) {
        button.setOnClickListener {
            val digit = button.text.toString()


            if (justCalculated) {
                clearAll()
                justCalculated = false
            }


            if (waitingForOperand) {
                currentValue = ""
                waitingForOperand = false
            }


            if (currentValue == "0" && digit != "0") {
                currentValue = digit
            } else if (currentValue != "0") {
                currentValue += digit
            }

            updateDisplay()
        }
    }

    private fun attachOperatorClickListener(button: Button) {
        button.setOnClickListener {
            val newOperator = button.text.toString()


            if (previous.isNotEmpty() && currentValue.isNotEmpty() && operator != null && !waitingForOperand) {
                val result = performCalculation()
                if (!result.isNaN()) {
                    previous = formatNumber(result)
                    currentValue = ""
                } else {
                    return@setOnClickListener
                }
            } else if (currentValue.isNotEmpty()) {
                previous = currentValue
                currentValue = ""
            } else if (justCalculated && previous.isEmpty()) {

                previous = tvDisplayValues.text.toString()
            }

            operator = newOperator
            waitingForOperand = true
            justCalculated = false
            updateDisplay()
        }
    }

    private fun attachDecimalClickListener(button: Button) {
        button.setOnClickListener {

            if (justCalculated) {
                clearAll()
                justCalculated = false
            }


            if (waitingForOperand) {
                currentValue = ""
                waitingForOperand = false
            }


            if (!currentValue.contains(",")) {
                if (currentValue.isEmpty() || currentValue == "0") {
                    currentValue = "0,"
                } else {
                    currentValue += ","
                }
                updateDisplay()
            }
        }
    }

    private fun attachEqualsClickListener(button: Button) {
        button.setOnClickListener {
            if (previous.isNotEmpty() && currentValue.isNotEmpty() && operator != null) {
                val result = performCalculation()
                if (!result.isNaN()) {
                    tvDisplayValues.text = formatNumber(result)

                    previous = ""
                    currentValue = ""
                    operator = null
                    waitingForOperand = true
                    justCalculated = true
                }
            }
        }
    }

    private fun attachClearClickListener(button: Button) {
        button.setOnClickListener {
           clearAll()
        }
    }



    private fun performCalculation(): Double {
        val (first, second) = parseNumbers(previous, currentValue)
        return calculateResult(first, second, operator!!)
    }

    private fun calculateResult(first: Double, second: Double, op: String): Double {
        return when (op) {
            "+" -> first + second
            "-" -> first - second
            "×", "*", "x" -> first * second
            "÷", "/" -> {
                if (second != 0.0) {
                    first / second
                } else {
                    Toast.makeText(this, "Erro: Divisão por zero", Toast.LENGTH_LONG).show()
                    clearAll()
                    Double.NaN
                }
            }
            else -> Double.NaN
        }
    }

    private fun updateDisplay() {
        tvDisplayValues.text = when {
            currentValue.isNotEmpty() -> {
                if (operator == null) currentValue
                else "$previous $operator $currentValue"
            }
            operator != null -> "$previous $operator"
            previous.isNotEmpty() -> previous
            else -> "0"
        }
    }

    private fun formatNumber(number: Double): String {
        return if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            decimalFormat.format(number).replace(".", ",")
        }
    }

    private fun clearAll() {
        tvDisplayValues.text = "0"
        previous = ""
        currentValue = ""
        operator = null
        waitingForOperand = false
        justCalculated = false
    }

    private fun parseNumbers(prev: String, current: String): Pair<Double, Double> {
        val normalizedPrev = prev.replace(",", ".")
        val normalizedCurrent = current.replace(",", ".")
        return normalizedPrev.toDouble() to normalizedCurrent.toDouble()
    }
}