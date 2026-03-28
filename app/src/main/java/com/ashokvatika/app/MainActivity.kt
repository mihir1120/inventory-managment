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
import com.ashokvatika.app.ui.screen.LoginScreen
import com.ashokvatika.app.ui.screen.MainDashboardScreen
import com.ashokvatika.app.ui.theme.AshokvatikaPalette
import com.ashokvatika.app.ui.theme.AshokvatikaTheme

private enum class AppScreen {
    Login,
    MainDashboard,
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
            productTitle = it[0] as String,
            batch = it[1] as Int,
            sale = it[2] as Int,
            rent = it[3] as Int,
            vendorName = it[4] as String,
            vendorPhone = it[5] as String,
            vendorEmail = it[6] as String,
            vendorAddress = it[7] as String,
            vendorWebsite = it[8] as String,
            selectedDateMillis = it[9] as Long,
            photoUri = it[10] as String?,
            saveStamp = it[11] as String,
            saveResultLabel = it[12] as String?
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

    fun openEditor(module: InventoryModule) {
        currentModule = module
        dashboardDraft = DashboardDraft()
        currentScreen = AppScreen.Editor
    }

    fun saveDashboardItem(item: InventoryRecord) {
        when (currentModule) {
            InventoryModule.Plants -> plantsViewModel.addItem(item)
            InventoryModule.Supplies -> fertilizersViewModel.addItem(item)
            InventoryModule.Tools -> soilViewModel.addItem(item)
            InventoryModule.Pots -> potsViewModel.addItem(item)
        }
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
                    onPlantsClick = { openEditor(InventoryModule.Plants) },
                    onSuppliesClick = { openEditor(InventoryModule.Supplies) },
                    onToolsClick = { openEditor(InventoryModule.Tools) },
                    onPotsClick = { openEditor(InventoryModule.Pots) }
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
                        currentScreen = AppScreen.MainDashboard
                    }
                )
            }
        }
    }
}
