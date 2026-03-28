package com.ashokvatika.app.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ashokvatika.app.data.inventory.AshokvatikaDatabase
import com.ashokvatika.app.data.inventory.FertilizersRepository
import com.ashokvatika.app.data.inventory.PlantsRepository
import com.ashokvatika.app.data.inventory.PotsRepository
import com.ashokvatika.app.data.inventory.SoilRepository
import com.ashokvatika.app.model.InventoryRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PlantsRepository(AshokvatikaDatabase.getInstance(application).plantDao())
    val items: StateFlow<List<InventoryRecord>> = repository.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(item: InventoryRecord) = viewModelScope.launch { repository.add(item) }
    fun updateItem(item: InventoryRecord) = viewModelScope.launch { repository.update(item) }
}

class PotsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PotsRepository(AshokvatikaDatabase.getInstance(application).potDao())
    val items: StateFlow<List<InventoryRecord>> = repository.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(item: InventoryRecord) = viewModelScope.launch { repository.add(item) }
    fun updateItem(item: InventoryRecord) = viewModelScope.launch { repository.update(item) }
}

class SoilViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SoilRepository(AshokvatikaDatabase.getInstance(application).soilDao())
    val items: StateFlow<List<InventoryRecord>> = repository.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(item: InventoryRecord) = viewModelScope.launch { repository.add(item) }
    fun updateItem(item: InventoryRecord) = viewModelScope.launch { repository.update(item) }
}

class FertilizersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FertilizersRepository(AshokvatikaDatabase.getInstance(application).fertilizerDao())
    val items: StateFlow<List<InventoryRecord>> = repository.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(item: InventoryRecord) = viewModelScope.launch { repository.add(item) }
    fun updateItem(item: InventoryRecord) = viewModelScope.launch { repository.update(item) }
}

inline fun <reified T : ViewModel> inventoryViewModelFactory(application: Application, crossinline creator: (Application) -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = creator(application) as T
    }
