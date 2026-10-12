package com.example.fastfood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {
    private val _order = MutableLiveData(Order())
    val order: LiveData<Order> get() = _order

    fun setMainMeal(meal: String) {
        _order.value = _order.value?.copy(mainMeal = meal)
    }

    fun setSideDishes(sides: List<String>) {
        _order.value = _order.value?.copy(sideDishes = sides)
    }

    fun setDrink(drink: String) {
        _order.value = _order.value?.copy(drink = drink)
    }
}
