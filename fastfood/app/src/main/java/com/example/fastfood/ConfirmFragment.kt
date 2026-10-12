package com.example.fastfood

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ConfirmFragment : Fragment(R.layout.fragment_confirm) {
    private val viewModel: OrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvOrderSummary = view.findViewById<TextView>(R.id.tvOrderSummary)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)

        viewModel.order.observe(viewLifecycleOwner) { order ->
            tvOrderSummary.text = "主餐：${order.mainMeal ?: "-"}\n" +
                    "副餐：${order.sideDishes.joinToString("、").ifEmpty { "-" }}\n" +
                    "飲料：${order.drink ?: "-"}"
        }

        btnConfirm.setOnClickListener {
            val order = viewModel.order.value
            if (order?.isComplete() != true) {
                Toast.makeText(
                    requireContext(),
                    "請選擇主餐、至少一項副餐以及飲料。",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("確認訂單")
                .setMessage(
                    "主餐：${order.mainMeal}\n" +
                            "副餐：${order.sideDishes.joinToString("、")}\n" +
                            "飲料：${order.drink}\n\n是否送出此訂單？"
                )
                .setPositiveButton("送出") { d, _ ->
                    d.dismiss()
                    Toast.makeText(requireContext(), "訂單已送出！ 🥳", Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }
}
