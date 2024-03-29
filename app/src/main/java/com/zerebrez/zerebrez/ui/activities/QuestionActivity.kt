/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.question.*
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil


/**
 * This class manage the UI questions showing
 *
 * Created by Jorge Zepeda Tinoco on 29/04/18.
 * jorzet.94@gmail.com
 */

class QuestionActivity : BaseActivityLifeCycle(), ErrorDialog.OnErrorDialogListener,
        RewardedVideoAdListener {

    /*
     * Tags
     */
    private val TAG : String = "QuestionActivity"
    private var CURRENT_COURSE : String = "current_course"
    private val MODULE_ID : String = "module_id"
    private val QUESTION_ID : String = "question_id"
    private val CURRENT_QUESTION_ID : String = "current_question_id"
    private val ANSWERED_QUESTIONS : String = "answered_questions"
    private val EXAM_ID : String = "exam_id"
    private val SELECTED_SUBJECT : String = "selected_subject"
    private val ANONYMOUS_USER : String = "anonymous_user"
    private val FROM_SUBJECT_QUESTION : String = "from_subject_question"
    private val FROM_WRONG_QUESTION : String = "from_wrong_question"
    private val FROM_EXAM_FRAGMENT : String = "from_exam_fragment"
    private val SHOW_START : String = "show_start"
    private val HITS_EXTRA = "hits_extra"
    private val MISSES_EXTRA = "misses_extra"
    private val WRONG_QUESTIONS_LIST = "wrong_questions_list"
    private val SUBJECT_QUESTIONS_LIST : String = "subject_questions_list"
    private val QUESTION_IDS_LIST : String = "questions_ids_list"
    private val SUBJECT_EXTRA : String = "subject_extra"

    /*
     * UI accessors
     */
    private lateinit var mModuleNumber : TextView
    private lateinit var mQuestiontypeText : TextView
    private lateinit var mCloseQuestion : View
    private lateinit var mNextQuestion : View
    private lateinit var mNextQuestionText : TextView
    private lateinit var mShowExpandedQuestion : View
    private lateinit var mShowAnswer : View
    private lateinit var mShowAnswerText : TextView
    private lateinit var mCompleteQuestionsFragmentContainer : FrameLayout
    private lateinit var mControlsBar : View
    private lateinit var progressBarHolder : FrameLayout
    private lateinit var mQuestionsProgress : ProgressBar
    private lateinit var mShowQuestionsButton : View
    private lateinit var mShowQuestionsText: TextView

    /*
     * Variables
     */
    private var mSubject : String = ""
    private var mCourse : String = ""
    private var mModuleId : Int = 0
    private var mQuestionId : Int = 0
    private var mExamId : Int = 0
    private var mCurrentQuestion : Int = 0
    private var mCurrentQuestionSkiped : Int = 0
    private var mLastKnownQuestion : Int = 0
    private var mCorrectQuestions : Int = 0
    private var mIncorrectQiestions : Int = 0
    private var isAnonymous : Boolean = false
    private var isFromSubjectQuestionFragment : Boolean = false
    private var isFromWrongQuestionFragment : Boolean = false
    private var isFromExamFragment : Boolean = false
    private var resetExpandedButton : Boolean = false
    private var mExamAnsQuestionsSaved = false
    private var mModulesAndQuestionsSaved = false
    private var mWrongQuestionsSaved = false
    private var mSubjectQuestionsSaved = false
    private var mShowPaymentFragment = false
    private var mUserSkipQuestions = false
    private var mUserFinishQuestons = false
    private var mProgressByQuestion : Float = 0.0F
    private var mCurrentProgress : Float = 0.0F

    /*
     * Animation
     */
    private lateinit var inAnimation : AlphaAnimation
    private lateinit var outAnimation : AlphaAnimation

    /*
     * Objects
     */
    private var mAnsweredQuestions = arrayListOf<QuestionNewFormat>()
    private var mQuestionsAux = arrayListOf<QuestionNewFormat>()
    private lateinit var mQuestionsId: List<String>
    private lateinit var mCurrentQuestionNewFormat: QuestionNewFormat
    private lateinit var mModuleList : List<Module>
    private lateinit var mExamList : List<Exam>
    private lateinit var currentFragment : Fragment

    /*
     * Ads Objects
     */
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_container)

        mModuleNumber = findViewById(R.id.tv_module_number)
        mQuestiontypeText = findViewById(R.id.tv_question_type_text)
        mCloseQuestion = findViewById(R.id.iv_close_question)
        mShowAnswer = findViewById(R.id.btn_show_answer)
        mShowAnswerText = findViewById(R.id.btn_show_answer_text)
        mShowExpandedQuestion = findViewById(R.id.iv_show_expanded_question)
        mNextQuestion = findViewById(R.id.btn_next_question)
        mNextQuestionText = findViewById(R.id.btn_next_question_text)
        mCompleteQuestionsFragmentContainer = findViewById(R.id.complete_question_fragment_container)
        mControlsBar = findViewById(R.id.bottom_bar)
        progressBarHolder = findViewById(R.id.progressBarHolder)
        mQuestionsProgress = findViewById(R.id.pb_questions_progress)
        mShowQuestionsButton = findViewById(R.id.rl_show_questions)
        mShowQuestionsText = findViewById(R.id.tv_show_questions)

        //mBackQuestion.setOnClickListener(mBackQuestionListener)
        mCloseQuestion.setOnClickListener(mCloseQuestionListener)
        mNextQuestion.setOnClickListener(mNextQuestionListener)
        mShowExpandedQuestion.setOnClickListener(mShowExpandedQuestionListener)
        mShowAnswer.setOnClickListener(mShowAnswerListener)
        mShowQuestionsButton.setOnClickListener(mShowQuestionsListener)

        mShowAnswer.isEnabled = false
        setNextQuestionEnable(false)
        mShowExpandedQuestion.visibility = View.GONE

        mModuleNumber.typeface = FontUtil.getNunitoBold(baseContext)
        mNextQuestionText.typeface = FontUtil.getNunitoBlack(baseContext)
        mShowAnswerText.typeface = FontUtil.getNunitoBlack(baseContext)
        mShowQuestionsText.typeface = FontUtil.getNunitoBlack(baseContext)

        inAnimation = AlphaAnimation(0f, 1f)
        inAnimation.duration = 200
        outAnimation = AlphaAnimation(1f, 0f)
        outAnimation.duration = 200

        if (intent != null) {
            try {

                mCourse = intent.getStringExtra(CURRENT_COURSE)
                mModuleId = intent.getIntExtra(MODULE_ID, -1)
                mQuestionId = intent.getIntExtra(QUESTION_ID, -1)
                mExamId = intent.getIntExtra(EXAM_ID, -1)
                isAnonymous = intent.getBooleanExtra(ANONYMOUS_USER, false)
                isFromSubjectQuestionFragment = intent.getBooleanExtra(FROM_SUBJECT_QUESTION, false)
                isFromWrongQuestionFragment = intent.getBooleanExtra(FROM_WRONG_QUESTION, false)
                isFromExamFragment = intent.getBooleanExtra(FROM_EXAM_FRAGMENT, false)

                val user = getUser()

                if (isFromSubjectQuestionFragment) {
                    showLoading(true)
                    //val mSelectedSubject = intent.getStringExtra(SELECTED_SUBJECT)
                    //requestGetQuestionsNewFormatBySubject(mSelectedSubject)

                    mQuestionsId = intent.getSerializableExtra(SUBJECT_QUESTIONS_LIST) as List<String>
                    mSubject = intent.getStringExtra(SUBJECT_EXTRA)

                    if (mQuestionId != -1) {
                        val questionsIds = arrayListOf<String>()
                        for (i in 0..mQuestionsId.size - 1) {
                            if (mQuestionsId[i].equals("p" + mQuestionId)) {
                                for (j in i..mQuestionsId.size - 1) {
                                    questionsIds.add(mQuestionsId[j])
                                    val questionAux = QuestionNewFormat()
                                    questionAux.questionId = mQuestionsId[j]
                                    mQuestionsAux.add(questionAux)
                                }
                                break;
                            }
                        }
                        mQuestionsId = questionsIds
                    }

                    mModuleNumber.text = ":)"
                    mQuestiontypeText.text = mSubject

                    mProgressByQuestion = 100 / mQuestionsId.size.toFloat()

                    if (user != null && !user.getCourse().equals("")) {
                        requestGetQuestionNewFormat(mQuestionsId[mCurrentQuestion], user.getCourse())
                        //requestGetQuestionsNewFormatBySubjectQuestionId(mQuestions, user.getCourse())
                    }
                } else if (isFromWrongQuestionFragment) {
                    showLoading(true)
                    mQuestionsId = intent.getSerializableExtra(WRONG_QUESTIONS_LIST) as List<String>

                    if (mQuestionId != -1) {
                        val questionsIds = arrayListOf<String>()
                        for (i in 0..mQuestionsId.size - 1) {
                            if (mQuestionsId[i].equals("p" + mQuestionId)) {
                                for (j in i..mQuestionsId.size - 1) {
                                    questionsIds.add(mQuestionsId[j])
                                    val questionAux = QuestionNewFormat()
                                    questionAux.questionId = mQuestionsId[j]
                                    mQuestionsAux.add(questionAux)
                                }
                                break;
                            }
                        }
                        mQuestionsId = questionsIds
                    }

                    mModuleNumber.text = ":)"
                    mProgressByQuestion = 100 / mQuestionsId.size.toFloat()

                    if (user != null && !user.getCourse().equals("")) {
                        requestGetQuestionNewFormat(mQuestionsId[mCurrentQuestion], user.getCourse())
                        //requestGetWrongQuestionsNewFormatByQuestionIdRefactor(mQuestions, user.getCourse())
                    }

                } else if (isFromExamFragment) {
                    showLoading(true)

                    mModuleNumber.text = mExamId.toString()
                    mQuestiontypeText.text = "Examen"

                    if (user != null && !user.getCourse().equals("")) {
                        requestGetQuestionsIdByExamId(mExamId, user.getCourse())
                        //requestGetQuestionsNewFormatByExamIdRefactor(mExamId, user.getCourse())
                    }
                } else {
                    showLoading(true)

                    mModuleNumber.text = mModuleId.toString()
                    mQuestiontypeText.text = "Módulo"

                    if (user != null && !user.getCourse().equals("")) {
                        requestGetQuestionsIdByModuleId(mModuleId, user.getCourse())
                        //requestGetQuestionsNewFormatByModuleIdRefactor(mModuleId, user.getCourse())
                    }
                }

                // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
                //MobileAds.initialize(this, "ca-app-pub-3940256099942544/1033173712") //test
                MobileAds.initialize(this, "ca-app-pub-4979517608172495/5959613048") //prod
                // Use an activity context to get the rewarded video instance.
                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
                mRewardedVideoAd.rewardedVideoAdListener = this
                // RequestAdd
                loadRewardedVideoAd()

                mInterstitialAd = InterstitialAd(this)
                //mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712" //test
                mInterstitialAd.adUnitId = "ca-app-pub-4979517608172495/5959613048" //prod
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                mInterstitialAd.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                    }

                    override fun onAdFailedToLoad(errorCode: Int) {
                        // Code to be executed when an ad request fails.
                    }

                    override fun onAdOpened() {
                        // Code to be executed when the ad is displayed.
                    }

                    override fun onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    override fun onAdClosed() {
                        // Code to be executed when when the interstitial ad is closed.
                    }
                }
            } catch (e: java.lang.Exception) {
            } catch (e: kotlin.Exception) {}
        }
    }

    private fun loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-4979517608172495/5959613048", // prod
                //"ca-app-pub-3940256099942544/5224354917",// test
                AdRequest.Builder().build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode.equals(SHOW_ANSWER_RESULT_CODE)) {
            val showAnswer = data!!.getBooleanExtra(SET_CHECKED_TAG, false)
            if (showAnswer) {
                if (::currentFragment.isInitialized && currentFragment is QuestionFragmentRefactor) {
                    (currentFragment as QuestionFragmentRefactor).showAnswerQuestion()
                }
            }
        } else if (resultCode.equals(SHOW_ANSWER_MESSAGE_RESULT_CODE)) {
            //DataHelper(baseContext).saveCurrentQuestion(mQuestions.get(mCurrentQuestion))
            if (::mCurrentQuestionNewFormat.isInitialized) {
                DataHelper(baseContext).saveCurrentQuestionNewFormat(mCurrentQuestionNewFormat)
            }
            showAnswer()
        } else if (resultCode.equals(SHOW_QUESTIONS_RESULT_CODE)) {
            val questionId = data!!.getStringExtra(REQUEST_NEW_QUESTION)
            val questionPosition = data.getIntExtra(QUESTION_POSITION, -1)

            if (questionPosition != -1) {
                if (questionPosition != mCurrentQuestion) {
                    mUserSkipQuestions = true
                }
                //mCurrentQuestion = questionPosition
                mCurrentQuestionSkiped = questionPosition
            }

            val user = getUser()
            if (user != null && !user.getCourse().equals("")) {
                requestGetQuestionNewFormat(questionId, user.getCourse())
            }
        }
    }

    override fun onBackPressed() {
        try {
            if (isAnonymous) {
                goLogInActivityStartFragment()
            } else if (mShowPaymentFragment) {
                val intent = Intent()
                intent.putExtra(SHOW_PAYMENT_FRAGMENT, true)
                setResult(SHOW_ANSWER_MESSAGE_RESULT_CODE, intent)
                finish()
            } else if (isFromWrongQuestionFragment) {
                val intent = Intent()
                intent.putExtra(UPDATE_WRONG_QUESTIONS, true)
                setResult(UPDATE_WRONG_QUESTIONS_RESULT_CODE, intent)
                finish()
            }
            super.onBackPressed()
        } catch (exception: Exception) {}
    }

    private val mCloseQuestionListener = View.OnClickListener {
        if (mLastKnownQuestion > 0) {
            if (isFromWrongQuestionFragment || isFromSubjectQuestionFragment) {
                if (isAnonymous) {
                    goLogInActivityStartFragment()
                } else {
                    requestSendAnsweredQuestionsNewFormat(mAnsweredQuestions, mCourse)
                    onBackPressed()
                }
            } else {
                ErrorDialog.newInstance("¿Seguro que quieres salir?",
                        "Perderás los avances.",
                        DialogType.YES_NOT_DIALOG,
                        this)!!
                        .show(supportFragmentManager, "")
            }
        } else {
            if (isAnonymous) {
                goLogInActivityStartFragment()
                //onBackPressed()
            } else {
                onBackPressed()
            }
        }
    }

    /*
     * A listener that check current question and show back question
     */
    /*private val mBackQuestionListener = View.OnClickListener {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
            showQuestion()
            mCurrentQuestion--
        }
    }*/

    /*
     * A listener that check current question and show next question
     *  - If user is anonymous is neesary show LoginActivity
     *  - Else (this means that user answer all questions) save questions in current module and show
     *    QuestionModuleFragment again with current module updated
     */
    private val mNextQuestionListener = View.OnClickListener {
        setNextQuestionEnable(false)
        enableDisableShowQuestionsButton(true)

        mLastKnownQuestion ++
        //if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size -1) {

        var questionsToAnswer = 0

        for (questionAux in mQuestionsAux) {
            if (!questionAux.answered) {
                questionsToAnswer++
            }
        }

        if (questionsToAnswer == 1) {
            mUserFinishQuestons = true
        }

        Log.d("mNextQuestionListener"," questionsToAnswer: " + questionsToAnswer)
        if (!mUserFinishQuestons) {
            Log.d("mNextQuestionListener"," mUserFinishQuestons: " + mUserFinishQuestons)

            Log.d("mNextQuestionListener"," mCurrentQuestionSkiped: " + mCurrentQuestionSkiped + " --- mCurrentQuestion: " + mCurrentQuestion)

            Log.d("mNextQuestionListener","next")
            val user = getUser()
            if (user != null && !user.getCourse().equals("")) {

                if (mCurrentQuestion != mCurrentQuestionSkiped) {
                    mCurrentQuestion = mCurrentQuestionSkiped
                }

                mQuestionsAux[mCurrentQuestion].answered = true

                if (::mCurrentQuestionNewFormat.isInitialized) {
                    mAnsweredQuestions.add(mCurrentQuestionNewFormat)
                }


                if (mCurrentQuestionSkiped == mQuestionsAux.size - 1) {
                    mCurrentQuestionSkiped = 0
                } else {
                    mCurrentQuestionSkiped++
                }

                var hasNextQuestion = false
                if (mQuestionsAux[mQuestionsAux.size - 1].answered) {
                    for (i in mCurrentQuestionSkiped..mQuestionsAux.size - 1) {
                        if (mQuestionsAux[i].answered) {
                            hasNextQuestion = true
                        }
                    }
                }

                mCurrentQuestion = mCurrentQuestionSkiped

                Log.d("mNextQuestionListener"," next--mCurrentQuestionSkiped: " + mCurrentQuestionSkiped)


                if (mQuestionsAux[mCurrentQuestionSkiped].answered) {
                    if (hasNextQuestion) {
                        mCurrentQuestionSkiped = 0
                    }
                    for (i in mCurrentQuestionSkiped..mQuestionsAux.size - 1) {
                        mCurrentQuestionSkiped = i
                        mCurrentQuestion = mCurrentQuestionSkiped
                        if (!mQuestionsAux[i].answered) {
                            requestGetQuestionNewFormat(mQuestionsAux[i].questionId, user.getCourse())
                            Log.d("mNextQuestionListener", " next--mCurrentQuestionSkiped: " + mCurrentQuestionSkiped)
                            break
                        }
                    }
                } else {
                    requestGetQuestionNewFormat(mQuestionsId[mCurrentQuestion], user.getCourse())
                }

            }
        } else {


            mQuestionsAux[mCurrentQuestion].answered = true
            if (::mCurrentQuestionNewFormat.isInitialized) {
                mAnsweredQuestions.add(mCurrentQuestionNewFormat)
            }

            Log.d("mNextQuestionListener"," else-- mAnsweredQuestions.size: " + mAnsweredQuestions.size)
            Log.d("mNextQuestionListener"," else-- mQuestionsId.size: " + mQuestionsId.size)

            if (isAnonymous) {
                saveModulesAndQuestions()
            } else {
                if (isFromWrongQuestionFragment) {
                    saveWrongQuestion()
                } else if (isFromSubjectQuestionFragment) {
                    saveQuestionSubject()
                } else if (isFromExamFragment) {
                    saveExamsAndQuestions()
                } else {
                    saveModulesAndQuestions()
                }
            }
        }
    }

    /*
     * A listener that expands the question to read it beater
     */
    private val mShowExpandedQuestionListener = View.OnClickListener {
        if (resetExpandedButton) {
            mShowExpandedQuestion.background = resources.getDrawable(R.drawable.finger_unselected_icon)
            resetExpandedButton = false
        } else {
            mShowExpandedQuestion.background = resources.getDrawable(R.drawable.finger_selected_icon)
            resetExpandedButton = true
        }

        if (currentFragment is QuestionFragmentRefactor) {
            (currentFragment as QuestionFragmentRefactor).showExpandedQuestion(resetExpandedButton)
        }
    }

    /*
     * A listener that show answer step by step according to current question
     */
    private val mShowAnswerListener = View.OnClickListener {
        //showAnswerMessage()

        if (SharedPreferencesManager(baseContext).isShowAnswerMessageOK()) {
            /*if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
                if (mQuestions.get(mCurrentQuestion).hasStepByStep()) {
                    DataHelper(baseContext).saveCurrentQuestion(mQuestions.get(mCurrentQuestion))
                    showAnswer()
                }
            }*/
            /*if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestionsNewFormat.size) {
                if (mQuestionsNewFormat.get(mCurrentQuestion).stepByStepData.isNotEmpty()) {
                    DataHelper(baseContext).saveCurrentQuestionNewFormat(mQuestionsNewFormat.get(mCurrentQuestion))
                    showAnswer()
                }
            }*/
            if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestionsId.size) {
                if (::mCurrentQuestionNewFormat.isInitialized && mCurrentQuestionNewFormat.stepByStepData.isNotEmpty()) {
                    DataHelper(baseContext).saveCurrentQuestionNewFormat(mCurrentQuestionNewFormat)
                    showAnswer()
                }
            }
        } else {
            showAnswerMessage()
        }

    }

    private val mShowQuestionsListener = View.OnClickListener {
        showQuestions()
    }


    /*
     * This method show the correspond fragment according to question type
     */
    private fun showQuestion() {
        mCurrentProgress += mProgressByQuestion
        mQuestionsProgress.progress = mCurrentProgress.toInt()
        currentFragment = QuestionFragmentRefactor()
        val manager = getSupportFragmentManager();
        val transaction = manager.beginTransaction();
        transaction.replace(R.id.question_fragment_container, currentFragment);
        transaction.commitAllowingStateLoss()
    }

    /**
     * @return
     *      The current question according to moduleId
     */
    fun getQuestionNewFormat() : QuestionNewFormat? {
        if (::mCurrentQuestionNewFormat.isInitialized) {
            return mCurrentQuestionNewFormat
        } else {
            return null
        }
    }

    fun getCorrectQuestions() : Int {
        return this.mCorrectQuestions
    }

    fun getIncorrectQuestion() : Int {
        return this.mIncorrectQiestions
    }

    /**
     * @param answer
     * @param wasOK
     *      The method set answed choosed by user and set if answer was correct or not
     */
    fun setQuestionNewFormatAnswer(answer : String, wasOK : Boolean) {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestionsId.size) {
            if (::mCurrentQuestionNewFormat.isInitialized) {
                mCurrentQuestionNewFormat.chosenOption = answer
                mCurrentQuestionNewFormat.wasOK = wasOK
            }
            if (wasOK) {
                mCorrectQuestions++
            } else {
                mIncorrectQiestions++
            }
        }
    }


    /*
     * This method save the current module and its own questions
     */
    private fun saveModulesAndQuestions() {

        //if (NetworkUtil.isConnected(baseContext)) {
        //    requestSendAnsweredModules(mModuleList)
        //} else {
        val module = Module()
        module.setId(Integer(mModuleId))
        module.setAnsweredModule(true)
        //module.setQuestions(mQuestions)
        module.setQuestionsNewFormat(mAnsweredQuestions)
        module.setCorrectQuestions(mCorrectQuestions)
        module.setIncorrectQuestions(mIncorrectQiestions)

        requestSendAnsweredModules(module, mCourse)
        //requestSendAnsweredQuestions(mQuestions, mCourse)
        requestSendAnsweredQuestionsNewFormat(mAnsweredQuestions, mCourse)
        mModulesAndQuestionsSaved = true

        // this is called on QuestionsCompleteFragment
        if (isAnonymous) {
            goLogInActivity()
        } else {
            showQuestionsCompleteFragment()
        }


    }

    private fun saveExamsAndQuestions() {

        // send data
        //if (NetworkUtil.isConnected(baseContext)) {
        //    requestSendAnsweredExams(mExamList)
        //} else {

        val exam = Exam()
        exam.setExamId(Integer(mExamId))
        //exam.setQuestions(mQuestions)
        exam.setQuestionsNewFormat(mAnsweredQuestions)
        exam.setHits(mCorrectQuestions)
        exam.setMisses(mIncorrectQiestions)
        exam.setAnsweredExam(true)

        requestSendAnsweredExams(exam, mCourse)
        //requestSendAnsweredQuestions(mQuestions, mCourse)
        requestSendAnsweredQuestionsNewFormat(mAnsweredQuestions, mCourse)
        mExamAnsQuestionsSaved = true
        showQuestionsCompleteFragment()
        // this is called on QuestionsCompleteFragment
        //onBackPressed()
        //}

    }

    private fun saveQuestionSubject() {
        requestSendAnsweredQuestionsNewFormat(mAnsweredQuestions, mCourse)
        mSubjectQuestionsSaved = true
        showQuestionsCompleteFragment()
    }

    private fun saveWrongQuestion() {
        //requestSendAnsweredQuestions(mQuestions, mCourse)
        requestSendAnsweredQuestionsNewFormat(mAnsweredQuestions, mCourse)
        mWrongQuestionsSaved = true
        showQuestionsCompleteFragment()
    }

    private fun showAnswerMessage() {
        val intent = Intent(this, ShowAnswerMessageActivity::class.java)
        this.startActivityForResult(intent, SHOW_QUESTION_RESULT_CODE)
    }

    private fun showAnswer() {
        val intent = Intent(this, ShowAnswerActivity::class.java)
        this.startActivityForResult(intent, SHOW_QUESTION_RESULT_CODE)
    }

    private fun showQuestions() {
        val mQuestionIds = arrayListOf<String>()
        mQuestionIds.addAll(mQuestionsId)

        val intent = Intent(this, ShowQuestionsActivity::class.java)
        intent.putExtra(FROM_SUBJECT_QUESTION, isFromSubjectQuestionFragment)
        intent.putExtra(FROM_EXAM_FRAGMENT, isFromExamFragment)
        intent.putExtra(FROM_WRONG_QUESTION, isFromWrongQuestionFragment)
        intent.putExtra(QUESTION_IDS_LIST, mQuestionIds)
        intent.putExtra(CURRENT_QUESTION_ID, mCurrentQuestionSkiped)
        intent.putExtra(ANSWERED_QUESTIONS, mAnsweredQuestions)
        this.startActivityForResult(intent, SHOW_QUESTIONS_RESULT_CODE)
    }

    private fun showQuestionsCompleteFragment() {
        mCompleteQuestionsFragmentContainer.visibility = View.VISIBLE
        showHideControlBar(false)

        currentFragment = QuestionsCompleteFragment()
        val manager = getSupportFragmentManager();
        val transaction = manager.beginTransaction();
        transaction.replace(R.id.complete_question_fragment_container, currentFragment)
        transaction.commit()
    }

    fun setNextQuestionEnable(isEnable : Boolean) {
        mNextQuestion.isEnabled = isEnable
    }

    fun showHideExpandedQuestionButton(showButton : Boolean) {
        if (showButton)
            mShowExpandedQuestion.visibility = View.VISIBLE
        else
            mShowExpandedQuestion.visibility = View.GONE
    }

    fun showHideControlBar(showControls : Boolean) {
        if (showControls) {
            mControlsBar.visibility = View.VISIBLE
        } else {
            mControlsBar.visibility = View.GONE
        }
    }

    /*
     * Those method are called on QuestionsCompletefragment
     */
    fun areModulesAndQuestionsSaved() : Boolean {
        return this.mModulesAndQuestionsSaved
    }

    fun areExamsAndQuestionsSaved() : Boolean {
        return this.mExamAnsQuestionsSaved
    }

    fun areSubjectQuestionsSaved() : Boolean {
        return this.mSubjectQuestionsSaved
    }

    fun getSubject() : String {
        return this.mSubject
    }

    fun areWrongQuestionsSaved() : Boolean {
        return this.mWrongQuestionsSaved
    }

    fun isAnonymousUser() : Boolean {
        return this.isAnonymous
    }

    fun getModuleId() : Int {
        return this.mModuleId
    }

    fun getExamId() : Int {
        return this.mExamId
    }

    fun showPaymentFragment(showPaymentFragment : Boolean) {
        this.mShowPaymentFragment = showPaymentFragment
    }

    /*
     * This method is only used after StartFragment when user responds the first module
     * the app is going to redirect to LoginActivity to show SingUpFragment
     */
    private fun goLogInActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(SHOW_START, false)
        intent.putExtra(HITS_EXTRA, getCorrectQuestions())
        intent.putExtra(MISSES_EXTRA, getIncorrectQuestion())
        this.startActivity(intent)
        this.finish()
    }

    private fun goLogInActivityStartFragment() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(SHOW_START, true)
        this.startActivity(intent)
        this.finish()
    }

    override fun onGetQuestionsIdSuccess(questionsId: List<String>) {
        super.onGetQuestionsIdSuccess(questionsId)
        for (questionId in questionsId) {
            val questionAux = QuestionNewFormat()
            questionAux.questionId = questionId
            mQuestionsAux.add(questionAux)
        }
        mQuestionsId = questionsId
        mProgressByQuestion = 100 / questionsId.size.toFloat()
        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestGetQuestionNewFormat(mQuestionsId[mCurrentQuestion], user.getCourse())
        }
    }

    override fun onGetQuestionsIdFail(throwable: Throwable) {
        super.onGetQuestionsIdFail(throwable)
        showLoading(false)
        onBackPressed()
    }

    override fun onGetQuestionNewFormatSuccess(question: QuestionNewFormat) {
        super.onGetQuestionNewFormatSuccess(question)
        mCurrentQuestionNewFormat = question

        try {
            if (isFromWrongQuestionFragment || isFromSubjectQuestionFragment) {
                mQuestiontypeText.text = question.subject.value
            }

        } catch (e: kotlin.Exception) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        showQuestion()
        showLoading(false)
    }

    override fun ongetQuestionNewFormatFail(throwable: Throwable) {
        super.ongetQuestionNewFormatFail(throwable)
        ErrorDialog.newInstance("Ocurrió un problema, vuelve a intentarlo",
                DialogType.OK_DIALOG, this)!!
                .show(supportFragmentManager!!, "notAbleNow")
    }



    fun showLoading(showLoading : Boolean) {
        if (showLoading) {
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        } else {
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
        }
    }

    fun enableDisableAnswerButton(showButton: Boolean) {
        mShowAnswer.isEnabled = showButton
    }

    fun enableDisableShowQuestionsButton(showButton: Boolean) {
        mShowQuestionsButton.isEnabled = showButton
    }

    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {
        if (isAnonymous) {
            goLogInActivityStartFragment()
        } else {
            onBackPressed()
        }
    }


    /********************************************************************************/

    fun getInterstitialAd() : InterstitialAd? {
        if (::mInterstitialAd.isInitialized) {
            return mInterstitialAd
        } else {
            return null
        }
    }

    fun getRewardedVideoAd() : RewardedVideoAd? {
        if (::mRewardedVideoAd.isInitialized) {
            return mRewardedVideoAd
        } else {
            return null
        }
    }

    override fun onRewarded(reward: RewardItem) {
        //Toast.makeText(this, "onRewarded! currency: ${reward.type} amount: ${reward.amount}", Toast.LENGTH_SHORT).show()
        // Reward the user.
    }

    override fun onRewardedVideoAdLeftApplication() {
        //Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdClosed() {
        //Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        //Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLoaded() {
        //Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
        //Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoStarted() {
        //Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
        //Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }

}
