package com.example.fastfood

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MainMealFragment : Fragment(R.layout.fragment_main_meal) {
    private val viewModel: OrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rgMainMeal = view.findViewById<RadioGroup>(R.id.rgMainMeal)
        val btnDone = view.findViewById<Button>(R.id.btnDoneMain)

        viewModel.order.value?.mainMeal?.let { meal ->
            when (meal) {
                "漢堡" -> rgMainMeal.check(R.id.rbBurger)
                "炸雞" -> rgMainMeal.check(R.id.rbFriedChicken)
                "雞塊" -> rgMainMeal.check(R.id.rbChickenNuggets)
            }
        }

        btnDone.setOnClickListener {
            val selectedId = rgMainMeal.checkedRadioButtonId
            val meal = when (selectedId) {
                R.id.rbBurger -> "漢堡"
                R.id.rbFriedChicken -> "炸雞"
                R.id.rbChickenNuggets -> "雞塊"
                else -> null
            }
            if (meal != null) {
                viewModel.setMainMeal(meal)
            }
            parentFragmentManager.popBackStack()
        }
    }
}
