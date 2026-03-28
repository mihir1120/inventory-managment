package com.ashokvatika.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashokvatika.app.model.InventoryRecord
import com.ashokvatika.app.ui.inventory.FertilizersViewModel
import com.ashokvatika.app.ui.inventory.PlantsViewModel
import com.ashokvatika.app.ui.inventory.PotsViewModel
import com.ashokvatika.app.ui.inventory.SoilViewModel
import com.ashokvatika.app.ui.inventory.inventoryViewModelFactory
import com.ashokvatika.app.ui.screen.DashboardDraft
import com.ashokvatika.app.ui.screen.DashboardScreen
import com.ashokvatika.app.ui.screen.InventoryListScreen
import com.ashokvatika.app.ui.screen.LoginScreen
import com.ashokvatika.app.ui.screen.MainDashboardScreen
import com.ashokvatika.app.ui.theme.AshokvatikaPalette
import com.ashokvatika.app.ui.theme.AshokvatikaTheme
import java.text.SimpleDateFormat
import java.util.Locale

private enum class AppScreen {
    Login,
    MainDashboard,
    InventoryList,
    Editor
}

private enum class InventoryModule {
    Plants,
    Supplies,
    Tools,
    Pots
}

private val DashboardDraftSaver: Saver<DashboardDraft, List<Any?>> = Saver(
    save = {
        listOf(
            it.id,
            it.productTitle,
            it.batch,
            it.sale,
            it.rent,
            it.vendorName,
            it.vendorPhone,
            it.vendorEmail,
            it.vendorAddress,
            it.vendorWebsite,
            it.selectedDateMillis,
            it.photoUri,
            it.saveStamp,
            it.saveResultLabel
        )
    },
    restore = {
        DashboardDraft(
            id = it[0] as Int,
            productTitle = it[1] as String,
            batch = it[2] as Int,
            sale = it[3] as Int,
            rent = it[4] as Int,
            vendorName = it[5] as String,
            vendorPhone = it[6] as String,
            vendorEmail = it[7] as String,
            vendorAddress = it[8] as String,
            vendorWebsite = it[9] as String,
            selectedDateMillis = it[10] as Long,
            photoUri = it[11] as String?,
            saveStamp = it[12] as String,
            saveResultLabel = it[13] as String?
        )
    }
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AshokvatikaTheme { AshokvatikaApp() }
        }
    }
}

@Composable
private fun AshokvatikaApp() {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Login) }
    var currentModule by rememberSaveable { mutableStateOf(InventoryModule.Plants) }
    var dashboardDraft by rememberSaveable(stateSaver = DashboardDraftSaver) { mutableStateOf(DashboardDraft()) }

    val application = LocalContext.current.applicationContext as Application
    val plantsViewModel: PlantsViewModel = viewModel(factory = inventoryViewModelFactory(application) { PlantsViewModel(it) })
    val potsViewModel: PotsViewModel = viewModel(factory = inventoryViewModelFactory(application) { PotsViewModel(it) })
    val soilViewModel: SoilViewModel = viewModel(factory = inventoryViewModelFactory(application) { SoilViewModel(it) })
    val fertilizersViewModel: FertilizersViewModel = viewModel(factory = inventoryViewModelFactory(application) { FertilizersViewModel(it) })

    val plantItems by plantsViewModel.items.collectAsState()
    val potItems by potsViewModel.items.collectAsState()
    val toolItems by soilViewModel.items.collectAsState()
    val supplyItems by fertilizersViewModel.items.collectAsState()

    fun openModule(module: InventoryModule) {
        currentModule = module
        currentScreen = AppScreen.InventoryList
    }

    fun openNewEditor(module: InventoryModule = currentModule) {
        currentModule = module
        dashboardDraft = DashboardDraft()
        currentScreen = AppScreen.Editor
    }

    fun openExistingItem(item: InventoryRecord) {
        dashboardDraft = item.toDashboardDraft()
        currentScreen = AppScreen.Editor
    }

    fun saveDashboardItem(item: InventoryRecord) {
        when (currentModule) {
            InventoryModule.Plants -> if (item.id == 0) plantsViewModel.addItem(item) else plantsViewModel.updateItem(item)
            InventoryModule.Supplies -> if (item.id == 0) fertilizersViewModel.addItem(item) else fertilizersViewModel.updateItem(item)
            InventoryModule.Tools -> if (item.id == 0) soilViewModel.addItem(item) else soilViewModel.updateItem(item)
            InventoryModule.Pots -> if (item.id == 0) potsViewModel.addItem(item) else potsViewModel.updateItem(item)
        }
        dashboardDraft = DashboardDraft()
        currentScreen = AppScreen.InventoryList
    }

    val currentModuleTitle = when (currentModule) {
        InventoryModule.Plants -> "Plants"
        InventoryModule.Supplies -> "Supplies"
        InventoryModule.Tools -> "Tools"
        InventoryModule.Pots -> "Pots"
    }

    val currentModuleItems = when (currentModule) {
        InventoryModule.Plants -> plantItems
        InventoryModule.Supplies -> supplyItems
        InventoryModule.Tools -> toolItems
        InventoryModule.Pots -> potItems
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(AshokvatikaPalette.colors.background)) {
            when (currentScreen) {
                AppScreen.Login -> LoginScreen(
                    onLoginSuccess = {
                        currentScreen = AppScreen.MainDashboard
                    }
                )

                AppScreen.MainDashboard -> MainDashboardScreen(
                    onLogout = {
                        dashboardDraft = DashboardDraft()
                        currentScreen = AppScreen.Login
                    },
                    onPlantsClick = { openModule(InventoryModule.Plants) },
                    onSuppliesClick = { openModule(InventoryModule.Supplies) },
                    onToolsClick = { openModule(InventoryModule.Tools) },
                    onPotsClick = { openModule(InventoryModule.Pots) }
                )

                AppScreen.InventoryList -> InventoryListScreen(
                    title = currentModuleTitle,
                    items = currentModuleItems,
                    onLogout = {
                        dashboardDraft = DashboardDraft()
                        currentScreen = AppScreen.Login
                    },
                    onBack = {
                        dashboardDraft = DashboardDraft()
                        currentScreen = AppScreen.MainDashboard
                    },
                    onAddItem = { openNewEditor() },
                    onItemClick = { openExistingItem(it) }
                )

                AppScreen.Editor -> DashboardScreen(
                    onLogout = {
                        dashboardDraft = DashboardDraft()
                        currentScreen = AppScreen.Login
                    },
                    onSaveInventory = ::saveDashboardItem,
                    draft = dashboardDraft,
                    onDraftChange = { dashboardDraft = it },
                    onBack = {
                        dashboardDraft = DashboardDraft()
                        currentScreen = AppScreen.InventoryList
                    }
                )
            }
        }
    }
}

private fun InventoryRecord.toDashboardDraft(): DashboardDraft = DashboardDraft(
    id = id,
    productTitle = title,
    batch = batch,
    sale = sale,
    rent = rent,
    vendorName = vendor.ifBlank { "Vendor" },
    vendorPhone = vendorPhone,
    vendorEmail = vendorEmail,
    vendorAddress = vendorAddress,
    vendorWebsite = vendorWebsite,
    selectedDateMillis = parseStorageDate(date),
    photoUri = photoUri,
    saveStamp = "save",
    saveResultLabel = null
)

private fun parseStorageDate(value: String): Long {
    return runCatching {
        SimpleDateFormat("dd-MM-yy", Locale.getDefault()).parse(value)?.time
    }.getOrNull() ?: System.currentTimeMillis()
}

