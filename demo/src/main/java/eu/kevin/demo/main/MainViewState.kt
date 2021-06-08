package eu.kevin.demo.main

sealed class MainViewState {
    data class Loading(val isLoading: Boolean): MainViewState()
}