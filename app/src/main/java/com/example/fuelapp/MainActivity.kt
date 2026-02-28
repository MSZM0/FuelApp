package com.example.fuelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fuelapp.ui.theme.FuelAppTheme
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.Locale

data class FuelPrices(
    val pb95: Double? = 0.0,
    val pb98: Double? = 0.0,
    val diesel: Double? = 0.0,
    val lpg: Double? = 0.0
)

interface FuelService {
    @GET
    suspend fun getFuelPrices(@Url url: String): FuelPrices
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FuelAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FuelCalculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCalculator(modifier: Modifier = Modifier) {
    var distance by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("1") }

    var fuelPrices by remember { mutableStateOf<FuelPrices?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedFuelType by remember { mutableStateOf("pb95") }
    var isExpanded by remember { mutableStateOf(false) }

    var totalCost by remember { mutableDoubleStateOf(0.0) }
    var costPerPerson by remember { mutableDoubleStateOf(0.0) }
    var hasCalculated by remember { mutableStateOf(false) }

    val fuelLabels = mapOf(
        "pb95" to "Benzyna PB 95",
        "pb98" to "Benzyna PB 98",
        "diesel" to "Diesel (ON)",
        "lpg" to "Gaz (LPG)"
    )

    val rawGistUrl = "https://gist.githubusercontent.com/MSZM0/dc88eb6f4a28826c315aa2dd06b8805c/raw/0405ef6e443434596c4dcacac85e73206262b625/fuel_price.json"
    val scope = rememberCoroutineScope()

    fun fetchPrices() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val gson = GsonBuilder().setLenient().create()
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://gist.githubusercontent.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                val service = retrofit.create(FuelService::class.java)
                val result = service.getFuelPrices(rawGistUrl)
                fuelPrices = result
                
                price = when (selectedFuelType) {
                    "pb98" -> result.pb98?.toString() ?: ""
                    "diesel" -> result.diesel?.toString() ?: ""
                    "lpg" -> result.lpg?.toString() ?: ""
                    else -> result.pb95?.toString() ?: ""
                }
            } catch (_: Exception) {
                errorMessage = "Błąd wczytywania cen paliwa, wpisz ręcznie"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { fetchPrices() }

    LaunchedEffect(selectedFuelType) {
        fuelPrices?.let { prices ->
            price = when (selectedFuelType) {
                "pb95" -> prices.pb95?.toString() ?: ""
                "pb98" -> prices.pb98?.toString() ?: ""
                "diesel" -> prices.diesel?.toString() ?: ""
                "lpg" -> prices.lpg?.toString() ?: ""
                else -> price
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Zrzutka na Paliwo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { if (!isLoading) isExpanded = it },
            modifier = Modifier.wrapContentWidth()
        ) {
            OutlinedTextField(
                value = if (errorMessage != null) "Tryb ręczny" else fuelLabels[selectedFuelType] ?: selectedFuelType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Typ paliwa") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                enabled = errorMessage == null,
                shape = MaterialTheme.shapes.large
            )

            if (errorMessage == null) {
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    fuelLabels.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label, fontWeight = FontWeight.Medium) },
                            onClick = {
                                selectedFuelType = key
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (errorMessage != null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cena za litr:", style = MaterialTheme.typography.bodyMedium)
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (errorMessage != null) {
                    IconButton(onClick = { fetchPrices() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Ponów")
                    }
                } else {
                    Text(
                        text = if (price.isNotEmpty()) "$price zł" else "---",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        OutlinedTextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("Dystans (km)") },
            placeholder = { Text("np. 350") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = consumption,
            onValueChange = { consumption = it },
            label = { Text("Spalanie (l/100km)") },
            placeholder = { Text("np. 6.5") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Cena (wpisz ręcznie)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = people,
            onValueChange = { people = it },
            label = { Text("Liczba osób") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val d = distance.replace(",", ".").toDoubleOrNull() ?: 0.0
                val c = consumption.replace(",", ".").toDoubleOrNull() ?: 0.0
                val p = price.replace(",", ".").toDoubleOrNull() ?: 0.0
                val n = people.toIntOrNull() ?: 1
                totalCost = (d / 100.0) * c * p
                costPerPerson = if (n > 0) totalCost / n else totalCost
                hasCalculated = true
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading && price.isNotEmpty()
        ) {
            Text("Oblicz")
        }

        if (hasCalculated) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val totalStr = String.format(Locale.getDefault(), "%.2f", totalCost)
                    val personStr = String.format(Locale.getDefault(), "%.2f", costPerPerson)
                    Text(text = "Całkowity koszt: $totalStr zł")
                    Text(
                        text = "Na osobę: $personStr zł",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FuelCalculatorPreview() {
    FuelAppTheme {
        FuelCalculator()
    }
}
