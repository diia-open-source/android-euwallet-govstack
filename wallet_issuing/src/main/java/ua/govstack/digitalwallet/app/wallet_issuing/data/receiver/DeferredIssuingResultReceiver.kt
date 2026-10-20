package ua.govstack.digitalwallet.app.wallet_issuing.data.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ua.govstack.digitalwallet.app.wallet_issuing.R
import ua.govstack.digitalwallet.app.core.util.extensions.getPendingFlags
import ua.govstack.digitalwallet.app.wallet_issuing.util.IssuingActions

internal class DeferredIssuingResultReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != IssuingActions.DEFERRED_ISSUING_RESULT_ACTION) return

        val status = intent.getStringExtra(IssuingActions.DEFERRED_ISSUING_RESULT_STATUS_EXTRA) ?: return
        if (status == IssuingActions.DEFERRED_ISSUING_STATUS_NOT_READY) return

        val docId = intent.getStringExtra(IssuingActions.DEFERRED_ISSUING_RESULT_DOC_ID_EXTRA).orEmpty()
        val error = intent.getStringExtra(IssuingActions.DEFERRED_ISSUING_RESULT_ERROR_EXTRA)

        val (title, text) = when (status) {
            IssuingActions.DEFERRED_ISSUING_STATUS_ISSUED -> {
                context.getString(R.string.deferred_issuing_notification_success_title) to
                        context.getString(R.string.deferred_issuing_notification_success_message)
            }

            IssuingActions.DEFERRED_ISSUING_STATUS_FAILED -> {
                context.getString(R.string.deferred_issuing_notification_failed_title) to (error
                    ?: context.getString(R.string.deferred_issuing_notification_failed_message))
            }

            IssuingActions.DEFERRED_ISSUING_STATUS_EXPIRED -> {
                context.getString(R.string.deferred_issuing_notification_expired_title) to
                        context.getString(R.string.deferred_issuing_notification_expired_message)
            }

            else -> return
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(manager, context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val launchAppPendingIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            ?.let {
                PendingIntent.getActivity(
                    context,
                    docId.hashCode(),
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or getPendingFlags()
                )
            }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(launchAppPendingIntent)
            .build()
        manager.notify(docId.hashCode(), notification)
    }

    private fun ensureChannel(manager: NotificationManager, context: Context) {
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.deferred_issuing_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "deferred_issuing_status_channel"
    }
}
