package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF64B5F6),
            background = Color(0xFFF5F5F5),
            surface = Color.White
        ),
        typography = Typography(
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                color = Color(0xFF1976D2)
            )
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Composable
fun CalculatorApp() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Мобільний калькулятор навантажень",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        CalculatorContent()
    }
}

@Composable
fun CalculatorContent() {
    var efficiency by remember { mutableStateOf("0.92") }
    var powerFactor by remember { mutableStateOf("0.9") }
    var voltage by remember { mutableStateOf("0.38") }
    var quantity by remember { mutableStateOf("4") }
    var nominalPower by remember { mutableStateOf("20") }
    var usageCoefficient by remember { mutableStateOf("0.15") }
    var reactivePowerCoefficient by remember { mutableStateOf("1.33") }
    var result by remember { mutableStateOf("") }
    var isCalculationError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input Fields
            val inputModifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)

            InputField(
                label = "Коефіцієнт корисної дії (ηн)",
                value = efficiency,
                onValueChange = { efficiency = it },
                modifier = inputModifier,
                keyboardType = KeyboardType.Decimal
            )

            InputField(
                label = "Коефіцієнт потужності навантаження (cos φ)",
                value = powerFactor,
                onValueChange = { powerFactor = it },
                modifier = inputModifier,
                keyboardType = KeyboardType.Decimal
            )

            InputField(
                label = "Напруга навантаження (Uн, кВ)",
                value = voltage,
                onValueChange = { voltage = it },
                modifier = inputModifier,
                keyboardType = KeyboardType.Decimal
            )

            InputField(
                label = "Кількість ЕП (n)",
                value = quantity,
                onValueChange = { quantity = it },
                modifier = inputModifier,
                keyboardType = KeyboardType.Number
            )

            InputField(
                label = "Номінальна потужність ЕП (Рн, кВт)",
                value = nominalPower,
                onValueChange = { nominalPower = it },
                modifier = inputModifier,
                keyboardType = KeyboardType.Decimal
            )

            InputField(
                label = "Коефіцієнт використання (КВ)",
                value = usageCoefficient,
                onValueChange = { usageCoefficient = it },
                modifier = inputModifier,
                keyboardType = KeyboardType.Decimal
            )

            InputField(
                label = "Коефіцієнт реактивної потужності (tgφ)",
                value = reactivePowerCoefficient,
                onValueChange = { reactivePowerCoefficient = it },
                modifier = inputModifier,
                keyboardType = KeyboardType.Decimal
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val eff = efficiency.toDoubleOrNull() ?: throw NumberFormatException()
                        val pf = powerFactor.toDoubleOrNull() ?: throw NumberFormatException()
                        val volt = voltage.toDoubleOrNull() ?: throw NumberFormatException()
                        val qty = quantity.toDoubleOrNull() ?: throw NumberFormatException()
                        val power = nominalPower.toDoubleOrNull() ?: throw NumberFormatException()
                        val usage = usageCoefficient.toDoubleOrNull() ?: throw NumberFormatException()
                        val reactiveCoeff = reactivePowerCoefficient.toDoubleOrNull() ?: throw NumberFormatException()

                        val totalNominalPower = qty * power
                        val current = (qty * power) / (sqrt(3.0) * volt * pf * eff)
                        val groupUsage = usage * totalNominalPower / totalNominalPower
                        val effectiveQty = (totalNominalPower * totalNominalPower) / totalNominalPower
                        val reactivePower = totalNominalPower * usage * reactiveCoeff
                        val activePower = totalNominalPower * usage
                        val totalPower = sqrt(activePower * activePower + reactivePower * reactivePower)

                        result = """
                            Розрахунковий струм: ${String.format("%.2f", current)} А
                            Груповий коефіцієнт використання: ${String.format("%.2f", groupUsage)}
                            Ефективна кількість ЕП: ${String.format("%.2f", effectiveQty)}
                            Активне навантаження: ${String.format("%.2f", activePower)} кВт
                            Реактивне навантаження: ${String.format("%.2f", reactivePower)} квар
                            Повна потужність: ${String.format("%.2f", totalPower)} кВА
                        """.trimIndent()

                        isCalculationError = false
                    } catch (e: Exception) {
                        result = "Помилка: Перевірте введені значення"
                        isCalculationError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                Spacer(Modifier.width(8.dp))
                Text("Розрахувати")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (result.isNotEmpty()) {
                Text(
                    text = result,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isCalculationError) Color.Red else Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isCalculationError)
                                Color.Red.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        )
    }
}