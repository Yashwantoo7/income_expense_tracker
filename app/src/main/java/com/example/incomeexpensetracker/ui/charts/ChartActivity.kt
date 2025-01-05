package com.example.incomeexpensetracker.ui.charts

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.incomeexpensetracker.R
import com.example.incomeexpensetracker.databinding.ActivityChartBinding
import com.example.incomeexpensetracker.mvvm.TransactionViewModel
import com.example.incomeexpensetracker.transactions.TransactionEntity
import com.example.incomeexpensetracker.utils.TransactionFilterHelper
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
    private lateinit var transactionFilterHelper: TransactionFilterHelper

    // Keep a reference to the currently displayed Toast
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactionFilterHelper = TransactionFilterHelper()

        // Get the Spinner for selecting days
        val daysSpinner: Spinner = binding.daysSpinner
        setupDaysSpinner(daysSpinner)
    }

    private fun setupDaysSpinner(daysSpinner: Spinner) {
        // Observe the selected item from the Spinner and update charts accordingly
        daysSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDays = parent?.getItemAtPosition(position).toString().toInt()
                setupObservers(selectedDays)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                setupObservers()
            }
        }
    }

    private fun setupObservers(days: Int = 30) {
        val pieChartIncome: PieChart = binding.pieChartIncome
        val pieChartExpense: PieChart = binding.pieChartExpense

        lifecycleScope.launch {
            transactionFilterHelper.filterTransactionsByDays(transactionViewModel.getIncomeSubcategoriesTransactions(), days).collect { incomeTransactions ->
                val incomeSubcategories = getSubcategoriesData(incomeTransactions)
                updatePieChart(pieChartIncome, incomeSubcategories, "Income Subcategories", true)
            }
        }

        lifecycleScope.launch {
            transactionFilterHelper.filterTransactionsByDays(transactionViewModel.getExpenseSubcategoriesTransactions(), days).collect { expenseTransactions ->
                val expenseSubcategories = getSubcategoriesData(expenseTransactions)
                updatePieChart(pieChartExpense, expenseSubcategories, "Expense Subcategories", false)
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

    private fun updatePieChart(pieChart: PieChart, subcategoryData: List<Pair<String, Double>>, description: String, isIncome: Boolean) {
        // Calculate total amount to compute percentages
        val totalAmount = subcategoryData.sumByDouble { it.second }

        // Combine small categories into "Others"
        val threshold = 5.0  // Categories below this threshold will be grouped as "Others"
        val filteredData = subcategoryData.filter { it.second >= threshold }.toMutableList()

        // Calculate the "Others" category if applicable
        var othersAmount = 0.0
        if (subcategoryData.size != filteredData.size) {
            othersAmount = subcategoryData.filter { it.second < threshold }.sumByDouble { it.second }
            filteredData.add(Pair("Others", othersAmount))
        }

        // Sort the data in descending order by amount
        val sortedData = filteredData.sortedByDescending { it.second }

        // Shuffle the sorted data to display it in random order
        val randomSortedData = sortedData.shuffled()

        // Create Pie Entries for the shuffled data
        val entries = randomSortedData.map { PieEntry(it.second.toFloat(), it.first) }

        // Create PieDataSet
        val dataSet = PieDataSet(entries, description)
        dataSet.colors = generateCustomColors(entries.size)

        // Set PieData
        val data = PieData(dataSet)
        dataSet.valueTextSize = 10f

        pieChart.data = data
        pieChart.legend.isEnabled = false // Disable internal legend
        pieChart.setDrawEntryLabels(true)
        pieChart.setEntryLabelTextSize(8f)

        // Make the center of the pie chart transparent (donut chart style)
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleColor(Color.TRANSPARENT)
        pieChart.transparentCircleRadius = 55f

        pieChart.description.isEnabled = false
        pieChart.animateY(1000)
        pieChart.invalidate()

        // Dynamically add legends with custom colors
        val legendLayout = if (isIncome) findViewById<LinearLayout>(R.id.incomeLegendLayout) else findViewById<LinearLayout>(R.id.expenseLegendLayout)
        legendLayout.removeAllViews() // Clear previous legends

        // Create and add legend entries with custom colors
        sortedData.forEachIndexed { index, (subcategory, amount) ->
            val legendEntry = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                setPadding(8, 0, 8, 8) // Add padding to the entry

                // Add the color box
                val colorBox = View(this@ChartActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(30, 30).apply {
                        rightMargin = 16
                    }
                    setBackgroundColor(dataSet.colors[index]) // Set the color of the legend item box
                }

                // Add the text
                val text = TextView(this@ChartActivity).apply {
                    text = "$subcategory: ${String.format("%.2f", amount)} (${String.format("%.1f", (amount / totalAmount) * 100)}%)"
                    setTextColor(Color.parseColor("#ADD8E6")) // Light blue color for the legend text
                    textSize = 12f
                    setPadding(8, 0, 8, 8)
                }

                // Add color box and text to the legend entry
                addView(colorBox)
                addView(text)
            }

            // Add the constructed legend entry to the appropriate legend layout
            legendLayout.addView(legendEntry)
        }

        // Handle selection of pie chart slices (interactive)
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let {
                    if (it is PieEntry) {
                        // Cancel previous toast if any
                        currentToast?.cancel()

                        // Show a new toast with selected entry information
                        val subcategory = it.label
                        val amount = it.value
                        currentToast = Toast.makeText(
                            this@ChartActivity,
                            "Subcategory: $subcategory\nAmount: ${String.format("%.2f", amount)}",
                            Toast.LENGTH_SHORT
                        )
                        currentToast?.show()
                    }
                }
            }

            override fun onNothingSelected() {
                // Handle when nothing is selected
            }
        })
    }

    // Function to generate a list of colors based on the number of entries
    private fun generateCustomColors(numEntries: Int): List<Int> {
        val colorList = mutableListOf<Int>()

        // Ensure there is enough space between hues to avoid similarity
        val hueStep = 240f / numEntries // Reducing the step to ensure distinct colors

        for (i in 0 until numEntries) {
            // Generate a color based on hue, avoiding pure red hues (near 0° or 360°)
            var hue = i * hueStep

            // Avoid hues near red (0° or 360°), we will allow hues near pink (300° to 330°)
            if (hue < 30f || hue > 330f) {
                hue = 30f + (i % 8) * hueStep // Apply a different pattern for distinct colors
            }

            // If hue falls near red, use pink instead
            if (hue < 50f) {
                hue = 330f // Set hue to a pinkish color (330° for light pink)
            }

            val color = Color.HSVToColor(floatArrayOf(hue, 0.7f, 0.8f)) // High saturation and lightness
            colorList.add(color)
        }

        return colorList
    }
}
