package com.example.mckeigue_final_project.data

data class OrderUiState(
    /** Selected ticket quantity (1, 2, 4) */
    val quantity: Int = 0,
    /** Name of concerts in order */
    val concerts: String = "",
    /** Selected date for concert (such as "Jan 1") */
    val date: String = "",
    /** Total price for the order */
    val price: String = "",
    /** Available concert dates */
    val concertOptions: List<String> = listOf()
)