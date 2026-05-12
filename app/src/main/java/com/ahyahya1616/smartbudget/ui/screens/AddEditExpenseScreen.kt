package com.ahyahya1616.smartbudget.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ahyahya1616.smartbudget.ui.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    viewModel: BudgetViewModel,
    expenseId: Long? = null,
    onNavigateUp: () -> Unit
) {
    val categories by viewModel.activeCategories.collectAsState()

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var paymentMethod by remember { mutableStateOf("Cash") }

    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isEditMode = expenseId != null
    val dateFormatter = remember { java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.FRANCE) }

    val paymentMethods = listOf("Cash", "Card", "Virement")

    LaunchedEffect(expenseId) {
        if (expenseId != null) {
            viewModel.getExpenseById(expenseId)?.let { expense ->
                amount = expense.amount.toString()
                note = expense.note ?: ""
                selectedCategoryId = expense.categoryId
                dateMillis = expense.date
                paymentMethod = expense.paymentMethod ?: "Cash"
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Modifier la dépense" else "Nouvelle dépense",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ─── Amount ──────────────────────────────────────────
            Text(
                "Montant",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    amountError = false
                },
                placeholder = { Text("0.00") },
                suffix = { Text("MAD", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            if (amountError) {
                Text(
                    "Veuillez entrer un montant valide (> 0)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ─── Category ────────────────────────────────────────
            Text(
                "Catégorie",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categories.find { it.id == selectedCategoryId }?.let { "${it.icon} ${it.name}" }
                        ?: "Sélectionner une catégorie",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    isError = categoryError,
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text("${category.icon} ${category.name}") },
                            onClick = {
                                selectedCategoryId = category.id
                                categoryError = false
                                expanded = false
                            }
                        )
                    }
                }
            }
            if (categoryError) {
                Text(
                    "Veuillez sélectionner une catégorie",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ─── Date ────────────────────────────────────────────
            Text(
                "Date",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = dateFormatter.format(java.util.Date(dateMillis)),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Choisir la date", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ─── Payment Method ──────────────────────────────────
            Text(
                "Méthode de paiement",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paymentMethods.forEach { method ->
                    val isSelected = paymentMethod == method
                    FilterChip(
                        selected = isSelected,
                        onClick = { paymentMethod = method },
                        label = {
                            Text(
                                when (method) {
                                    "Cash" -> "💵 Espèces"
                                    "Card" -> "💳 Carte"
                                    "Virement" -> "🏦 Virement"
                                    else -> method
                                }
                            )
                        },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ─── Note ────────────────────────────────────────────
            Text(
                "Note (optionnelle)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Ajouter une note...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ─── Save Button ─────────────────────────────────────
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    val isValidAmount = parsedAmount > 0
                    val isValidCategory = selectedCategoryId != null

                    amountError = !isValidAmount
                    categoryError = !isValidCategory

                    if (isValidAmount && isValidCategory) {
                        if (isEditMode) {
                            viewModel.updateExpense(
                                com.ahyahya1616.smartbudget.data.model.Expense(
                                    id = expenseId!!,
                                    amount = parsedAmount,
                                    date = dateMillis,
                                    categoryId = selectedCategoryId!!,
                                    note = note.ifBlank { null },
                                    paymentMethod = paymentMethod,
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        } else {
                            viewModel.addExpense(
                                amount = parsedAmount,
                                dateMillis = dateMillis,
                                categoryId = selectedCategoryId!!,
                                note = note.ifBlank { "" },
                                paymentMethod = paymentMethod
                            )
                        }
                        onNavigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    if (isEditMode) "Mettre à jour" else "Enregistrer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
