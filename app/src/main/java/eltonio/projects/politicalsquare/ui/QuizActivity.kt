package eltonio.projects.politicalsquare.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import eltonio.projects.politicalsquare.*
import eltonio.projects.politicalsquare.data.AppViewModel
import eltonio.projects.politicalsquare.models.QuestionWithAnswers
import eltonio.projects.politicalsquare.models.*
import eltonio.projects.politicalsquare.App.Companion.appQuestionsWithAnswers
import eltonio.projects.politicalsquare.data.AppRepository
import eltonio.projects.politicalsquare.util.*

import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

class QuizActivity : BaseActivity(), View.OnTouchListener {

    // TODO: MVVM to VM
    //TEMP
    private var localRepo = AppRepository.Local()
    private var cloudRepo = AppRepository.Cloud()

    private var previousStep: Step? = null
    private var isPreviousStep = false
    private var questionCountTotal = 0
    private var questionCounter = 0
    private var horizontalScore = 0f
    private var verticalScore = 0f

    private var zeroAnswerCnt = 0

    private var quizFinished = false

    private lateinit var currentQuestion: QuestionWithAnswers

    private lateinit var radioShapeHover1: GradientDrawable
    private lateinit var radioShapeHover2: GradientDrawable
    private lateinit var radioShapeHover3: GradientDrawable
    private lateinit var radioShapeHover4: GradientDrawable
    private lateinit var radioShapeHover5: GradientDrawable

    private lateinit var radioShapeHoverList: MutableList<GradientDrawable>
    private var rbSelectedIndex = -1

    lateinit var appViewModel: AppViewModel
    lateinit var scope: CoroutineScope

    // end VM

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // TODO: V
        this.title = getString(R.string.quiz_title_actionbar)

        appViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        scope = CoroutineScope(Dispatchers.IO)
        // end

        // TODO: VM - to vm
        cloudRepo.logQuizBeginEvent()
        questionCountTotal = appQuestionsWithAnswers.size
        // end

        // TODO: V
        radio_answer_3.visibility = View.GONE // DISABLE "Hard to answer" radio

        // Listeners
        fab_undo.setOnClickListener { showPrevQuestion() }
        radio_answer_1.setOnTouchListener(this)
        radio_answer_2.setOnTouchListener(this)
        radio_answer_3.setOnTouchListener(this)
        radio_answer_4.setOnTouchListener(this)
        radio_answer_5.setOnTouchListener(this)
        // end

        // TODO: V - to method
        // == Radio button hovers ==
        // Get shape_radio_hover for every radio button
        radioShapeHover1 = ContextCompat.getDrawable(this, R.drawable.shape_radio_hover) as GradientDrawable
        radioShapeHover2 = ContextCompat.getDrawable(this, R.drawable.shape_radio_hover) as GradientDrawable
        radioShapeHover3 = ContextCompat.getDrawable(this, R.drawable.shape_radio_hover) as GradientDrawable
        radioShapeHover4 = ContextCompat.getDrawable(this, R.drawable.shape_radio_hover) as GradientDrawable
        radioShapeHover5 = ContextCompat.getDrawable(this, R.drawable.shape_radio_hover) as GradientDrawable

        (radio_answer_1.background as LayerDrawable).setDrawableByLayerId(R.id.shape_radio_hover, radioShapeHover1)
        (radio_answer_2.background as LayerDrawable).setDrawableByLayerId(R.id.shape_radio_hover, radioShapeHover2)
        (radio_answer_3.background as LayerDrawable).setDrawableByLayerId(R.id.shape_radio_hover, radioShapeHover3)
        (radio_answer_4.background as LayerDrawable).setDrawableByLayerId(R.id.shape_radio_hover, radioShapeHover4)
        (radio_answer_5.background as LayerDrawable).setDrawableByLayerId(R.id.shape_radio_hover, radioShapeHover5)

        radioShapeHoverList = mutableListOf(
            radioShapeHover1,
            radioShapeHover2,
            radioShapeHover3,
            radioShapeHover4,
            radioShapeHover5
        )

