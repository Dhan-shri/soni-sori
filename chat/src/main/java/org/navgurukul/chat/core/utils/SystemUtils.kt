package org.navgurukul.chat.core.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.merakilearn.core.extentions.toast
import org.navgurukul.chat.R
import org.navgurukul.chat.features.notifications.NotificationUtils

/**
 * Tells if the application ignores battery optimizations.
 *
 * Ignoring them allows the app to run in background to make background sync with the homeserver.
 * This user option appears on Android M but Android O enforces its usage and kills apps not
 * authorised by the user to run in background.
 *
 * @param context the context
 * @return true if battery optimisations are ignored
 */
fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    // no issue before Android M, battery optimisations did not exist
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || (context.getSystemService(Context.POWER_SERVICE) as PowerManager?)?.isIgnoringBatteryOptimizations(context.packageName) == true
}

fun isAirplaneModeOn(context: Context): Boolean {
    return Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
}

/**
 * display the system dialog for granting this permission. If previously granted, the
 * system will not show it (so you should call this method).
 *
 * Note: If the user finally does not grant the permission, PushManager.isBackgroundSyncAllowed()
 * will return false and the notification privacy will fallback to "LOW_DETAIL".
 */
@TargetApi(Build.VERSION_CODES.M)
fun requestDisablingBatteryOptimization(activity: Activity, fragment: Fragment?, requestCode: Int) {
    val intent = Intent()
    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    intent.data = Uri.parse("package:" + activity.packageName)
    if (fragment != null) {
        fragment.startActivityForResult(intent, requestCode)
    } else {
        activity.startActivityForResult(intent, requestCode)
    }
}

// ==============================================================================================================
// Clipboard helper
// ==============================================================================================================

/**
 * Copy a text to the clipboard, and display a Toast when done
 *
 * @param context the context
 * @param text    the text to copy
 */
fun copyToClipboard(context: Context, text: CharSequence, showToast: Boolean = true, @StringRes toastMessage: Int = R.string.copied_to_clipboard) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("", text))
    if (showToast) {
        context.toast(toastMessage)
    }
}

/**
 * Shows notification settings for the current app.
 * In android O will directly opens the notification settings, in lower version it will show the App settings
 */
fun startNotificationSettingsIntent(activity: AppCompatActivity, requestCode: Int) {
    val intent = Intent()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra("app_package", activity.packageName)
        intent.putExtra("app_uid", activity.applicationInfo?.uid)
    } else {
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
    }
    activity.startActivityForResult(intent, requestCode)
}

/**
 * Shows notification system settings for the given channel id.
 */
@TargetApi(Build.VERSION_CODES.O)
fun startNotificationChannelSettingsIntent(fragment: Fragment, channelID: String) {
    if (!NotificationUtils.supportNotificationChannels()) return
    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, fragment.context?.packageName)
        putExtra(Settings.EXTRA_CHANNEL_ID, channelID)
    }
    fragment.startActivity(intent)
}

fun startAddGoogleAccountIntent(context: AppCompatActivity, requestCode: Int) {
    try {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        context.startActivityForResult(intent, requestCode)
    } catch (activityNotFoundException: ActivityNotFoundException) {
        context.toast(R.string.error_no_external_application_found)
    }
}

fun startSharePlainTextIntent(fragment: Fragment, chooserTitle: String?, text: String, subject: String? = null, requestCode: Int? = null) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    } else {
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    // Add data to the intent, the receiving app will decide what to do with it.
    share.putExtra(Intent.EXTRA_SUBJECT, subject)
    share.putExtra(Intent.EXTRA_TEXT, text)
    try {
        if (requestCode != null) {
            fragment.startActivityForResult(Intent.createChooser(share, chooserTitle), requestCode)
        } else {
            fragment.startActivity(Intent.createChooser(share, chooserTitle))
        }
    } catch (activityNotFoundException: ActivityNotFoundException) {
        fragment.activity?.toast(R.string.error_no_external_application_found)
    }
}

fun startImportTextFromFileIntent(fragment: Fragment, requestCode: Int) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "text/plain"
    }
    if (intent.resolveActivity(fragment.requireActivity().packageManager) != null) {
        fragment.startActivityForResult(intent, requestCode)
    } else {
        fragment.activity?.toast(R.string.error_no_external_application_found)
    }
}
