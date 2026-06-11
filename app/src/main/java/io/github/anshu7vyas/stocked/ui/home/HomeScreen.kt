package io.github.anshu7vyas.stocked.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.ui.additem.AddItemSheet
import io.github.anshu7vyas.stocked.ui.common.DaysLeftRing
import io.github.anshu7vyas.stocked.ui.common.categoryEmoji
import io.github.anshu7vyas.stocked.ui.shopping.ShoppingScreen
import io.github.anshu7vyas.stocked.ui.shopping.ShoppingViewModel
import io.github.anshu7vyas.stocked.ui.timeline.TimelineScreen
import io.github.anshu7vyas.stocked.ui.timeline.TimelineViewModel
import kotlinx.coroutines.launch

private const val TAB_PANTRY = 0
private const val TAB_SHOPPING = 1
private const val TAB_TIMELINE = 2

/** Days-left threshold for "needs attention": badge, headline, and urgent carousel. */
private const val URGENT_DAYS = 3

/** Main screen: bottom navigation between Pantry, Shopping, and Timeline. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    shoppingViewModel: ShoppingViewModel = hiltViewModel(),
    timelineViewModel: TimelineViewModel = hiltViewModel(),
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(TAB_PANTRY) }
    val snackbarHostState = remember { SnackbarHostState() }

    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val shoppingItems by shoppingViewModel.items.collectAsStateWithLifecycle()
    val timelineItems by timelineViewModel.items.collectAsStateWithLifecycle()

    // Saveable so an in-progress add survives configuration changes.
    var addSheet by rememberSaveable { mutableStateOf<AddSheetRequest?>(null) }

    val attentionCount = homeState.items.count { it.daysLeft <= URGENT_DAYS }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            TAB_SHOPPING -> stringResource(R.string.shopping_title)
                            TAB_TIMELINE -> stringResource(R.string.timeline_title)
                            else -> stringResource(R.string.app_name)
                        },
                        fontWeight = FontWeight.Bold,
                    )
                },
            )
        },
        floatingActionButton = {
            if (selectedTab == TAB_PANTRY) {
                ExtendedFloatingActionButton(
                    onClick = { addSheet = AddSheetRequest() },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.add_item)) },
                    modifier = Modifier.testTag("add_item_fab"),
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == TAB_PANTRY,
                    onClick = { selectedTab = TAB_PANTRY },
                    icon = {
                        BadgedBox(badge = {
                            if (attentionCount > 0) Badge { Text("$attentionCount") }
                        }) {
                            Icon(Icons.Outlined.Inventory2, contentDescription = null)
                        }
                    },
                    label = { Text(stringResource(R.string.nav_pantry)) },
                )
                NavigationBarItem(
                    selected = selectedTab == TAB_SHOPPING,
                    onClick = { selectedTab = TAB_SHOPPING },
                    icon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_shopping)) },
                )
                NavigationBarItem(
                    selected = selectedTab == TAB_TIMELINE,
                    onClick = { selectedTab = TAB_TIMELINE },
                    icon = { Icon(Icons.Outlined.History, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_timeline)) },
                )
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selectedTab) {
                TAB_PANTRY -> PantryDashboard(
                    state = homeState,
                    snackbarHostState = snackbarHostState,
                    onConsume = homeViewModel::markConsumed,
                    onExpire = homeViewModel::markExpired,
                    onDelete = homeViewModel::delete,
                )

                TAB_SHOPPING -> ShoppingScreen(
                    items = shoppingItems,
                    onQuickAdd = shoppingViewModel::addItem,
                    onDelete = shoppingViewModel::delete,
                    onMoveToPantry = { product ->
                        addSheet = AddSheetRequest(prefillName = product.name, moving = product)
                    },
                )

                else -> TimelineScreen(items = timelineItems)
            }
        }
    }

    addSheet?.let { request ->
        AddItemSheet(
            onDismiss = { addSheet = null },
            initialName = request.prefillName,
            initialCategory = request.moving?.category ?: "",
            onSave = { name, category, expiry ->
                homeViewModel.saveToPantry(request.moving, name, category, expiry)
                addSheet = null
            },
        )
    }
}

private data class AddSheetRequest(
    val prefillName: String = "",
    val moving: Product? = null,
) : java.io.Serializable

@Composable
private fun PantryDashboard(
    state: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onConsume: (Product) -> Unit,
    onExpire: (Product) -> Unit,
    onDelete: (Product) -> Unit,
) {
    // Buckets mirror the design: expiry day itself counts as "1 day left".
    val urgent = state.items.filter { it.daysLeft <= URGENT_DAYS }
    val today = state.items.filter { it.daysLeft <= 1 }
    val thisWeek = state.items.filter { it.daysLeft in 2..7 }
    val later = state.items.filter { it.daysLeft > 7 }
    var deleteCandidate by remember { mutableStateOf<Product?>(null) }
    val scope = rememberCoroutineScope()

    if (state.items.isEmpty()) {
        EmptyState(R.string.pantry_empty, "pantry_list")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("pantry_list"),
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(stringResource(R.string.greeting), style = MaterialTheme.typography.headlineLarge)
                Text(
                    text = if (urgent.isEmpty()) stringResource(R.string.all_fresh)
                    else pluralStringResource(R.plurals.items_need_attention, urgent.size, urgent.size),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (urgent.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.use_it_up),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(urgent, key = { it.product.id }) { item -> UrgentCard(item) }
                }
            }
        }

        pantrySection(R.string.section_today, today, onConsume, onExpire) { deleteCandidate = it }
        pantrySection(R.string.section_this_week, thisWeek, onConsume, onExpire) { deleteCandidate = it }
        pantrySection(R.string.section_later, later, onConsume, onExpire) { deleteCandidate = it }
    }

    deleteCandidate?.let { product ->
        val deletedMessage = stringResource(R.string.deleted_confirmation, product.name)
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(product)
                    deleteCandidate = null
                    scope.launch { snackbarHostState.showSnackbar(deletedMessage) }
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}

private fun LazyListScope.pantrySection(
    titleRes: Int,
    items: List<StockedItem>,
    onConsume: (Product) -> Unit,
    onExpire: (Product) -> Unit,
    onDeleteRequest: (Product) -> Unit,
) {
    if (items.isEmpty()) return
    item {
        SectionHeader(titleRes)
    }
    items(items, key = { it.product.id }) { item ->
        PantryRow(item, onConsume, onExpire, onDeleteRequest)
    }
}

@Composable
private fun SectionHeader(titleRes: Int) {
    Text(
        text = stringResource(titleRes),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp),
    )
}

@Composable
private fun UrgentCard(item: StockedItem) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier.width(150.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DaysLeftRing(item.daysLeft)
            Text(
                "${categoryEmoji(item.product.category)} ${item.product.name}",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
            )
            Text(
                text = if (item.daysLeft <= 0) stringResource(R.string.expired)
                else item.product.category,
                style = MaterialTheme.typography.labelSmall,
                color = if (item.daysLeft <= 0) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PantryRow(
    item: StockedItem,
    onConsume: (Product) -> Unit,
    onExpire: (Product) -> Unit,
    onDeleteRequest: (Product) -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DaysLeftRing(item.daysLeft)
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(item.product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${categoryEmoji(item.product.category)} ${item.product.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.item_menu),
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_consumed)) },
                        onClick = {
                            menuOpen = false
                            onConsume(item.product)
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_expired)) },
                        onClick = {
                            menuOpen = false
                            onExpire(item.product)
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_delete)) },
                        onClick = {
                            menuOpen = false
                            onDeleteRequest(item.product)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(textRes: Int, tag: String) {
    Box(
        Modifier
            .fillMaxSize()
            .testTag(tag),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            stringResource(textRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp),
        )
    }
}
