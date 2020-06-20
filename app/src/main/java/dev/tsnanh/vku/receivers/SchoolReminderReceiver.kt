package dev.tsnanh.vku.receivers

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.PowerManager
import androidx.annotation.RequiresApi
import dev.tsnanh.vku.R
import dev.tsnanh.vku.domain.entities.Resource
import dev.tsnanh.vku.domain.usecases.RetrieveUserTimetableUseCase
import dev.tsnanh.vku.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.util.*

class SchoolReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock =
                powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.WAKE_LOCK_TAG)
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
            // [START] Prepare data
            Timber.d("Receiver called")
            val retrieveTimetableUseCase by KoinJavaComponent.inject(RetrieveUserTimetableUseCase::class.java)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val email = intent.getStringExtra("email")
            val partOfTheDay = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 -> 0
                12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 -> 1
                else -> throw IllegalArgumentException("Wrong hour")
            }
            // [END]
            when (val result =
                withContext(Dispatchers.IO) {
                    retrieveTimetableUseCase.invoke(
                        "http://daotao.sict.udn.vn/tkb",
                        email!!
                    )
                }) {
                is Resource.Error -> Timber.d(result.message)
                is Resource.Success -> {
                    val dayOfWeek = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
                    val list = result.data!!.filter { subject ->
                        dayOfWeekFilter(subject, dayOfWeek)
                    }
                    if (list.isEmpty()) {
                        notificationManager.sendSchoolReminderNotification(
                            context
                                .getString(R.string.title_notification_school_reminder_no_subject),
                            context
                                .getString(R.string.content_notification_school_reminder_no_subject),
                            context
                        )
                    } else {
                        when (partOfTheDay) {
                            0 -> {
                                val morningSubjects = list.filter { subject ->
                                    subject.week.trim()
                                        .isNotEmpty() && subject.lesson.trim()[0].toString()
                                        .matches(Regex("[1-5]"))
                                }
                                if (morningSubjects.isEmpty()) {
                                    notificationManager.sendSchoolReminderNotification(
                                        context
                                            .getString(R.string.title_notification_school_reminder_no_subject_morning),
                                        context
                                            .getString(R.string.content_notification_school_reminder_no_subject_morning),
                                        context
                                    )
                                } else {
                                    notificationManager.sendSchoolReminderNotification(
                                        "cc",
                                        "cc",
                                        context
                                    )
                                }
                            }
                            1 -> {
                                val afternoonSubjects = list.filter { subject ->
                                    subject.week.trim()
                                        .isNotEmpty() && subject.lesson.trim()[0].toString()
                                        .matches(Regex("[6-9]"))
                                }
                                if (afternoonSubjects.isEmpty()) {
                                    notificationManager.sendSchoolReminderNotification(
                                        context
                                            .getString(R.string.title_notification_school_reminder_no_subject_afternoon),
                                        context
                                            .getString(R.string.content_notification_school_reminder_no_subject_afternoon),
                                        context
                                    )
                                } else {
                                    notificationManager.sendSchoolReminderNotification(
                                        "cc",
                                        "cc",
                                        context
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= M) {
                setAlarmApi23AndAbove(context, intent)
            }
            wakeLock.release()
        }
    }

    @RequiresApi(M)
    private fun setAlarmApi23AndAbove(
        context: Context,
        intent: Intent
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RC_SCHOOL_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        when (Calendar.getInstance()[Calendar.HOUR_OF_DAY]) {
            6 -> alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendarMorning.timeInMillis + AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            12 -> alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendarAfternoon.timeInMillis + AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            18 -> alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendarEvening.timeInMillis + AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            21 -> alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendarNight.timeInMillis + AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }
}