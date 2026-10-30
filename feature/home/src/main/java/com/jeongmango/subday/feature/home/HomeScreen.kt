package com.jeongmango.subday.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeongmango.subday.core.designsystem.component.AnimatedMangoMascot
import com.jeongmango.subday.core.designsystem.component.MangoMascot
import com.jeongmango.subday.core.designsystem.theme.Apricot
import com.jeongmango.subday.core.designsystem.theme.Cocoa
import com.jeongmango.subday.core.designsystem.theme.Mango
import com.jeongmango.subday.core.designsystem.theme.SubDayTheme
import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

/**
 * 홈 화면의 입구입니다. ViewModel에서 상태를 꺼내 HomeContent에 넘기는 일만 합니다.
 * hiltViewModel()이 HomeViewModel을 만들면서 필요한 Repository를 Hilt가 넣어 줍니다.
 */
@Composable
internal fun HomeScreen(
    onAddClick: () -> Unit,
    onSubscriptionClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // StateFlow를 Compose 상태로 바꿉니다. 값이 바뀌면 화면이 자동으로 다시 그려집니다.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 구독이 하나라도 생기면 알림 권한을 요청합니다. 알릴 구독이 없을 때 먼저 묻지 않습니다.
    val hasSubscriptions = (uiState as? HomeUiState.Success)?.items?.isNotEmpty() == true
    RequestNotificationPermission(enabled = hasSubscriptions)

    HomeContent(
        uiState = uiState,
        onAddClick = onAddClick,
        onSubscriptionClick = onSubscriptionClick,
        onSettingsClick = onSettingsClick,
    )
}

/**
 * Android 13(API 33)부터는 알림을 띄우기 전에 사용자에게 허용을 받아야 합니다.
 * 시스템 권한 창은 Activity가 있어야 띄울 수 있어서 ViewModel이 아닌 화면에서 요청합니다.
 * 사용자가 두 번 거절하면 시스템이 더 이상 창을 띄우지 않습니다. 그 뒤로는 설정 화면에서 안내합니다.
 */
@Composable
private fun RequestNotificationPermission(enabled: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    // 화면 회전 등으로 다시 그려져도 한 번만 묻도록 기억합니다.
    var asked by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(enabled) {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (enabled && !granted && !asked) {
            asked = true
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
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
    onSubscriptionClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { BrandTitle() },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "설정")
                    }
                },
            )
        },
        floatingActionButton = {
            // 아이콘만 있는 동그란 버튼 대신 글자를 함께 보여 줘서 처음 쓰는 사람도 바로 알 수 있게 합니다.
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("구독 추가") },
            )
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

            is HomeUiState.Success ->
                if (uiState.items.isEmpty()) {
                    EmptySubscriptions(modifier = Modifier.padding(innerPadding))
                } else {
                    SubscriptionList(
                        state = uiState,
                        contentPadding = innerPadding,
                        onSubscriptionClick = onSubscriptionClick,
                    )
                }
        }
    }
}

/** "Sub"는 갈색, "Day"는 망고색으로 앱 이름을 보여 줍니다. */
@Composable
private fun BrandTitle() {
    Text(
        text = buildAnnotatedString {
            append("Sub")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Day") }
        },
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun EmptySubscriptions(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 그림 둘레 여백이 있어서 크게 잡습니다. 망고 자체는 약 170dp 크기로 보입니다.
        AnimatedMangoMascot(modifier = Modifier.size(260.dp))
        Text(
            text = "등록된 구독이 없어요",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "구독을 추가하면 결제일 전에\n망고가 먼저 알려 드릴게요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun SubscriptionList(
    state: HomeUiState.Success,
    contentPadding: PaddingValues,
    onSubscriptionClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { SpendingSummaryCard(state) }
        item {
            Text(
                text = "다가오는 결제",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 12.dp),
            )
        }
        items(state.items, key = { it.subscription.id }) { item ->
            SubscriptionCard(item, onClick = { onSubscriptionClick(item.subscription.id) })
        }
    }
}

/**
 * 이번 달 예상 지출 카드. 앱 아이콘처럼 살구색 → 망고색 그라데이션 위에 망고 캐릭터를 올립니다.
 * 브랜드 그림이라 어두운 모드에서도 같은 색을 씁니다 (글자도 그라데이션에 맞춘 고정 색).
 */
@Composable
private fun SpendingSummaryCard(state: HomeUiState.Success) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Brush.linearGradient(listOf(Apricot, Mango)))
            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${state.month.monthValue}월 예상 지출",
                style = MaterialTheme.typography.labelLarge,
                color = Cocoa.copy(alpha = 0.75f),
            )
            Text(
                text = formatWon(state.monthlySpending),
                style = MaterialTheme.typography.headlineMedium,
                color = Cocoa,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = "구독 ${state.subscriptionCount}개",
                style = MaterialTheme.typography.labelMedium,
                color = Cocoa,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        MangoMascot(modifier = Modifier.size(width = 102.dp, height = 96.dp))
    }
}

@Composable
private fun SubscriptionCard(item: HomeSubscriptionItem, onClick: () -> Unit) {
    val subscription = item.subscription
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 서비스 로고 대신 이름 첫 글자를 동그라미에 넣어 목록에서 빨리 구분되게 합니다.
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = subscription.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(subscription.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${item.nextBillingDate.monthValue}월 ${item.nextBillingDate.dayOfMonth}일 · ${subscription.billingCycle.label()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatWon(subscription.amount), style = MaterialTheme.typography.titleMedium)
                DDayBadge(item.daysUntilBilling, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

/** D-day 알약 모양 표시. 3일 이내면 아이콘의 "D-1" 말풍선과 같은 빨강으로 눈에 띄게 합니다. */
@Composable
private fun DDayBadge(daysUntilBilling: Long, modifier: Modifier = Modifier) {
    val urgent = daysUntilBilling <= 3
    Text(
        text = dDayLabel(daysUntilBilling),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = if (urgent) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .clip(CircleShape)
            .background(if (urgent) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

private fun formatWon(amount: Long): String =
    NumberFormat.getNumberInstance(Locale.KOREA).format(amount) + "원"

private fun dDayLabel(days: Long): String = if (days == 0L) "오늘" else "D-$days"

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
                items = listOf(
                    HomeSubscriptionItem(
                        subscription = Subscription(1, "유튜브 프리미엄", 14_900, BillingCycle.MONTHLY, LocalDate.of(2026, 3, 2)),
                        nextBillingDate = LocalDate.of(2026, 11, 2),
                        daysUntilBilling = 2,
                    ),
                    HomeSubscriptionItem(
                        subscription = Subscription(2, "넷플릭스", 17_000, BillingCycle.MONTHLY, LocalDate.of(2026, 1, 15)),
                        nextBillingDate = LocalDate.of(2026, 11, 15),
                        daysUntilBilling = 15,
                    ),
                ),
                month = YearMonth.of(2026, 10),
                monthlySpending = 31_900,
                subscriptionCount = 2,
            ),
            onAddClick = {},
            onSubscriptionClick = {},
            onSettingsClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    SubDayTheme {
        HomeContent(uiState = HomeUiState.Loading, onAddClick = {}, onSubscriptionClick = {}, onSettingsClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentEmptyPreview() {
    SubDayTheme {
        HomeContent(
            uiState = HomeUiState.Success(
                items = emptyList(),
                month = YearMonth.of(2026, 10),
                monthlySpending = 0,
                subscriptionCount = 0,
            ),
            onAddClick = {},
            onSubscriptionClick = {},
            onSettingsClick = {},
        )
    }
}
