package io.github.anshu7vyas.stocked.work

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.anshu7vyas.stocked.HomeActivity
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.StockedApp
import io.github.anshu7vyas.stocked.data.ProductRepository

/**
 * Daily check for items expiring today or tomorrow. Unlike the legacy app, which
 * only checked when the user opened the Home screen, this fires in the background.
 */
@HiltWorker
class ExpiryCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ProductRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        repository.expireOverdue()

        val expiring = repository.expiringSoon()
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (expiring.isNotEmpty() && notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID, buildNotification(expiring.size))
        }
        return Result.success()
    }

    private fun buildNotification(count: Int): Notification {
        val intent = Intent(applicationContext, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(applicationContext, StockedApp.CHANNEL_EXPIRY)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(
                applicationContext.resources.getQuantityString(
                    R.plurals.expiry_notification_text, count, count,
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    companion object {
        const val WORK_NAME = "expiry_check"
        const val NOTIFICATION_ID = 0
    }
}
