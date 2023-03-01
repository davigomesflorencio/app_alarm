package xing.dev.alarm_app.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import java.util.*


fun hideSoftKeyboard(context: Context, view: View) {
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}


fun showBasicMessageDialog(message: String, activity: Activity) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
    builder.setMessage(message)
//    builder.apply {
//        this.setPositiveButton("OKAY", DialogInterface.OnClickListener { _, _ ->
//
//        })
//    }
    val dialog: AlertDialog? = builder.create()
    dialog?.show()
}

fun selectDayOfWeek(element: String): Int {
    when (element) {
        "SEG" -> {
            return Calendar.MONDAY
        }
        "TER" -> {
            return Calendar.TUESDAY
        }
        "QUA" -> {
            return Calendar.WEDNESDAY
        }
        "QUI" -> {
            return Calendar.THURSDAY
        }
        "SEX" -> {
            return Calendar.FRIDAY
        }
        "SAB" -> {
            return Calendar.SATURDAY
        }
        "DOM" -> {
            return Calendar.SUNDAY
        }
    }
    return -1
}
