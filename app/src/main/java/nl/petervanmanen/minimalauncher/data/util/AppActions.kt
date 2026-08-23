package nl.petervanmanen.minimalauncher.data.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun launchApp(context: Context, packageName: String) {
    context.packageManager.getLaunchIntentForPackage(packageName)?.let { context.startActivity(it) }
}

fun requestUninstall(context: Context, packageName: String) {
    val intent = Intent(Intent.ACTION_DELETE, Uri.fromParts("package", packageName, null))
    context.startActivity(intent)
}

fun openAppInfo(context: Context, packageName: String) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    )
    context.startActivity(intent)
}
