package com.example.secactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {

    private val _orderState = MutableLiveData(OrderState())
    val orderState: LiveData<OrderState> = _orderState

    fun setMainMeal(meal: MainMealOption?) {
        _orderState.value = _orderState.value?.copy(mainMeal = meal)
    }

    fun setSideDishes(dishes: List<SideDishOption>) {
        _orderState.value = _orderState.value?.copy(sideDishes = dishes)
    }

    fun setDrink(drink: DrinkOption?) {
        _orderState.value = _orderState.value?.copy(drink = drink)
    }

    fun setIceLevel(ice: String) {
        _orderState.value = _orderState.value?.copy(iceLevel = ice)
    }

    fun setSugarLevel(sugar: String) {
        _orderState.value = _orderState.value?.copy(sugarLevel = sugar)
    }

    fun resetOrder() {
        _orderState.value = OrderState()
    }
}
