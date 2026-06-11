package io.github.anshu7vyas.stocked.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.ui.shopping.ShoppingViewModel
import io.github.anshu7vyas.stocked.ui.theme.CardBackground
import io.github.anshu7vyas.stocked.ui.theme.cardListItem
import io.github.anshu7vyas.stocked.ui.theme.stockedTopBarColors
import io.github.anshu7vyas.stocked.ui.timeline.TimelineViewModel
import io.github.anshu7vyas.stocked.ui.timeline.TimelineRow
import kotlinx.coroutines.launch

/** The three-tab main screen: Home (pantry), Shopping List, Timeline. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddItem: () -> Unit,
    onAddShoppingItem: () -> Unit,
) {
    val tabTitles = listOf(
        stringResource(R.string.tab_home),
        stringResource(R.string.tab_shopping_list),
        stringResource(R.string.tab_timeline),
    )
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.mipmap.ic_launcher),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                colors = stockedTopBarColors(),
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title) },
                    )
                }
            }
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> PantryTab(snackbarHostState, onAddItem)
                    1 -> ShoppingTab(onAddShoppingItem)
                    else -> TimelineTab()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PantryTab(
    snackbarHostState: SnackbarHostState,
    onAddItem: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var actionItem by remember { mutableStateOf<StockedItem?>(null) }
    var deleteItem by remember { mutableStateOf<StockedItem?>(null) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("pantry_list"),
        ) {
            items(uiState.items, key = { it.product.id }) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .background(CardBackground)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { actionItem = item },
                            onLongClickLabel = stringResource(
                                R.string.item_action_title, item.product.name,
                            ),
                        )
                        .padding(12.dp),
                ) {
                    Text(
                        item.product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(" (${item.product.category}) ")
                    Text(
                        if (item.product.expired) stringResource(R.string.expired)
                        else stringResource(R.string.expire_in_days, item.daysLeft),
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = onAddItem,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_item_fab"),
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_item))
        }
    }

    actionItem?.let { item ->
        AlertDialog(
            onDismissRequest = { actionItem = null },
            title = { Text(stringResource(R.string.item_action_title, item.product.name)) },
            text = { Text(stringResource(R.string.item_action_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.markConsumed(item.product)
                    actionItem = null
                }) { Text(stringResource(R.string.action_consumed)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.markExpired(item.product)
                    actionItem = null
                }) { Text(stringResource(R.string.action_expired)) }
                TextButton(onClick = {
                    deleteItem = item
                    actionItem = null
                }) { Text(stringResource(R.string.action_delete)) }
            },
        )
    }

    deleteItem?.let { item ->
        val deletedMessage = stringResource(R.string.deleted_confirmation, item.product.name)
        AlertDialog(
            onDismissRequest = { deleteItem = null },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(item.product)
                    deleteItem = null
                    scope.launch { snackbarHostState.showSnackbar(deletedMessage) }
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteItem = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ShoppingTab(
    onAddShoppingItem: () -> Unit,
    viewModel: ShoppingViewModel = hiltViewModel(),
) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("shopping_list"),
        ) {
            items(items, key = { it.id }) { product ->
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.cardListItem(),
                )
            }
        }
        FloatingActionButton(
            onClick = onAddShoppingItem,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_shopping_fab"),
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cart_item))
        }
    }
}

@Composable
private fun TimelineTab(viewModel: TimelineViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("timeline_list"),
    ) {
        items(items, key = { it.id }) { product -> TimelineRow(product) }
    }
}
