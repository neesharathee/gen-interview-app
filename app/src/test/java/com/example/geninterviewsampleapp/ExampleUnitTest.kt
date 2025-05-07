package com.example.geninterviewsampleapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ShoppingCart
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testRecipientSerialization() {
        val recipient = Recipient("Alice", "https://example.com/image.jpg", true)
        val json = kotlinx.serialization.json.Json.encodeToString(Recipient.serializer(), recipient)
        val deserializedRecipient =
            kotlinx.serialization.json.Json.decodeFromString(Recipient.serializer(), json)

        assertEquals(recipient, deserializedRecipient)
    }

    @Test
    fun testTransactionSerialization() {
        val transaction = Transaction("Shopping", "Bought groceries", -50.0, "ShoppingCart")
        val json =
            kotlinx.serialization.json.Json.encodeToString(Transaction.serializer(), transaction)
        val deserializedTransaction =
            kotlinx.serialization.json.Json.decodeFromString(Transaction.serializer(), json)

        assertEquals(transaction, deserializedTransaction)
    }

    @Test
    fun testBankDataSerialization() {
        val bankData = BankData(
            balance = 1234.56,
            recipients = listOf(Recipient("Alice", "", true)),
            transactions = listOf(
                Transaction(
                    "Shopping",
                    "Bought groceries",
                    -50.0,
                    "ShoppingCart"
                )
            )
        )
        val json = kotlinx.serialization.json.Json.encodeToString(BankData.serializer(), bankData)
        val deserializedBankData =
            kotlinx.serialization.json.Json.decodeFromString(BankData.serializer(), json)

        assertEquals(bankData, deserializedBankData)
    }

    @Test
    fun testGetIconForTransactionType() {
        assertEquals(Icons.Default.ShoppingCart, getIconForTransactionType("ShoppingCart"))
        assertEquals(Icons.Default.AccountBalance, getIconForTransactionType("AccountBalance"))
        assertEquals(Icons.Default.ShoppingCart, getIconForTransactionType("Unknown"))
    }

    @Test
    fun testRecipientOnlineStatus() {
        val onlineRecipient = Recipient("Alice", "https://example.com/image.jpg", true)
        val offlineRecipient = Recipient("Bob", "https://example.com/image.jpg", false)

        assertTrue(onlineRecipient.isOnline)
        assertTrue(!offlineRecipient.isOnline)
    }

    @Test
    fun testTransactionAmountFormatting() {
        val transaction = Transaction("Shopping", "Bought groceries", -50.0, "ShoppingCart")
        val formattedAmount = formatTransactionAmount(transaction.amount)

        assertEquals("-$50.00", formattedAmount)
    }

    @Test
    fun testEmptyBankDataSerialization() {
        val emptyBankData =
            BankData(balance = 0.0, recipients = emptyList(), transactions = emptyList())
        val json =
            kotlinx.serialization.json.Json.encodeToString(BankData.serializer(), emptyBankData)
        val deserializedBankData =
            kotlinx.serialization.json.Json.decodeFromString(BankData.serializer(), json)

        assertEquals(emptyBankData, deserializedBankData)
    }

    @Test
    fun testGetIconForTransactionTypeWithUnknownType() {
        val icon = getIconForTransactionType("UnknownType")
        assertEquals(Icons.Default.ShoppingCart, icon) // Default icon for unknown types
    }

    @Test
    fun testGraphDataPoints() {
        val dataPoints = listOf(100f, 300f, 250f, 409f, 300f, 150f)
        val maxPoint = dataPoints.maxOrNull()
        val minPoint = dataPoints.minOrNull()

        assertEquals(409f, maxPoint)
        assertEquals(100f, minPoint)
    }

    private fun formatTransactionAmount(amount: Double): String {
        val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)
        return currencyFormat.format(amount)
    }
}
