package com.example.incomeexpensetracker.ui.charts

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.incomeexpensetracker.databinding.ActivityChartBinding
import com.example.incomeexpensetracker.mvvm.TransactionViewModel
import com.example.incomeexpensetracker.transactions.TransactionEntity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartBinding
    private val transactionViewModel: TransactionViewModel by viewModels()

    // Keep a reference to the currently displayed Toast
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
    }

    private fun setupObservers() {
        val pieChartIncome: PieChart = binding.pieChartIncome
        val pieChartExpense: PieChart = binding.pieChartExpense

        lifecycleScope.launch {
            transactionViewModel.getIncomeSubcategoriesTransactions().collect { incomeTransactions ->
                val incomeSubcategories = getSubcategoriesData(incomeTransactions)
                updatePieChart(pieChartIncome, incomeSubcategories, "Income Subcategories")
            }
        }

        lifecycleScope.launch {
            transactionViewModel.getExpenseSubcategoriesTransactions().collect { expenseTransactions ->
                val expenseSubcategories = getSubcategoriesData(expenseTransactions)
                updatePieChart(pieChartExpense, expenseSubcategories, "Expense Subcategories")
            }
        }
    }

    // Extract subcategory data from the list of transactions
    private fun getSubcategoriesData(transactions: List<TransactionEntity>): List<Pair<String, Double>> {
        val subcategoryData = mutableMapOf<String, Double>()

        transactions.forEach { transaction ->
            subcategoryData[transaction.subcategory] = subcategoryData.getOrDefault(transaction.subcategory, 0.0) + transaction.amount
        }

        return subcategoryData.map { it.toPair() }
    }

    // Update the pie chart with the given data
    private fun updatePieChart(pieChart: PieChart, subcategoryData: List<Pair<String, Double>>, description: String) {
        val entries = subcategoryData.map { PieEntry(it.second.toFloat(), it.first) }

        val dataSet = PieDataSet(entries, description)
        dataSet.colors = generateCustomColors(entries.size)

        val data = PieData(dataSet)
        pieChart.data = data

        // Make the center of the pie chart transparent
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT) // Make the center transparent
        pieChart.setTransparentCircleColor(Color.TRANSPARENT) // Optional: Make the transparent circle area also transparent
        pieChart.transparentCircleRadius = 55f  // Adjust the transparent circle radius

        pieChart.description.isEnabled = false
        pieChart.animateY(1000)
        pieChart.invalidate()
        pieChart.legend.textColor = resources.getColor(android.R.color.holo_blue_light) // Change this to your desired color

        // Set on value selected listener
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let {
                    if (it is PieEntry) {
                        // Cancel previous toast if any
                        currentToast?.cancel()

                        // Show the new toast
                        val subcategory = it.label
                        val amount = it.value
                        currentToast = Toast.makeText(
                            this@ChartActivity,
                            "Subcategory: $subcategory\nAmount: $amount",
                            Toast.LENGTH_LONG
                        )
                        currentToast?.show()
                    }
                }
            }

            override fun onNothingSelected() {
                // Handle the case when no entry is selected
            }
        })
    }

    // Function to generate a list of colors based on the number of entries
    private fun generateCustomColors(numEntries: Int): List<Int> {
        val colorList = mutableListOf<Int>()
        val hueStep = 360f / numEntries // Adjust hue to ensure unique colors for each slice

        for (i in 0 until numEntries) {
            // Generate a color based on hue, with full saturation and lightness
            val hue = i * hueStep
            val color = Color.HSVToColor(floatArrayOf(hue, 0.8f, 0.8f))  // High saturation and lightness
            colorList.add(color)
        }

        return colorList
    }
}