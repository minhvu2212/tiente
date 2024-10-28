package com.example.tiente

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var editTextSource: EditText
    private lateinit var editTextTarget: EditText
    private lateinit var spinnerSource: Spinner
    private lateinit var spinnerTarget: Spinner

    // Tỷ giá được đơn giản hóa còn 5 đồng tiền phổ biến
    private val exchangeRates = mapOf(
        "USD" to 1.0,      // Đô la Mỹ
        "EUR" to 0.92,     // Euro
        "VND" to 24650.0,  // Việt Nam Đồng
        "JPY" to 151.50,   // Yên Nhật
        "CNY" to 7.23      // Nhân dân tệ
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo views
        editTextSource = findViewById(R.id.editTextSource)
        editTextTarget = findViewById(R.id.editTextTarget)
        spinnerSource = findViewById(R.id.spinnerSource)
        spinnerTarget = findViewById(R.id.spinnerTarget)

        // Thiết lập adapter cho spinners với 5 đồng tiền
        val currencies = exchangeRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSource.adapter = adapter
        spinnerTarget.adapter = adapter

        // Đặt giá trị mặc định: USD -> VND
        spinnerSource.setSelection(currencies.indexOf("USD"))
        spinnerTarget.setSelection(currencies.indexOf("VND"))

        // Xử lý sự kiện khi text thay đổi
        editTextSource.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                convertCurrency()
            }
        })

        // Xử lý sự kiện khi spinner thay đổi
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerSource.onItemSelectedListener = spinnerListener
        spinnerTarget.onItemSelectedListener = spinnerListener

        // Xử lý sự kiện khi click vào EditText
        editTextSource.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                swapIfNeeded(true)
            }
        }

        editTextTarget.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                swapIfNeeded(false)
                editTextSource.requestFocus() // Chuyển focus về source
            }
        }
    }

    private fun convertCurrency() {
        val sourceText = editTextSource.text.toString()
        if (sourceText.isEmpty()) {
            editTextTarget.setText("")
            return
        }

        try {
            val sourceAmount = sourceText.toDouble()
            val sourceCurrency = spinnerSource.selectedItem.toString()
            val targetCurrency = spinnerTarget.selectedItem.toString()

            // Chuyển đổi qua USD trước, sau đó chuyển sang tiền tệ đích
            val usdAmount = sourceAmount / exchangeRates[sourceCurrency]!!
            val targetAmount = usdAmount * exchangeRates[targetCurrency]!!

            // Hiển thị kết quả với 2 số thập phân
            editTextTarget.setText(String.format("%.2f", targetAmount))
        } catch (e: Exception) {
            editTextTarget.setText("Error")
        }
    }

    private fun swapIfNeeded(isSourceFocused: Boolean) {
        if (isSourceFocused) {
            // Không cần làm gì nếu source đã được focus
        } else {
            // Hoán đổi giá trị của các spinner và edittext
            val tempText = editTextSource.text.toString()
            editTextSource.setText(editTextTarget.text.toString())
            editTextTarget.setText(tempText)

            val sourcePos = spinnerSource.selectedItemPosition
            spinnerSource.setSelection(spinnerTarget.selectedItemPosition)
            spinnerTarget.setSelection(sourcePos)
        }
    }
}