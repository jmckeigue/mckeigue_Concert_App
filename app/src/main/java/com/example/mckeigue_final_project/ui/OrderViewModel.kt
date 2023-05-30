package com.example.mckeigue_final_project.ui

import androidx.lifecycle.ViewModel
import com.example.mckeigue_final_project.data.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val price_per_ticket = 100.00

private const val early_ticket = 60.00

class OrderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState(concertOptions = concertOptions()))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    fun setQuantity(numberTix: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                quantity = numberTix,
                price = calculatePrice(quantity = numberTix)
            )
        }
    }

    fun setConcert(desiredConcert: String) {
        _uiState.update { currentState ->
            currentState.copy(concerts = desiredConcert)
        }
    }

    fun setDate(concertDate: String) {
        _uiState.update { currentState ->
            currentState.copy(
                date = concertDate,
                price = calculatePrice(concertDate = concertDate)
            )
        }
    }

    fun resetOrder() {
        _uiState.value = OrderUiState(concertOptions = concertOptions())
    }

    private fun calculatePrice(
        quantity: Int = _uiState.value.quantity,
        concertDate: String = _uiState.value.date
    ): String {
        var calculatedPrice = quantity * price_per_ticket
        if (concertOptions()[0] == concertDate) {
            calculatedPrice += early_ticket
        }
        val formattedPrice = NumberFormat.getCurrencyInstance().format(calculatedPrice)
        return formattedPrice
    }

    private fun concertOptions(): List<String> {
        val dateOptions = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()

        repeat(4) {
            dateOptions.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateOptions
    }
}