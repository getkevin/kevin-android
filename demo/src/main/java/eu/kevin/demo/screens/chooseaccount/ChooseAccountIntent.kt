package eu.kevin.demo.screens.chooseaccount

import eu.kevin.common.architecture.interfaces.Intent

internal sealed class ChooseAccountIntent : Intent {
    data class OnAccountChosen(val id: Long) : ChooseAccountIntent()
}