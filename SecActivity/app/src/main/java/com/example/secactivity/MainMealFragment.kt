package com.example.secactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.secactivity.databinding.FragmentMainMealBinding

class MainMealFragment : Fragment() {

    private var _binding: FragmentMainMealBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by activityViewModels()

    private val meals = listOf(
        MainMealOption("麥香雞漢堡套餐", 85),
        MainMealOption("雙層牛肉吉事堡套餐", 110),
        MainMealOption("勁辣雞腿堡套餐", 105),
        MainMealOption("經典義大利麵", 120)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgMainMeal.setOnCheckedChangeListener { _, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.rb_meal1 -> meals[0]
                R.id.rb_meal2 -> meals[1]
                R.id.rb_meal3 -> meals[2]
                R.id.rb_meal4 -> meals[3]
                else -> null
            }
            viewModel.setMainMeal(selectedOption)
        }

        viewModel.orderState.observe(viewLifecycleOwner) { state ->
            if (state.mainMeal == null) {
                binding.rgMainMeal.clearCheck()
            } else {
                when (state.mainMeal.name) {
                    meals[0].name -> binding.rbMeal1.isChecked = true
                    meals[1].name -> binding.rbMeal2.isChecked = true
                    meals[2].name -> binding.rbMeal3.isChecked = true
                    meals[3].name -> binding.rbMeal4.isChecked = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
