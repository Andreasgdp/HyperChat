package utils

import android.app.Activity
import android.content.Context
import android.content.Intent




class ActivityRouting(currentActivityContext: Activity) {

    private var currentActivity: Activity? = null

    init {
        currentActivity = currentActivityContext
    }

    public fun goToActivity(activity: Class<*>) {
        val intent = Intent(currentActivity, activity)
        currentActivity?.startActivity(intent)
    }

    public fun clearCurrentAndGoToActivity(activity: Class<*>) {
        val intent = Intent(currentActivity, activity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        currentActivity?.startActivity(intent)
        currentActivity?.finish() // if the activity running has it's own context
        // view.getContext().finish() for fragments etc.
    }
}