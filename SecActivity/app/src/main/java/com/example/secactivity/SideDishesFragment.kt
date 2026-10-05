package com.example.secactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.secactivity.databinding.FragmentSideDishesBinding

class SideDishesFragment : Fragment() {

    private var _binding: FragmentSideDishesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by activityViewModels()

    private val sideOptions = listOf(
        SideDishOption("黃金大薯條", 45),
        SideDishOption("麥克雞塊 (6塊)", 55),
        SideDishOption("鮮翠凱薩沙拉", 50),
        SideDishOption("玉米濃湯", 40)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSideDishesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkBoxes = listOf(
            binding.cbSide1 to sideOptions[0],
            binding.cbSide2 to sideOptions[1],
            binding.cbSide3 to sideOptions[2],
            binding.cbSide4 to sideOptions[3]
        )

        val listener = View.OnClickListener {
            val selectedDishes = checkBoxes
                .filter { (cb, _) -> cb.isChecked }
                .map { (_, option) -> option }
            viewModel.setSideDishes(selectedDishes)
        }

        checkBoxes.forEach { (cb, _) ->
            cb.setOnClickListener(listener)
        }

        viewModel.orderState.observe(viewLifecycleOwner) { state ->
            val selectedNames = state.sideDishes.map { it.name }.toSet()
            checkBoxes.forEach { (cb, option) ->
                cb.isChecked = selectedNames.contains(option.name)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
