
package com.example.geninterviewsampleapp

import android.content.Context
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import coil.compose.rememberAsyncImagePainter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.text.NumberFormat
import java.util.Locale

@Serializable
data class Recipient(
    val name: String,
    val imageUrl: String,
    val isOnline: Boolean)

@Serializable
data class Transaction(
    val type: String,
    val description: String,
    val amount: Double,
    val icon: String
)

@Serializable
data class BankData(
    val balance: Double,
    val recipients: List<Recipient>,
    val transactions: List<Transaction>
)

@Composable
fun MainScreenUI() {
    val context = LocalContext.current
    val bankDataState = remember { mutableStateOf<BankData?>(null) }
    var selectedNavItem by remember { mutableStateOf("home") }

    LaunchedEffect(key1 = Unit) {
        try {
            // Load the JSON file from assets
            val inputStream: InputStream = context.assets.open("sample_data.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val bankData = Json.decodeFromString<BankData>(jsonString)
            bankDataState.value = bankData
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val bankData = bankDataState.value

    if (bankData != null) {
        Column(modifier = Modifier.fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedNavItem) {
                    "home" -> HomeScreen(bankData, context)
                    "stats" -> StatsScreen()
                    "profile" -> ProfileScreen()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavigationBar(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    selectedNavItem = selectedNavItem,
                    onNavItemClick = { selectedNavItem = it }
                )
                Spacer(modifier = Modifier.width(36.dp)) // Increased margin between FAB and nav bar
                AddFab(
                    onClick = { /* Handle FAB click */ },
                    modifier = Modifier
                        .size(56.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
    }
}

@Composable
fun HeaderSection(context: Context) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Welcome, ", fontSize = 24.sp, color = Color.Gray)
            Text(text = "John!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier.clickable {
                Toast.makeText(context, "Notifications clicked!", Toast.LENGTH_SHORT)
                    .show() // Added Toast
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                modifier = Modifier.size(28.dp)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Red)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun BalanceSection(balance: Double) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = currencyFormat.format(balance), fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Balance",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
    }
}


@Composable
fun GraphSection() {
    val dataPoints = listOf(100f, 300f, 250f, 409f, 300f, 150f)
    val yAxisSteps = listOf(0f, 100f, 200f, 300f, 400f, 500f)
    val xAxisLabels = mapOf(
        1 to "1D", 2 to "5D", 3 to "1M", 4 to "3M", 5 to "6M", 6 to "1Y"
    )
    val defaultIndex = dataPoints.indexOf(409f)
    val selectedPointIndex = remember { mutableIntStateOf(defaultIndex) }
    val scrubberPosition = remember { mutableStateOf<Offset?>(null) }
    var spacing by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2C2C2C))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 32.dp, end = 16.dp, top = 24.dp, bottom = 24.dp)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    event.changes.forEach { pointerInputChange ->
                                        if (pointerInputChange.pressed) {
                                            val position = pointerInputChange.position
                                            scrubberPosition.value = Offset(position.x, 0f)
                                            val index =
                                                ((position.x - spacing / 2) / spacing).toInt()
                                                    .coerceIn(0, dataPoints.size - 1)
                                            selectedPointIndex.value = index
                                            pointerInputChange.consume()
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    spacing = size.width / (dataPoints.size - 1)
                    val chartHeight = size.height
                    val maxY = 500f

                    // Y-axis labels
                    yAxisSteps.forEach { step ->
                        val y = chartHeight - (step / maxY) * chartHeight
                        drawContext.canvas.nativeCanvas.drawText(
                            step.toInt().toString(),
                            -40f,
                            y + 10f,
                            Paint().apply {
                                color = android.graphics.Color.LTGRAY
                                textSize = 32f
                            }
                        )
                    }

                    // Data points
                    val points = dataPoints.mapIndexed { i, yVal ->
                        Offset(
                            x = i * spacing + spacing / 2,
                            y = chartHeight - (yVal / maxY) * chartHeight
                        )
                    }

                    // Smooth curved line
                    val path = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val curr = points[i]
                                val controlPoint1 = Offset((prev.x + curr.x) / 2, prev.y)
                                val controlPoint2 = Offset((prev.x + curr.x) / 2, curr.y)
                                cubicTo(
                                    controlPoint1.x, controlPoint1.y,
                                    controlPoint2.x, controlPoint2.y,
                                    curr.x, curr.y
                                )
                            }
                        }
                    }

                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(
                            width = 8f,
                            cap = StrokeCap.Round
                        )
                    )

                    // Highlight selected point
                    if (selectedPointIndex.intValue != -1) {
                        val selectedPoint = points[selectedPointIndex.intValue]
                        drawCircle(
                            color = Red,
                            radius = 12f,
                            center = selectedPoint,
                            style = Stroke(width = 4f)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 8f,
                            center = selectedPoint
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            "$${dataPoints[selectedPointIndex.intValue].toInt()}",
                            selectedPoint.x,
                            selectedPoint.y - 20f,
                            Paint().apply {
                                color = android.graphics.Color.RED
                                textSize = 30f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }

                    // Draw scrubber line
                    scrubberPosition.value?.let { position ->
                        drawLine(
                            color = Color.Gray,
                            start = Offset(position.x, 0f),
                            end = Offset(position.x, chartHeight),
                            strokeWidth = 2f
                        )
                    }
                }
            }

            // X-axis labels with correct offset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                xAxisLabels.forEach { (index, label) ->
                    Box(
                        modifier = Modifier.weight(1f), // Distribute labels evenly
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            color = if (selectedPointIndex.intValue == index - 1) Color.Red else Color.White,
                            modifier = Modifier.clickable {
                                selectedPointIndex.intValue = index - 1
                                scrubberPosition.value = Offset(
                                    x = (index - 1) * spacing + spacing / 2,
                                    y = 0f
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipientsSection(recipients: List<Recipient>, context: Context) {
    val showBottomSheet = remember { mutableStateOf(false) }
    Column {
        Text(text = "Recipients", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp),
            horizontalArrangement = Arrangement.SpaceEvenly // Space items evenly
        ) {
            val displayedRecipients = recipients.take(5)
            items(displayedRecipients) { recipient ->
                if (displayedRecipients.indexOf(recipient) < 4) {
                    RecipientItem(recipient = recipient, context = context)
                } else {
                    val remainingCount = recipients.size - 4
                    RecipientItemWithCount(
                        recipient = recipient,
                        remainingCount = remainingCount,
                        context = context,
                        onClick = { showBottomSheet.value = true }
                    )
                }
            }
        }
    }

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                items(recipients) { recipient ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RecipientItem(recipient = recipient, context = context)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = recipient.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipientItem(recipient: Recipient, context: Context) {
    val imagePainter: Painter = if (recipient.imageUrl.isNotEmpty() && isInternetAvailable(context)) {
        rememberAsyncImagePainter(model = recipient.imageUrl)
    } else {
        painterResource(id = R.drawable.ic_launcher_background) // Fallback for missing images
    }

    Box {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .size(65.dp) // Circle size
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        val statusColor = if (recipient.isOnline) Green else Red
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(14.dp) // Status dot size
        ) {
            // Draw border
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 2
            )
            // Draw inner dot
            drawCircle(
                color = statusColor,
                radius = size.minDimension / 2.5f
            )
        }
    }
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun RecipientItemWithCount(recipient: Recipient, remainingCount: Int, context: Context, onClick: () -> Unit) {
    val imagePainter: Painter = if (recipient.imageUrl.isNotEmpty() && isInternetAvailable(context)) {
        rememberAsyncImagePainter(model = recipient.imageUrl)
    } else {
        painterResource(id = R.drawable.ic_launcher_background) // Fallback
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Overlay gray mask
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clip(CircleShape)
        )

        Text(
            text = "$remainingCount+",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
fun TransactionHistorySection(transactions: List<Transaction>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Transactions History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Today ▾", fontSize = 14.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            transactions.forEach { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2C2C2C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForTransactionType(transaction.icon),
                    contentDescription = transaction.type,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = transaction.type, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = transaction.description, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Text(
            text = "${if (transaction.amount >= 0) "+" else ""}${currencyFormat.format(transaction.amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.End
        )
    }
}

// Function to map the icon name to an ImageVector
fun getIconForTransactionType(iconName: String): ImageVector {
    return when (iconName) {
        "Restaurant" -> Icons.Default.Icecream
        "AccountBalance" -> Icons.Default.AccountBalance
        "ShoppingCart" -> Icons.Default.ShoppingCart
        else -> Icons.Default.ShoppingCart // Default icon
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: MutableState<Boolean>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (isSelected.value) Color.White else Color.Transparent) // White background for selected state
            .border(
                width = if (isSelected.value) 2.dp else 0.dp, // Black border for selected state
                color = if (isSelected.value) Color.White else Color.Transparent,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected.value) Color.Black else Color.White, // White for unselected, black for selected
            modifier = Modifier.size(24.dp)
        )
        if (isSelected.value) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = Color.Black, // Black text for selected state
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1 // Ensure the label doesn't wrap
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedNavItem: String,
    onNavItemClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 4.dp) // Reduced margin
            .height(60.dp) // Reduced height for the navigation bar
            .background(Color(0xFF2C2C2C), shape = RoundedCornerShape(50.dp))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val homeSelected = remember { mutableStateOf(selectedNavItem == "home") }
        val statsSelected = remember { mutableStateOf(selectedNavItem == "stats") }
        val profileSelected = remember { mutableStateOf(selectedNavItem == "profile") }

        BottomNavItem(
            icon = Icons.Outlined.Home,
            label = stringResource(R.string.home),
            isSelected = homeSelected,
            onClick = {
                onNavItemClick("home")
                homeSelected.value = true
                statsSelected.value = false
                profileSelected.value = false
            }
        )
        BottomNavItem(
            icon = Icons.Outlined.BarChart,
            label = stringResource(R.string.stats),
            isSelected = statsSelected,
            onClick = {
                onNavItemClick("stats")
                homeSelected.value = false
                statsSelected.value = true
                profileSelected.value = false
            }
        )
        BottomNavItem(
            icon = Icons.Outlined.Person,
            label = stringResource(R.string.profile),
            isSelected = profileSelected,
            onClick = {
                onNavItemClick("profile")
                homeSelected.value = false
                statsSelected.value = false
                profileSelected.value = true
            }
        )
    }
}

@Composable
fun AddFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    androidx.compose.material3.FloatingActionButton(
        onClick = {
            Toast.makeText(context, "FAB clicked!", Toast.LENGTH_SHORT).show() // Added Toast
            onClick()
        },
        modifier = modifier,
        shape = CircleShape,
        containerColor = Color(0xFF2C2C2C)
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = stringResource(R.string.add),
            tint = Color.White
        )
    }
}

