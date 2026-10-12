package com.example.fastfood

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class DrinkFragment : Fragment(R.layout.fragment_drink) {
    private val viewModel: OrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rgDrink = view.findViewById<RadioGroup>(R.id.rgDrink)
        val btnDone = view.findViewById<Button>(R.id.btnDoneDrink)

        viewModel.order.value?.drink?.let { drink ->
            when (drink) {
                "可樂" -> rgDrink.check(R.id.rbCola)
                "冰紅茶" -> rgDrink.check(R.id.rbIcedTea)
                "水" -> rgDrink.check(R.id.rbWater)
            }
        }

        btnDone.setOnClickListener {
            val selectedId = rgDrink.checkedRadioButtonId
            val drink = when (selectedId) {
                R.id.rbCola -> "可樂"
                R.id.rbIcedTea -> "冰紅茶"
                R.id.rbWater -> "水"
                else -> null
            }
            if (drink != null) {
                viewModel.setDrink(drink)
            }
            parentFragmentManager.popBackStack()
        }
    }
}