        // Reset all shape_radio_hover to prevent bug
        for (item in radioShapeHoverList) {
            item.setColor(ContextCompat.getColor(this@QuizActivity, android.R.color.transparent))
        }
        // end

        // TODO: VM - to vm
        showNextQuestion()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v) {
            radio_answer_1 -> {
                Log.i(TAG, "Checked Index: ${radio_group_answers.indexOfChild(radio_answer_1)}")

                rbSelectedIndex = radio_group_answers.indexOfChild(radio_answer_1)

                if (event?.action == MotionEvent.ACTION_DOWN) {
                    resetRadioToDefault(radio_answer_1)
                }
                if (event?.action == MotionEvent.ACTION_UP) {
                    radio_answer_1.isChecked = true
                    checkAnswer(rbSelectedIndex)
                    showNextQuestion()
                }
            }

            radio_answer_2 -> {
                Log.i(TAG, "Checked: ${radio_group_answers.indexOfChild(radio_answer_2)}")

                rbSelectedIndex = radio_group_answers.indexOfChild(radio_answer_2)

                if (event?.action == MotionEvent.ACTION_DOWN) {
                    resetRadioToDefault(radio_answer_2)
                }
                if (event?.action == MotionEvent.ACTION_UP) {
                    radio_answer_2.isChecked = true
                    checkAnswer(rbSelectedIndex)
                    showNextQuestion()
                }
            }

            radio_answer_3 -> {
                Log.i(TAG, "Checked: ${radio_group_answers.indexOfChild(radio_answer_3)}")

                rbSelectedIndex = radio_group_answers.indexOfChild(radio_answer_3)

                if (event?.action == MotionEvent.ACTION_DOWN) {
                    resetRadioToDefault(radio_answer_3)
                }
                if (event?.action == MotionEvent.ACTION_UP) {
                    radio_answer_3.isChecked = true
                    checkAnswer(rbSelectedIndex)
                    showNextQuestion()
                }
            }

            radio_answer_4 -> {
                Log.i(TAG, "Checked: ${radio_group_answers.indexOfChild(radio_answer_4)}")

                rbSelectedIndex = radio_group_answers.indexOfChild(radio_answer_4)

                if (event?.action == MotionEvent.ACTION_DOWN) {
                    resetRadioToDefault(radio_answer_4)
                }
                if (event?.action == MotionEvent.ACTION_UP) {
                    radio_answer_4.isChecked = true
                    checkAnswer(rbSelectedIndex)
                    showNextQuestion()
                }
            }

            radio_answer_5 -> {
                Log.i(TAG, "Checked: ${radio_group_answers.indexOfChild(radio_answer_5)}")

                rbSelectedIndex = radio_group_answers.indexOfChild(radio_answer_5)

                if (event?.action == MotionEvent.ACTION_DOWN) {
                    resetRadioToDefault(radio_answer_5)
                }
                if (event?.action == MotionEvent.ACTION_UP) {
                    radio_answer_5.isChecked = true
                    checkAnswer(rbSelectedIndex)
                    showNextQuestion()
                }
            }
        }
        return false
    }

    override fun onBackPressed() {
        showEndQuizDialogLambda(this) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /** CUSTOM METHODS */
    private fun checkAnswer(radioSelectedIndex: Int) {
        if (isPreviousStep) return // Need to check, because setOnCheckedChangeListener shoots automatically after radio changed

        if (radio_answer_3.isChecked) zeroAnswerCnt++

        val rbSelected = findViewById<RadioButton>(radio_group_answers.checkedRadioButtonId)
        val point = currentQuestion.answerList[radioSelectedIndex].point
        val scale = currentQuestion.scale
        val step = Step()
        if (scale == "horizontal") horizontalScore += point else verticalScore += point

        // Save a current step
        step.let {
            it.questionIndex = questionCounter-1
            it.rbIndex = radioSelectedIndex
            it.rbSelected = rbSelected
            it.scale = scale
            it.point = point
        }

        // Save as a previous step
        previousStep = step

        startShowFABAnimation()
    }


    private fun showNextQuestion() {

        if (fab_undo.isEnabled != true) fab_undo.isEnabled = true // TODO: V

        // TODO: VM - to vm
        if (questionCounter < questionCountTotal) {
            radio_group_answers.clearCheck() // TODO: V - livedata<clearCheckTriggerEvent>

            // TODO: VM - to vm
            currentQuestion = appQuestionsWithAnswers[questionCounter]

            // TODO: V - livedata<questionNewVisible>, livedata<questionOldVisible>
            text_question_new.visibility = View.VISIBLE
            text_question_old.visibility = View.VISIBLE

            // TODO: VM - to vm
            val ans = currentQuestion.answerList
            when (defaultLang) {
                "uk" -> {
                    if (questionCounter > 0)
                        text_question_old.text = // TODO: V - livedate<qustionOld>
                            appQuestionsWithAnswers[questionCounter - 1].questionUk

                    text_question_new.text = currentQuestion.questionUk //TODO: V - livedate<qustionNew>
                    // TODO: Refactoring, transfer answers to Strings
                    // TODO: V
                    radio_answer_1.text = ans[0].answerUk
                    radio_answer_2.text = ans[1].answerUk
                    radio_answer_3.text = ans[2].answerUk
                    radio_answer_4.text = ans[3].answerUk
                    radio_answer_5.text = ans[4].answerUk
                    // end
                }
                "ru" -> {
                    if (questionCounter > 0) text_question_old.text = appQuestionsWithAnswers[questionCounter - 1].questionRu

                    text_question_new.text = currentQuestion.questionRu //TODO: V - livedate<qustionNew>
                    //TODO: Refactoring, transfer answers to Strings
                    // TODO: V
                    radio_answer_1.text = ans[0].answerRu
                    radio_answer_2.text = ans[1].answerRu
                    radio_answer_3.text = ans[2].answerRu
                    radio_answer_4.text = ans[3].answerRu
                    radio_answer_5.text = ans[4].answerRu
                    // end
                }
                "en" -> {
                    if (questionCounter > 0) text_question_old.text = appQuestionsWithAnswers[questionCounter - 1].questionEn

                    text_question_new.text = currentQuestion.questionEn //TODO: V - livedate<qustionNew>
                    //TODO: Refactoring, transfer answers to Strings
                    // TODO: V
                    radio_answer_1.text = ans[0].answerEn
                    radio_answer_2.text = ans[1].answerEn
                    radio_answer_3.text = ans[2].answerEn
                    radio_answer_4.text = ans[3].answerEn
                    radio_answer_5.text = ans[4].answerEn
                    // end
                }
                // end
            }

            // TODO: V - debug
            val size = getScreenResolution(this)
            val width = size.x
            val height = size.y

            Log.i(TAG, "$width and $height")
            // end

            // TODO: V
            startOldQuestionAnimation()
            startNewQuestionAnimation()
            startProgressBarAnimation()
            // end

            // TODO: VM - to vm
            questionCounter++
        } else {
            // Finish Quiz
            // TODO: VM - to vm
            quizFinished = true // todo: VM - livedata
            Log.i(TAG, "Vertical Score: $verticalScore, horizontal score: $horizontalScore")

            localRepo.setZeroAnswerCht(zeroAnswerCnt)
            localRepo.setHorScore(horizontalScore.toInt())
            localRepo.setVerScore(verticalScore.toInt())
            // end

            // TODO: V
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
            slideLeft(this) //quiz in
            // end
        }
    }

    private fun showPrevQuestion() {
        // TODO: VM - to vm, livedata
        isPreviousStep = true

        // TODO: VM - to vm
        if (questionCounter > 1) {
            questionCounter-- // reset a next question to current

            currentQuestion = appQuestionsWithAnswers[questionCounter-1] // take the previous question
            // end

            // TODO: V - livedata<questionNewVisible>, livedata<questionOldVisible>
            text_question_new.visibility = View.VISIBLE
            text_question_old.visibility = View.VISIBLE

            // TODO: VM - to vm
            val ans = currentQuestion.answerList
            when (defaultLang) {
                "uk" -> {
                    text_question_old.text = currentQuestion.questionUk // TODO: V - livedate<qustionOld>
                    //TODO: Refactoring, transfer answers to Strings
                    // TODO: V
                    radio_answer_1.text = ans[0].answerUk
                    radio_answer_2.text = ans[1].answerUk
                    radio_answer_3.text = ans[2].answerUk
                    radio_answer_4.text = ans[3].answerUk
                    radio_answer_5.text = ans[4].answerUk
                }
                "ru" -> {
                    text_question_old.text = currentQuestion.questionRu // TODO: V - livedate<qustionOld>
                    //TODO: Refactoring, transfer answers to Strings
                    // TODO: V
                    radio_answer_1.text = ans[0].answerRu
                    radio_answer_2.text = ans[1].answerRu
                    radio_answer_3.text = ans[2].answerRu
                    radio_answer_4.text = ans[3].answerRu
                    radio_answer_5.text = ans[4].answerRu
                }
                "en" -> {
                    text_question_old.text = currentQuestion.questionEn // TODO: V - livedate<qustionOld>
                    //TODO: Refactoring, transfer answers to Strings
                    // TODO: V
                    radio_answer_1.text = ans[0].answerEn
                    radio_answer_2.text = ans[1].answerEn
                    radio_answer_3.text = ans[2].answerEn
                    radio_answer_4.text = ans[3].answerEn
                    radio_answer_5.text = ans[4].answerEn
                }
            }
            // end

            // TODO: V
            startOldQuestionBackwardAnimation()
            startNewQuestionBackwardAnimation()
            startProgressBarBackwardAnimation()

            text_questions_left.text = "${questionCounter} / $questionCountTotal" // TODO: V - livedata...
            // end

            // Save a previous state
            // TODO: VM - to vm
            val prevStep = previousStep

            if (prevStep != null) {
                // TODO: Refactor - get rid of Radio in Step
                prevStep.rbSelected?.isChecked = true // todo: V - livedata<prevStepChecked>
                // TODO: V
                fadeInOldAnswer(prevStep.rbSelected, radioShapeHoverList[prevStep.rbIndex])

                // TODO: Refactor: get rid of zero answer
                if (prevStep.rbSelected == radio_answer_3) zeroAnswerCnt-- // a zero answer
                // For debug
                Log.d(TAG, "zeroAnswerCnt: $zeroAnswerCnt")

                // TODO: VM - vm
                if (prevStep.scale == "horizontal")
                    horizontalScore -= prevStep.point
                else
                    verticalScore -= prevStep.point
            }
        }

        // Clear the previous step
        // TODO: VM - to VM, method
        previousStep = null
        isPreviousStep = false

        // TODO: V
        startHideFABAnimation()
    }

    // TODO: V
    private fun fadeInOldAnswer(radioButtonSelected: RadioButton?, radioShapeHover: GradientDrawable) {
        if (radioButtonSelected != null) {
            val radioBackground = radioButtonSelected.background as LayerDrawable
            val rippleEffect = radioBackground.findDrawableByLayerId(R.id.shape_radio_ripple) as RippleDrawable
            rippleEffect.apply {
                setColor(ContextCompat.getColorStateList(this@QuizActivity, R.color.selector_ripple_effect_oldselected))
                setHotspot(10f, 10f)
                state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
                setColor(ContextCompat.getColorStateList(this@QuizActivity, R.color.selector_ripple_effect_normal))
            }

            Handler().postDelayed({
                rippleEffect.state = intArrayOf()
                rippleEffect.setColor(ContextCompat.getColorStateList(this, R.color.selector_ripple_effect_oldselected))
                radioShapeHover.setColor(ContextCompat.getColor(this@QuizActivity, R.color.quiz_answer_old_selected))

                radioShapeHover.alpha = 0
                ValueAnimator.ofArgb(0, 255).apply {
                    duration = 200 //200
                    addUpdateListener {
                        radioShapeHover.setColor(ContextCompat.getColor(this@QuizActivity, R.color.quiz_answer_old_selected))
                        radioShapeHover.alpha = this.animatedValue as Int
                    }
                    start()
                }
            }, 200)
        }
    }

    private fun resetRadioToDefault(radioAnswer: RadioButton?) {
        // Fade out all old selections
        for (item in radioShapeHoverList) {
            item.alpha = 255
            ValueAnimator.ofArgb(255, 0).apply {
                duration = 300
                addUpdateListener {
                    item.alpha = this.animatedValue as Int
                }
                addListener (object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        item.setColor(ContextCompat.getColor(this@QuizActivity, android.R.color.transparent))
                    }
                })
                start()
            }
        }
        if (radioAnswer != null) {
            val radioBackground = radioAnswer.background as LayerDrawable
            val rippleEffect = radioBackground.findDrawableByLayerId(R.id.shape_radio_ripple) as RippleDrawable
            val colorStateList = ContextCompat.getColorStateList(this, R.color.selector_ripple_effect_normal)
            rippleEffect.setColor(colorStateList)
        }
    }

    // ANIMATION METHODS
    private fun startOldQuestionAnimation() {
        val quesOldAnimation = AnimationUtils.loadAnimation(this, R.anim.move_old_question)
        quesOldAnimation.duration = 300
        quesOldAnimation.setAnimationListener (object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                text_question_old.visibility = View.INVISIBLE
            }
        })
        text_question_old.startAnimation(quesOldAnimation)
    }
    private fun startNewQuestionAnimation() {
        val quesNewAnimation = AnimationUtils.loadAnimation(this, R.anim.move_new_question)
        quesNewAnimation.duration = 300
        text_question_new.startAnimation(quesNewAnimation)
    }
    private fun startProgressBarAnimation() {
        text_questions_left.text = "${questionCounter+1} / $questionCountTotal"
        val percent = (((questionCounter+1).toFloat()/questionCountTotal.toFloat())*1000).toInt()
        val oldPercent = (((questionCounter).toFloat()/questionCountTotal.toFloat())*1000).toInt()
        ObjectAnimator.ofInt(progress_bar, "progress", oldPercent, percent).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
        }.start()
    }

    private fun startOldQuestionBackwardAnimation() {
        val quesOldAnimation = AnimationUtils.loadAnimation(this, R.anim.back_move_old_question)
        quesOldAnimation.duration = 250

        quesOldAnimation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                text_question_new.visibility = View.INVISIBLE
            }
        })
        text_question_old.startAnimation(quesOldAnimation)
    }
    private fun startNewQuestionBackwardAnimation() {
        val quesNewAnimation = AnimationUtils.loadAnimation(this, R.anim.back_move_new_question)
        quesNewAnimation.duration = 250
        text_question_new.startAnimation(quesNewAnimation)
    }
    private fun startProgressBarBackwardAnimation() {
        val percent = (((questionCounter+1).toFloat()/questionCountTotal.toFloat())*1000).toInt()
        val oldPercent = (((questionCounter).toFloat()/questionCountTotal.toFloat())*1000).toInt()
        ObjectAnimator.ofInt(progress_bar, "progress", percent, oldPercent).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
        }.start()
    }

    private fun startShowFABAnimation() {
        if (fab_undo.visibility == View.GONE) {
            fab_undo.apply {
                scaleX = 0.2f
                scaleY = 0.2f
                alpha = 0f
                visibility = View.VISIBLE
            }
            fab_undo.animate().scaleY(1f).scaleX(1f).alpha(1f)
        }
    }
    private fun startHideFABAnimation() {
        if (fab_undo.visibility == View.VISIBLE) {
            fab_undo.isEnabled = false
            fab_undo.animate().scaleX(0.2f).scaleY(0.2f).alpha(0f)
                .withEndAction {
                    fab_undo.visibility = View.GONE
                }
        }
    }
    // end
}

