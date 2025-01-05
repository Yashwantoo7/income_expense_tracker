package com.example.incomeexpensetracker.utils
import com.example.incomeexpensetracker.transactions.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TransactionFilterHelper {

    // Function to filter transactions based on the number of days
    fun filterTransactionsByDays(
        allTransactionsFlow: Flow<List<TransactionEntity>>,
        numberOfDays: Int
    ): Flow<List<TransactionEntity>> {
        val currentDate = LocalDate.now()
        val pastDate = currentDate.minusDays(numberOfDays.toLong())

        return allTransactionsFlow.map { transactions ->
            transactions.filter { transaction ->
                val transactionDate = Instant.ofEpochMilli(transaction.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                transactionDate.isAfter(pastDate) || transactionDate.isEqual(pastDate)
            }
        }
    }
}