@Composable
fun HomeScreen(bankData: BankData, context: Context) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White), // Dark grey background
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            // Group HeaderSection and Divider tightly using a Box
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    HeaderSection(context = context)
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }
        }
        item {
            BalanceSection(balance = bankData.balance)
        }
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp), // Remove spacing
                modifier = Modifier.fillMaxWidth()
            ) {
                GraphSection()
                //shadow with space
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp) // Height of the shadow base
                    // Align at the bottom
                ) {
                    rotate(180f) {
                        val radius = 20.dp.toPx()
                        val shadowHeight = size.height
                        val shadowWidth = size.width
                        val shadowBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f), // Grey with transparency
                                Color.Transparent // Fully transparent
                            )
                        )

                        drawPath(
                            path = Path().apply {
                                moveTo(0f, shadowHeight) // Bottom-left
                                quadraticBezierTo(
                                    0f,
                                    shadowHeight - radius,
                                    radius,
                                    shadowHeight - radius
                                ) // Curve to top-left
                                lineTo(
                                    shadowWidth - radius,
                                    shadowHeight - radius
                                ) // Line to top-right
                                quadraticBezierTo(
                                    shadowWidth,
                                    shadowHeight - radius,
                                    shadowWidth,
                                    shadowHeight
                                ) // Curve to bottom-right
                                lineTo(shadowWidth, shadowHeight) // Bottom-right
                                quadraticBezierTo(
                                    shadowWidth,
                                    radius,
                                    shadowWidth - radius,
                                    radius
                                ) // Curve to bottom-right
                                lineTo(radius, radius) // Line to bottom-left
                                quadraticBezierTo(
                                    0f,
                                    radius,
                                    0f,
                                    shadowHeight
                                ) // Curve to bottom-left
                                close()
                            },
                            brush = shadowBrush
                        )
                    }
                }
            }
        }
        item {
            RecipientsSection(recipients = bankData.recipients, context = context)
        }
        item {
            TransactionHistorySection(transactions = bankData.transactions)
        }
    }
}

@Composable
fun StatsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Stats",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                MainScreenUI()
            }
        }
    }

}

