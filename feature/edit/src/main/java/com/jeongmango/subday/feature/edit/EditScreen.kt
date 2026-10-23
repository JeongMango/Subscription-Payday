package com.jeongmango.subday.feature.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeongmango.subday.core.designsystem.theme.SubDayTheme
import com.jeongmango.subday.core.model.BillingCycle
import java.time.LocalDate

/**
 * 등록·수정 화면의 입구. ViewModel에서 상태를 꺼내 EditContent에 넘기고,
 * 사용자의 행동(입력, 버튼)은 ViewModel 함수로 연결합니다.
 */
@Composable
internal fun EditScreen(
    onBackClick: () -> Unit,
    viewModel: EditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 저장·삭제가 끝나면(isDone) 이전 화면으로 돌아갑니다.
    LaunchedEffect(uiState.isDone) {
        if (uiState.isDone) onBackClick()
    }

    EditContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onNameChange = viewModel::onNameChange,
        onAmountChange = viewModel::onAmountChange,
        onBillingCycleChange = viewModel::onBillingCycleChange,
        onFirstBillingDateChange = viewModel::onFirstBillingDateChange,
        onFreeTrialEndDateChange = viewModel::onFreeTrialEndDateChange,
        onSaveClick = viewModel::onSaveClick,
        onDeleteClick = viewModel::onDeleteClick,
    )
}

/**
 * 실제로 그리는 부분. ViewModel을 모르고, 상태를 받아 그리고 행동을 함수로 알리기만 합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditContent(
    uiState: EditUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onBillingCycleChange: (BillingCycle) -> Unit,
    onFirstBillingDateChange: (LocalDate) -> Unit,
    onFreeTrialEndDateChange: (LocalDate?) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "구독 수정" else "구독 등록") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text("이름") },
                placeholder = { Text("넷플릭스") },
                isError = uiState.nameError != null,
                supportingText = uiState.nameError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.amountText,
                onValueChange = onAmountChange,
                label = { Text("금액") },
                suffix = { Text("원") },
                isError = uiState.amountError != null,
                supportingText = uiState.amountError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Column {
                Text("결제 주기", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BillingCycle.entries.forEach { cycle ->
                        FilterChip(
                            selected = uiState.billingCycle == cycle,
                            onClick = { onBillingCycleChange(cycle) },
                            label = { Text(cycle.label()) },
                        )
                    }
                }
            }

            DateField(
                label = "첫 결제일",
                date = uiState.firstBillingDate,
                onDateChange = onFirstBillingDateChange,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("무료 체험 중", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = uiState.freeTrialEndDate != null,
                    onCheckedChange = { checked ->
                        onFreeTrialEndDateChange(if (checked) uiState.firstBillingDate else null)
                    },
                )
            }
            uiState.freeTrialEndDate?.let { trialEnd ->
                DateField(
                    label = "무료 체험 종료일 (이날 첫 결제)",
                    date = trialEnd,
                    onDateChange = onFreeTrialEndDateChange,
                )
            }

            Button(onClick = onSaveClick, modifier = Modifier.fillMaxWidth()) {
                Text("저장")
            }
        }
    }
}

/** 누르면 달력이 뜨는 날짜 입력칸 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(label: String, date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    // 달력을 열었는지는 이 화면 안에서만 필요한 값이라 ViewModel에 두지 않습니다.
    var showPicker by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        OutlinedButton(onClick = { showPicker = true }, modifier = Modifier.fillMaxWidth()) {
            Text("${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일")
        }
    }

    if (showPicker) {
        // DatePicker는 날짜를 UTC 기준 밀리초로 주고받습니다.
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = date.toEpochDay() * MILLIS_PER_DAY)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { onDateChange(LocalDate.ofEpochDay(it / MILLIS_PER_DAY)) }
                    showPicker = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("취소") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L

private fun BillingCycle.label(): String = when (this) {
    BillingCycle.WEEKLY -> "매주"
    BillingCycle.MONTHLY -> "매월"
    BillingCycle.YEARLY -> "매년"
}

@Preview(showBackground = true)
@Composable
private fun EditContentNewPreview() {
    SubDayTheme {
        EditContent(
            uiState = EditUiState(firstBillingDate = LocalDate.of(2026, 10, 23), nameError = "이름을 입력하세요"),
            onBackClick = {},
            onNameChange = {},
            onAmountChange = {},
            onBillingCycleChange = {},
            onFirstBillingDateChange = {},
            onFreeTrialEndDateChange = {},
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditContentEditPreview() {
    SubDayTheme {
        EditContent(
            uiState = EditUiState(
                isEditMode = true,
                name = "넷플릭스",
                amountText = "17000",
                firstBillingDate = LocalDate.of(2026, 1, 15),
                freeTrialEndDate = LocalDate.of(2026, 2, 15),
            ),
            onBackClick = {},
            onNameChange = {},
            onAmountChange = {},
            onBillingCycleChange = {},
            onFirstBillingDateChange = {},
            onFreeTrialEndDateChange = {},
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}
