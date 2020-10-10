package eltonio.projects.politicalcompassquiz.other

import android.content.Context
import android.util.Log
import androidx.core.os.ConfigurationCompat
import eltonio.projects.politicalcompassquiz.models.QuizOptions

object QuizOptionHelper {
    fun saveQuizOption(quizOptionId: Int) {
        val sharedPrefs = appContext.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE).edit()
        sharedPrefs.putInt(PREF_QUIZ_OPTION, quizOptionId)
        sharedPrefs.apply()
    }
    fun loadQuizOption(context: Context): Int {
        //val currentCountry = LocaleHelper.loadCountry(context)
        //Log.i(TAG, "Country is: $currentCountry")
        val defQuizOption: Int = QuizOptions.UKRAINE.id
        val sharedPrefs = appContext.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE)
        val quizOptionId = sharedPrefs.getInt(PREF_QUIZ_OPTION, defQuizOption)
        return quizOptionId
    }
}