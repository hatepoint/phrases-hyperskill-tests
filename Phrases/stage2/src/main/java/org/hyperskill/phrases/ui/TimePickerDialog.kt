package org.hyperskill.phrases.ui

import android.app.Dialog
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.DialogFragment

class TimePickerDialog : DialogFragment() {
    private val defaultHour = 9
    private val defaultMinute = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return android.app.TimePickerDialog(activity, activity as android.app.TimePickerDialog.OnTimeSetListener, defaultHour, defaultMinute, is24HourFormat(activity))
    }
}