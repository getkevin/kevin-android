package eu.kevin.core.architecture.interfaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

interface IModelFactory {
    fun getFactory(): ViewModelProvider.Factory
    fun getFactory(viewModel: ViewModel): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return viewModel as T
            }
        }
    }
}