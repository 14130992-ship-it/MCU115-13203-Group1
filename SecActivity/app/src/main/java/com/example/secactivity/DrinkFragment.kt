package com.example.secactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.secactivity.databinding.FragmentDrinkBinding

class DrinkFragment : Fragment() {

    private var _binding: FragmentDrinkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by activityViewModels()

    private val drinks = listOf(
        DrinkOption("可口可樂", 35),
        DrinkOption("零卡可樂", 35),
        DrinkOption("冰綠茶", 30),
        DrinkOption("焦糖奶茶", 45)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgDrink.setOnCheckedChangeListener { _, checkedId ->
            val selectedDrink = when (checkedId) {
                R.id.rb_drink1 -> drinks[0]
                R.id.rb_drink2 -> drinks[1]
                R.id.rb_drink3 -> drinks[2]
                R.id.rb_drink4 -> drinks[3]
                else -> null
            }
            viewModel.setDrink(selectedDrink)
        }

        binding.rgIce.setOnCheckedChangeListener { _, checkedId ->
            val ice = when (checkedId) {
                R.id.rb_ice_normal -> "正常冰"
                R.id.rb_ice_less -> "少冰"
                R.id.rb_ice_none -> "去冰"
                else -> "正常冰"
            }
            viewModel.setIceLevel(ice)
        }

        binding.rgSugar.setOnCheckedChangeListener { _, checkedId ->
            val sugar = when (checkedId) {
                R.id.rb_sugar_normal -> "正常糖"
                R.id.rb_sugar_half -> "半糖"
                R.id.rb_sugar_none -> "無糖"
                else -> "正常糖"
            }
            viewModel.setSugarLevel(sugar)
        }

        viewModel.orderState.observe(viewLifecycleOwner) { state ->
            if (state.drink == null) {
                binding.rgDrink.clearCheck()
            } else {
                when (state.drink.name) {
                    drinks[0].name -> binding.rbDrink1.isChecked = true
                    drinks[1].name -> binding.rbDrink2.isChecked = true
                    drinks[2].name -> binding.rbDrink3.isChecked = true
                    drinks[3].name -> binding.rbDrink4.isChecked = true
                }
            }

            when (state.iceLevel) {
                "正常冰" -> binding.rbIceNormal.isChecked = true
                "少冰" -> binding.rbIceLess.isChecked = true
                "去冰" -> binding.rbIceNone.isChecked = true
            }

            when (state.sugarLevel) {
                "正常糖" -> binding.rbSugarNormal.isChecked = true
                "半糖" -> binding.rbSugarHalf.isChecked = true
                "無糖" -> binding.rbSugarNone.isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
