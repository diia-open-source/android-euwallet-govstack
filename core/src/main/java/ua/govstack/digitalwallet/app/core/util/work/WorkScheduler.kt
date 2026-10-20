package ua.govstack.digitalwallet.app.core.util.work

import androidx.work.WorkManager

interface WorkScheduler {

    fun enqueue(workManager: WorkManager)
}