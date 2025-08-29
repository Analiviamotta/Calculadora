package br.edu.ifsp.scl.ads.prdm.sc3033945.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var previous: String = ""
    private var operator: String? = ""
    private var currentValue: String = ""

    private lateinit var tvDisplayValues: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tvDisplayValues = findViewById(R.id.tvDisplayValues)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNumberButtonsClick(vararg buttons: Button) {
        buttons.forEach { button ->
            attachNumberClickListener(button)
        }
    }


    private fun attachNumberClickListener(button: Button) {
        button.setOnClickListener {
            currentValue += button.text
            tvDisplayValues.text = currentValue
        }
    }

    private fun attachOperatorClickListener(button: Button) {
        button.setOnClickListener {
            if (currentValue.isNotEmpty()) {
                previous = currentValue
                currentValue = ""
                operator = button.text.toString()
                tvDisplayValues.text = "$previous $operator"
            }
        }
    }
}