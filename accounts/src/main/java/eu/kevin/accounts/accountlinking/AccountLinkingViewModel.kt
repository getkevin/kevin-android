package eu.kevin.accounts.accountlinking

import android.net.Uri
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.BuildConfig
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.*
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.entities.KevinWebFrameColorsConfiguration
import eu.kevin.common.extensions.appendQueryParameter
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.plugin.Kevin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

internal class AccountLinkingViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AccountLinkingState, AccountLinkingIntent>(savedStateHandle) {

    override fun getInitialData() = AccountLinkingState()

    override suspend fun handleIntent(intent: AccountLinkingIntent) {
        when (intent) {
            is Initialize -> initialize(
                configuration = intent.configuration,
                kevinWebFrameColorsConfiguration = intent.kevinWebFrameColorsConfiguration,
                defaultLocale = intent.defaultLocale
            )
            is HandleBackClicked -> GlobalRouter.popCurrentFragment()
            is HandleAuthorization -> handleAuthorizationReceived(intent.uri)
        }
    }

    private suspend fun initialize(
        configuration: AccountLinkingFragmentConfiguration,
        kevinWebFrameColorsConfiguration: KevinWebFrameColorsConfiguration,
        defaultLocale: Locale
    ) {
        val baseLinkAccountUrl = if (Kevin.isSandbox()) {
            BuildConfig.KEVIN_SANDBOX_LINK_ACCOUNT_URL
        } else {
            BuildConfig.KEVIN_LINK_ACCOUNT_URL
        }
        val url = appendQueryParametersToUrl(
            url = baseLinkAccountUrl.format(
                configuration.state,
                configuration.selectedBankId
            ),
            kevinWebFrameColorsConfiguration = kevinWebFrameColorsConfiguration,
            deviceLocale = defaultLocale
        )
        updateState {
            it.copy(
                bankRedirectUrl = url
            )
        }
    }

    private fun appendQueryParametersToUrl(
        url: String,
        kevinWebFrameColorsConfiguration: KevinWebFrameColorsConfiguration,
        deviceLocale: Locale
    ): String {
        return url
            .appendQueryParameter(
                key = "lang",
                value = getActiveLocaleCode(deviceLocale)
            )
            .appendQueryParameter(
                key = "cs",
                value = Json.encodeToString(kevinWebFrameColorsConfiguration)
            )
    }

    private fun getActiveLocaleCode(defaultLocale: Locale): String {
        return Kevin.getLocale()?.language ?: defaultLocale.language
    }

    private fun handleAuthorizationReceived(uri: Uri) {
        val status = uri.getQueryParameter("status")
        if (status == "success") {
            val result = AccountLinkingFragmentResult(
                uri.getQueryParameter("requestId")!!,
                uri.getQueryParameter("code")!!
            )
            GlobalRouter.returnFragmentResult(
                AccountLinkingContract,
                FragmentResult.Success(result)
            )
        } else {
            GlobalRouter.returnFragmentResult(AccountLinkingContract, FragmentResult.Canceled)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(owner: SavedStateRegistryOwner) :
        AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return AccountLinkingViewModel(
                handle
            ) as T
        }
    }
}