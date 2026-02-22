package app.expense.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import app.expense.tracker.services.SMSSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachUI()
        syncSMS()
    }

    private fun attachUI() {
        setContent {
            ExpenseTrackerMain()
        }
    }

    private fun syncSMS() {
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "SMS_SYNC",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequest.Builder(
                    SMSSyncWorker::class.java,
                    Duration.ofMinutes(15),
                    Duration.ofMinutes(5)
                ).build()
            )
    }
}
