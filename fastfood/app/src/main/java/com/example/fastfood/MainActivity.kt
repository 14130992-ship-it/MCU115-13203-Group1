package com.example.fastfood

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView

class MainActivity : AppCompatActivity() {
    private val viewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnChooseMain = findViewById<Button>(R.id.btnChooseMain)
        val btnChooseSides = findViewById<Button>(R.id.btnChooseSides)
        val btnChooseDrink = findViewById<Button>(R.id.btnChooseDrink)
        val btnOrder = findViewById<Button>(R.id.btnOrder)
        val tvCurrentSelection = findViewById<TextView>(R.id.tvCurrentSelection)
        val fragmentContainer = findViewById<FragmentContainerView>(R.id.fragmentContainer)

        btnChooseMain.setOnClickListener {
            fragmentContainer.visibility = android.view.View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MainMealFragment())
                .addToBackStack(null)
                .commit()
        }

        btnChooseSides.setOnClickListener {
            fragmentContainer.visibility = android.view.View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SideDishesFragment())
                .addToBackStack(null)
                .commit()
        }

        btnChooseDrink.setOnClickListener {
            fragmentContainer.visibility = android.view.View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DrinkFragment())
                .addToBackStack(null)
                .commit()
        }

        btnOrder.setOnClickListener {
            val order = viewModel.order.value
            if (order?.isComplete() != true) {
                Toast.makeText(
                    this,
                    "請選擇主餐、至少一項副餐以及飲料。",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("確認訂單")
                .setMessage(
                    "主餐：${order.mainMeal}\n" +
                            "副餐：${order.sideDishes.joinToString("、")}\n" +
                            "飲料：${order.drink}\n\n是否送出此訂單？"
                )
                .setPositiveButton("送出") { d, _ ->
                    d.dismiss()
                    Toast.makeText(this, "訂單已送出！ 🥳", Toast.LENGTH_LONG).show()
                    finish()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        viewModel.order.observe(this) { order ->
            tvCurrentSelection.text = "目前選擇：\n" +
                    "主餐：${order.mainMeal ?: "-"}\n" +
                    "副餐：${order.sideDishes.joinToString("、").ifEmpty { "-" }}\n" +
                    "飲料：${order.drink ?: "-"}"
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                fragmentContainer.visibility = android.view.View.GONE
            }
        }
    }
}
