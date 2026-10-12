package com.example.fastfood

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class SideDishesFragment : Fragment(R.layout.fragment_side_dishes) {
    private val viewModel: OrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cbFries = view.findViewById<CheckBox>(R.id.cbFries)
        val cbSalad = view.findViewById<CheckBox>(R.id.cbSalad)
        val cbCornCup = view.findViewById<CheckBox>(R.id.cbCornCup)
        val btnDone = view.findViewById<Button>(R.id.btnDoneSides)

        val currentSides = viewModel.order.value?.sideDishes ?: emptyList()
        cbFries.isChecked = currentSides.contains("薯條")
        cbSalad.isChecked = currentSides.contains("沙拉")
        cbCornCup.isChecked = currentSides.contains("玉米濃湯")

        btnDone.setOnClickListener {
            val selectedSides = mutableListOf<String>()
            if (cbFries.isChecked) selectedSides.add("薯條")
            if (cbSalad.isChecked) selectedSides.add("沙拉")
            if (cbCornCup.isChecked) selectedSides.add("玉米濃湯")

            viewModel.setSideDishes(selectedSides)
            parentFragmentManager.popBackStack()
        }
    }
}
