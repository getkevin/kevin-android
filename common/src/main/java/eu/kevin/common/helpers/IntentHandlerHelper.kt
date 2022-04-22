package eu.kevin.common.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

object IntentHandlerHelper {
    fun getIntentForUri(context: Context, uri: Uri): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getHandlerIntentApi30(uri)
        } else {
            getHandlerIntent(context, uri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getHandlerIntentApi30(uri: Uri): Intent {
        return Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            flags = Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
        }
    }

    private fun getHandlerIntent(context: Context, uri: Uri): Intent? {
        val defaultBrowser = getDefaultBrowserPackage(context)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val resolveInfo = context.packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        val intentPackage = resolveInfo?.activityInfo?.packageName
        return if (defaultBrowser != null && intentPackage != null && defaultBrowser != intentPackage) {
            intent
        } else {
            null
        }
    }

    private fun getDefaultBrowserPackage(context: Context): String? {
        val defaultBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val defaultBrowserResolveInfo = context.packageManager
            .resolveActivity(defaultBrowserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        return defaultBrowserResolveInfo?.activityInfo?.packageName
    }
}