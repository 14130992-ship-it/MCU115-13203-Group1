package com.example.secactivity

data class MainMealOption(
    val name: String,
    val price: Int
)

data class SideDishOption(
    val name: String,
    val price: Int
)

data class DrinkOption(
    val name: String,
    val price: Int
)

data class OrderState(
    val mainMeal: MainMealOption? = null,
    val sideDishes: List<SideDishOption> = emptyList(),
    val drink: DrinkOption? = null,
    val iceLevel: String = "正常冰",
    val sugarLevel: String = "正常糖",
    val note: String = ""
) {
    val totalPrice: Int
        get() = (mainMeal?.price ?: 0) +
                sideDishes.sumOf { it.price } +
                (drink?.price ?: 0)
}
