package com.example.fastfood

data class Order(
    val mainMeal: String? = null,
    val sideDishes: List<String> = emptyList(),
    val drink: String? = null
) {
    fun isComplete(): Boolean {
        return !mainMeal.isNullOrBlank() && sideDishes.isNotEmpty() && !drink.isNullOrBlank()
    }
}
