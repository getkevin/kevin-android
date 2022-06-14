package eu.kevin.demo.screens.chooseaccount

import eu.kevin.common.architecture.interfaces.IIntent

sealed class ChooseAccountIntent : IIntent {
    data class OnAccountChosen(val id: Long) : ChooseAccountIntent()
}