package com.jeongmango.subday.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeongmango.subday.core.designsystem.theme.SubDayTheme
import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

/**
 * 홈 화면의 입구입니다. ViewModel에서 상태를 꺼내 HomeContent에 넘기는 일만 합니다.
 * hiltViewModel()이 HomeViewModel을 만들면서 필요한 Repository를 Hilt가 넣어 줍니다.
 */
@Composable
internal fun HomeScreen(
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // StateFlow를 Compose 상태로 바꿉니다. 값이 바뀌면 화면이 자동으로 다시 그려집니다.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onAddClick = onAddClick,
        onSettingsClick = onSettingsClick,
    )
}

/**
 * 실제로 화면을 그리는 부분입니다. ViewModel을 모르고 상태(uiState)만 받습니다.
 * 그래서 Preview에서 가짜 상태를 넣어 바로 확인할 수 있습니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SubDay") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "설정")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "구독 추가")
            }
        },
    ) { innerPadding ->
        when (uiState) {
            HomeUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is HomeUiState.Success -> SubscriptionList(
                state = uiState,
                contentPadding = innerPadding,
            )
        }
    }
}

@Composable
private fun SubscriptionList(state: HomeUiState.Success, contentPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("등록된 구독 합계", style = MaterialTheme.typography.labelLarge)
                Text(formatWon(state.totalAmount), style = MaterialTheme.typography.headlineMedium)
            }
        }
        items(state.subscriptions, key = { it.id }) { subscription ->
            SubscriptionCard(subscription)
        }
    }
}

@Composable
private fun SubscriptionCard(subscription: Subscription) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(subscription.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "첫 결제일 ${subscription.firstBillingDate}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatWon(subscription.amount), style = MaterialTheme.typography.titleMedium)
                Text(subscription.billingCycle.label(), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun formatWon(amount: Long): String =
    NumberFormat.getNumberInstance(Locale.KOREA).format(amount) + "원"

private fun BillingCycle.label(): String = when (this) {
    BillingCycle.WEEKLY -> "매주"
    BillingCycle.MONTHLY -> "매월"
    BillingCycle.YEARLY -> "매년"
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    SubDayTheme {
        HomeContent(
            uiState = HomeUiState.Success(
                subscriptions = listOf(
                    Subscription(1, "넷플릭스", 17_000, BillingCycle.MONTHLY, LocalDate.of(2026, 1, 15)),
                    Subscription(2, "iCloud+", 39_000, BillingCycle.YEARLY, LocalDate.of(2025, 11, 30)),
                ),
                totalAmount = 56_000,
            ),
            onAddClick = {},
            onSettingsClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    SubDayTheme {
        HomeContent(uiState = HomeUiState.Loading, onAddClick = {}, onSettingsClick = {})
    }
}
