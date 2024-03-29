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

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.enums.ComproPagoStatus
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.request.RequestManager
import com.zerebrez.zerebrez.services.compropago.ComproPagoManager
import com.zerebrez.zerebrez.services.sharedpreferences.JsonParcer
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

open class BaseActivityLifeCycle : AppCompatActivity(), ErrorDialog.OnErrorDialogListener {

    /*
     * Global Tags
     */
    companion object {
        val SET_CHECKED_TAG : String = "set_checked_tag"
        val SHOW_PAYMENT_FRAGMENT : String = "show_payment_fragment"
        val UPDATE_WRONG_QUESTIONS : String = "update_wrong_questions"
        val REQUEST_NEW_QUESTION : String = "request_new_question"
        val QUESTION_POSITION : String = "question_position"
        val REFRESH_FRAGMENT : String = "refresh_fragment"
        val SHOW_QUESTION_RESULT_CODE : Int = 1
        val SHOW_ANSWER_RESULT_CODE : Int = 2
        val SHOW_ANSWER_MESSAGE_RESULT_CODE : Int = 3
        val SHOW_PAYMENT_FRAGMENT_RESULT_CODE : Int = 4
        val RC_CHOOSE_SCHOOL : Int = 5
        val UPDATE_USER_SCHOOLS_RESULT_CODE = 6
        val UPDATE_WRONG_QUESTIONS_RESULT_CODE = 7
        val SHOW_QUESTIONS_RESULT_CODE = 8
    }

    /*
     * Manager
     */
    private lateinit var mRequestManager : RequestManager

    override fun onStart() {
        super.onStart()
        mRequestManager = RequestManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRequestManager = RequestManager(this)
        //setUserNormal()
        checkPendingPayment()
    }

    override fun onStop() {
        super.onStop()
    }

    open fun saveUser(user : User) {
        val json = JsonParcer.parceObjectToJson(user)
        SharedPreferencesManager(baseContext).storeJsonUser(json)
    }

    open fun getUser() : User? {
        val json = SharedPreferencesManager(baseContext).getJsonUser()
        if (json != null) {
            return JsonParcer.getObjectFromJson(json, User::class.java) as User
        } else {
            return null
        }
    }

    open fun setLogInData(hasLogInData : Boolean) {
        SharedPreferencesManager(baseContext).storeLogInData(hasLogInData)
    }

    open fun hasLogInData() : Boolean {
        val isLogedIn = SharedPreferencesManager(baseContext).getLogInData()
        return isLogedIn
    }

    open fun getTermsAndPrivacy() : String {
        val termsAndPrivacy = SharedPreferencesManager(baseContext).getTermsAndPrivacy()
        return termsAndPrivacy
    }


    fun hasPendingPayment() : Boolean{
        val pendingPayment = SharedPreferencesManager(baseContext).getPendingPayment()
        return pendingPayment
    }

    fun getPaymentId() : String {
        val paymentId = SharedPreferencesManager(baseContext).getPaymentId()
        return paymentId
    }

    fun setPendingPayment(hasPendingPayment : Boolean) {
        SharedPreferencesManager(baseContext).storePendingPayment(hasPendingPayment)
    }

    fun setPaymentId(paymentId : String) {
        SharedPreferencesManager(baseContext).storePaymentId(paymentId)
    }

    /*
     * SEND ANSWERED QUESTIONS OLD FORMAT
     */
    /*
    fun requestSendAnsweredQuestions(questions : List<Question>, course: String) {
        mRequestManager.requestSendAnsweredQuestions(questions, course, object : RequestManager.OnSendAnsweredQuestionsListener {
            override fun onSendAnsweredQuestionsLoaded(success: Boolean) {
                onSendAnsweredQuestionsSuccess(success)
            }

            override fun onSendAnsweredQuestionsError(throwable: Throwable) {
                onSendAnsweredQuestionsFail(throwable)
            }
        })
    }*/

    /*
     * SEND ANSWERED QUESTIONS NEW FORMAT
     */
    fun requestSendAnsweredQuestionsNewFormat(questions : List<QuestionNewFormat>, course: String) {
        mRequestManager.requestSendAnsweredQuestionsNewFormat(questions, course,
                object : RequestManager.OnSendAnsweredQuestionsNewFormatListener {
            override fun onSendAnsweredQuestionsNewFormatLoaded(success: Boolean) {
                onSendAnsweredQuestionsNewFormatSuccess(success)
            }

            override fun onSendAnsweredQuestionsNewFormatError(throwable: Throwable) {
                onSendAnsweredQuestionsNewFormatFail(throwable)
            }
        })
    }

    fun requestSendAnsweredQuestionNewFormat(question : QuestionNewFormat, course: String) {
        mRequestManager.requestSendAnsweredQuestionNewFormat(question, course,
                object : RequestManager.OnSendAnsweredQuestionNewFormatListener {
                    override fun onSendAnsweredQuestionNewFormatLoaded(success: Boolean) {
                        onSendAnsweredQuestionNewFormatSuccess(success)
                    }

                    override fun onSendAnsweredQuestionNewFormatError(throwable: Throwable) {
                        onSendAnsweredQuestionNewFormatFail(throwable)
                    }
                })
    }

    fun requestSendAnsweredModules(module : Module, course: String) {
        mRequestManager.requestSendAnsweredModules(module, course, object : RequestManager.OnSendAnsweredModulesListener {
            override fun onSendAnsweredModulesLoaded(success: Boolean) {
                onSendAnsweredModulesSuccess(success)
            }

            override fun onSendAnsweredModulesError(throwable: Throwable) {
                onSendAnsweredModulesFail(throwable)
            }
        })
    }

    fun requestSendAnsweredExams(exam : Exam, course: String) {
        mRequestManager.requestSendAnsweredExams(exam, course, object : RequestManager.OnSendAnsweredExamsListener {
            override fun onSendAnsweredExamsLoaded(success: Boolean) {
                onSendAnsweredExamsSuccess(success)
            }

            override fun onSendAnsweredExamsError(throwable: Throwable) {
                onSendAnsweredExamsFail(throwable)
            }
        })
    }

    fun requestSendSelectedSchools(user: User, schools : List<School>) {
        mRequestManager.requestSendSelectedSchools(user, schools, object : RequestManager.OnSendSelectedSchoolsListener {
            override fun onSendSelectedSchoolsLoaded(success: Boolean) {
                onSendSelectedSchoolsSuccess(success)
            }

            override fun onSendSelectedSchoolsError(throwable: Throwable) {
                onSendSelectedSchoolsFail(throwable)
            }
        })
    }

    fun requestSendUser(user : User) {
        mRequestManager.requestSendUser(user, object : RequestManager.OnSendUserListener {
            override fun onSendUserLoaded(success: Boolean) {
                onSendUserSuccess(success)
            }

            override fun onSendUserError(throwable: Throwable) {
                onSendUserFail(throwable)
            }
        })
    }

    /*fun requestModules() {
        mRequestManager.requestGetModules(object : RequestManager.OnGetModulesListener {
            override fun onGetModulesLoaded(result: List<Module>) {
                onGetModulesSucces(result)
            }

            override fun onGetModulesError(throwable: Throwable) {
                onGetModulesFail(throwable)
            }
        })
    }*/

    fun requestCourses() {
        mRequestManager.requestGetCourses(object : RequestManager.OnGetCoursesListener {
            override fun onGetCoursesLoaded(result: List<String>) {
                onGetCoursesSuccess(result)
            }

            override fun onGetCoursesError(throwable: Throwable) {
                onGetCoursesFail(throwable)
            }
        })
    }

    fun requestGetUserData() {
        mRequestManager.requestGetUserData(object : RequestManager.OnGetUserDataListener {
            override fun onGetUserDataLoaded(user: User) {
                onGetUserDataSuccess(user)
            }

            override fun onGetUserDataError(throwable: Throwable) {
                onGetUserDataFail(throwable)
            }
        })
    }

    fun requestGetInstitutes(course: String) {
        mRequestManager.requestGetInstitutes(course, object : RequestManager.OnGetInstitutesListener {
            override fun onGetInstitutesLoaded(institutes: List<Institute>) {
                onGetInstitutesSuccess(institutes)
            }

            override fun onGetInstitutesError(throwable: Throwable) {
                onGetInstitutesFail(throwable)
            }
        })
    }

    fun requestGetImagesPath(course: String) {
        mRequestManager.requestGetImagesPath(course, object : RequestManager.OnGetImagesPathListener {
            override fun onGetImagesPathLoaded(images: List<Image>) {
                onGetImagesPathSuccess(images)
            }

            override fun onGetImagesPathError(throwable: Throwable) {
                onGetImagesPathFail(throwable)
            }
        })
    }

    fun requestGetProfileRefactor() {
        mRequestManager.requestGetProfileRefactor(object : RequestManager.OnGetProfileRefactorListener {
            override fun onGetProfileRefactorLoaded(user: User) {
                onGetProfileRefactorSuccess(user)
            }

            override fun onGetProfileRefactorError(throwable: Throwable) {
                onGetProfileRefactorFail(throwable)
            }
        })
    }

    fun checkPendingPayment() {
        if (hasPendingPayment() && !getPaymentId().equals("")) {
            Log.e("CheckPendingPayment","Tiene un cargo pendiente")
            val mComproPagoManager = ComproPagoManager()
            mComproPagoManager.verifyCharge(getPaymentId(),
                    object: ComproPagoManager.OnVerifyChargeListener {

                override fun onVerifyChargeResponse(response: Response<ChargeResponse>?) {
                    onVerifyChargeSuccess(response)
                }

                override fun onVerifyChargeFailure(throwable: Throwable?) {

                }
            });
        }
    }

    fun onVerifyChargeSuccess(response: Response<ChargeResponse>?){
        if (response != null) {
            if(response.code() > 199 && response.code() < 300){
                val chargeResponse = response.body()
                if (chargeResponse != null) {
                    if(chargeResponse.paid && chargeResponse.type.equals("charge.success")){

                        val user = getUser()
                        if (user != null) {

                            val userFirebase = FirebaseAuth.getInstance().currentUser
                            if (userFirebase != null && !userFirebase.email.equals("")) {
                                user.setEmail(userFirebase.email!!)
                            }

                            if (!user.getCourse().equals("")) {
                                requestSendUserComproPago(user, chargeResponse.id, ComproPagoStatus.CHARGE_SUCCESS)
                            }
                        }

                        ErrorDialog.newInstance("Felicidades ya eres PREMIUM",
                                DialogType.OK_DIALOG ,this)!!
                                .show(supportFragmentManager, "paywaySuccess")
                        setUserPremium()
                    }
                }
            }
        }
    }

    fun setUserPremium(){
        val user = getUser()
        if(user!=null){
            val userFirebase = FirebaseAuth.getInstance().currentUser
            if (userFirebase != null) {
                user.setEmail(userFirebase.email!!)
            }
            setPendingPayment(false)
            user.setPremiumUser(true)
            user.setTimeStamp(System.currentTimeMillis())
            user.setPayGayMethod("comproPago")
            saveUser(user)
            requestSendUser(user)
        }
    }

    fun setUserNormal(){
        Log.e("SetUserPremium","YA ERES PREMIUM")
        val user = getUser()
        if(user!=null){
            val userFirebase = FirebaseAuth.getInstance().currentUser
            if (userFirebase != null) {
                user.setEmail(userFirebase.email!!)
            }
            user.setPremiumUser(false)
            user.setTimeStamp(System.currentTimeMillis())
            user.setPayGayMethod("")
            saveUser(user)
            requestSendUser(user)
        }
    }

    open fun onSendAnsweredQuestionsSuccess(success : Boolean) {
    }

    open fun onSendAnsweredQuestionsFail(throwable: Throwable) {
    }

    open fun onSendAnsweredQuestionsNewFormatSuccess(success : Boolean) {
    }

    open fun onSendAnsweredQuestionsNewFormatFail(throwable: Throwable) {
    }

    open fun onSendAnsweredQuestionNewFormatSuccess(success : Boolean) {
    }

    open fun onSendAnsweredQuestionNewFormatFail(throwable: Throwable) {
    }

    open fun onSendAnsweredModulesSuccess(success: Boolean) {
    }

    open fun onSendAnsweredModulesFail(throwable: Throwable) {
    }

    open fun onSendAnsweredExamsSuccess(success: Boolean) {
    }

    open fun onSendAnsweredExamsFail(throwable: Throwable) {
    }

    open fun onSendSelectedSchoolsSuccess(success: Boolean) {
    }

    open fun onSendSelectedSchoolsFail(throwable: Throwable) {
    }

    open fun onGetExamScoresSuccess(examScores : List<ExamScore>) {
    }

    open fun onGetExamScoresFail(throwable: Throwable) {
    }

    open fun onSendUserSuccess(success: Boolean) {
    }

    open fun onSendUserFail(throwable: Throwable) {
    }

    open fun onDoLogInSuccess(success : Boolean) {
    }

    open fun onDoLogInFail(throwable: Throwable) {
    }

    open fun onUpdateUserSuccess(success: Boolean) {
    }

    open fun onUpdateUserFail(throwable: Throwable) {
    }

    open fun onSignInUserWithFacebookProviderSuccess(success: Boolean) {
    }

    open fun onSignInUserWithFacebookProviderFail(throwable: Throwable) {
    }

    open fun onSignInUserWithGoogleProviderSuccess(success: Boolean) {
    }

    open fun onSignInUserWithGoogleProviderFail(throwable: Throwable) {
    }

    open fun onLinkAnonymousUserWithFacebookProviderSuccess(success: Boolean) {
    }

    open fun onLinkAnonymousUserWithFacebookProviderFail(throwable: Throwable) {
    }

    open fun onLinkAnonymousUserWithGoogleProviderSuccess(success: Boolean) {
    }

    open fun onLinkAnonymousUserWithGoogleProviderFail(throwable: Throwable) {
    }

    open fun onGetModulesSucces(result : List<Module>) {
    }

    open fun onGetModulesFail(throwable: Throwable) {
    }

    open fun onGetCoursesSuccess(courses : List<String>) {
    }

    open fun onGetCoursesFail(throwable: Throwable) {
    }

    open fun onGetUserDataSuccess(user : User) {
    }

    open fun onGetUserDataFail(throwable: Throwable) {
    }

    open fun onGetInstitutesSuccess(institutes : List<Institute>) {
    }

    open fun onGetInstitutesFail(throwable: Throwable) {
    }

    open fun onGetExamsSuccess(exams : List<Exam>) {
    }

    open fun onGetExamsFail(throwable: Throwable) {
    }

    open fun onGetImagesPathSuccess(images : List<Image>) {
    }

    open fun onGetImagesPathFail(throwable: Throwable) {
    }
    open fun onGetProfileRefactorSuccess(user: User) {}
    open fun onGetProfileRefactorFail(throwable: Throwable) {}


    /*
     * REQUEST QUESTIONS OLD FORMAT
     */

    /*
    fun requestGetQuestionsByModuleIdRefactor(moduleId : Int) {
        mRequestManager.requestGetQuestionsByModuleIdRefactor(moduleId, object : RequestManager.OnGetQuestionsByModuleIdRefactorListener {
            override fun onGetQuestionsByModuleIdRefactorLoaded(questions: List<Question>) {
                onGetQuestionsByModuleIdRefactorSuccess(questions)
            }

            override fun onGetQuestionsByModuleIdRefactorError(throwable: Throwable) {
                onGetQuestionsByModuleIdRefactorFail(throwable)
            }
        })
    }

    fun requestGetQuestionsByExamIdRefactor(examId : Int) {
        mRequestManager.requestGetQuestionsByExamIdRefactor(examId, object : RequestManager.OnGetQuestionsByExamIdRefactorListener {
            override fun onGetQuestionsByExamIdRefactorLoaded(questions: List<Question>) {
                onGetQuestionsByExamIdRefactorSuccess(questions)
            }

            override fun onGetQuestionsByExamIdRefactorError(throwable: Throwable) {
                onGetQuestionsByExamIdRefactorFail(throwable)
            }
        })
    }

    fun requestGetWrongQuestionsByQuestionIdRefactor(wrongQuestions : List<Question>) {
        mRequestManager.requestGetWrongQuestionsByQuestionIdRefactor(wrongQuestions, object : RequestManager.OnGetWrongQuestionsByQuestionIdRefactorListener {
            override fun onGetWrongQuestionsByQuestionIdRefactorLoaded(questions: List<Question>) {
                onGetWrongQuestionsByQuestionIdRefactorSuccess(questions)
            }

            override fun onGetWrongQuestionsByQuestionIdRefactorError(throwable: Throwable) {
                onGetWrongQuestionsByQuestionIdRefactorFail(throwable)
            }
        })
    }

    open fun onGetQuestionsByModuleIdRefactorSuccess(questions : List<Question>) {}
    open fun onGetQuestionsByModuleIdRefactorFail(throwable: Throwable) {}
    open fun onGetQuestionsByExamIdRefactorSuccess(questions : List<Question>) {}
    open fun onGetQuestionsByExamIdRefactorFail(throwable: Throwable) {}
    open fun onGetWrongQuestionsByQuestionIdRefactorSuccess(questions : List<Question>) {}
    open fun onGetWrongQuestionsByQuestionIdRefactorFail(throwable: Throwable) {}*/

    /*
     * REQUEST QUESTIONS NEW FORMAT
     */

    fun requestGetQuestionsNewFormatByModuleIdRefactor(moduleId : Int, course: String) {
        mRequestManager.requestGetQuestionsNewFormatByModuleIdRefactor(moduleId, course, object : RequestManager.OnGetQuestionsNewFormatByModuleIdRefactorListener {
            override fun onGetQuestionsNewFormatByModuleIdRefactorLoaded(questions: List<QuestionNewFormat>) {
                onGetQuestionsNewFormatByModuleIdRefactorSuccess(questions)
            }

            override fun onGetQuestionsNewFormatByModuleIdRefactorError(throwable: Throwable) {
                onGetQuestionsNewFormatByModuleIdRefactorFail(throwable)
            }
        })
    }

    fun requestGetQuestionsNewFormatByExamIdRefactor(examId : Int, course: String) {
        mRequestManager.requestGetQuestionsNewFormatByExamIdRefactor(examId, course, object : RequestManager.OnGetQuestionsNewFormatByExamIdRefactorListener {
            override fun onGetQuestionsNewFormatByExamIdRefactorLoaded(questions: List<QuestionNewFormat>) {
                onGetQuestionsNewFormatByExamIdRefactorSuccess(questions)
            }

            override fun onGetQuestionsNewFormatByExamIdRefactorError(throwable: Throwable) {
                onGetQuestionsNewFormatByExamIdRefactorFail(throwable)
            }
        })
    }

    fun requestGetWrongQuestionsNewFormatByQuestionIdRefactor(wrongQuestionsNewFormat : List<QuestionNewFormat>, course: String) {
        mRequestManager.requestGetWrongQuestionsNewFormatByQuestionIdRefactor(wrongQuestionsNewFormat, course, object : RequestManager.OnGetWrongQuestionsNewFormatByQuestionIdRefactorListener {
            override fun onGetWrongQuestionsNewFormatByQuestionIdRefactorLoaded(questions: List<QuestionNewFormat>) {
                onGetWrongQuestionsNewFormatByQuestionIdRefactorSuccess(questions)
            }

            override fun onGetWrongQuestionsNewFormatByQuestionIdRefactorError(throwable: Throwable) {
                onGetWrongQuestionsNewFormatByQuestionIdRefactorFail(throwable)
            }
        })
    }


    open fun onGetQuestionsNewFormatByModuleIdRefactorSuccess(questions : List<QuestionNewFormat>) {}
    open fun onGetQuestionsNewFormatByModuleIdRefactorFail(throwable: Throwable) {}
    open fun onGetQuestionsNewFormatByExamIdRefactorSuccess(questions : List<QuestionNewFormat>) {}
    open fun onGetQuestionsNewFormatByExamIdRefactorFail(throwable: Throwable) {}
    open fun onGetWrongQuestionsNewFormatByQuestionIdRefactorSuccess(questions : List<QuestionNewFormat>) {}
    open fun onGetWrongQuestionsNewFormatByQuestionIdRefactorFail(throwable: Throwable) {}


    fun requestGetSchools(course: String) {
        mRequestManager.requestGetSchools(course, object : RequestManager.OnGetSchoolsListener {
            override fun onGetSchoolsLoaded(institutes: List<Institute>) {
                onGetSchoolsSuccess(institutes)
            }

            override fun onGetSchoolsError(throwable: Throwable) {
                onGetSchoolsFail(throwable)
            }
        })
    }



    open fun onGetSchoolsSuccess(institutes: List<Institute>) {}
    open fun onGetSchoolsFail(throwable: Throwable) {}


    fun requestGetQuestionsNewFormatBySubjectQuestionId(subjectQuestionsNewFormat: List<QuestionNewFormat>, course: String) {
        mRequestManager.requestGetQuestionsNewFormatBySubjectQuestionId(subjectQuestionsNewFormat, course, object : RequestManager.OnGetSubjectQuestionsNewFormatBySubjectQuestionIdListener {
            override fun OnGetSubjectQuestionsNewFormatBySubjectQuestionIdLoaded(questions: List<QuestionNewFormat>) {
                onGetSubjectQuestionsNewFormatBySubjectQuestionIdSuccess(questions)
            }

            override fun OnGetSubjectQuestionsNewFormatBySubjectQuestionIdError(throwable: Throwable) {
                onGetSubjectQuestionsNewFormatBySubjectQuestionIdFail(throwable)
            }

        })
    }

    open fun onGetSubjectQuestionsNewFormatBySubjectQuestionIdSuccess(questions: List<QuestionNewFormat>) {}
    open fun onGetSubjectQuestionsNewFormatBySubjectQuestionIdFail(throwable: Throwable) {}


    fun requestGetQuestionsIdByExamId(examId: Int, course: String) {
        mRequestManager.requestGetQuestionsIdByExamId(examId, course, object: RequestManager.OnGetQuestionsIdListener {
            override fun onGetQuestionsIdLoaded(questionsId: List<String>) {
                onGetQuestionsIdSuccess(questionsId)
            }
            override fun onGetQuestionsIdError(throwable: Throwable) {
                onGetQuestionsIdFail(throwable)
            }
        })
    }

    fun requestGetQuestionsIdByModuleId(moduleId: Int, course: String) {
        mRequestManager.requestGetQuestionsIdByModuleId(moduleId, course, object: RequestManager.OnGetQuestionsIdListener {
            override fun onGetQuestionsIdLoaded(questionsId: List<String>) {
                onGetQuestionsIdSuccess(questionsId)
            }
            override fun onGetQuestionsIdError(throwable: Throwable) {
                onGetQuestionsIdFail(throwable)
            }
        })
    }

    open fun onGetQuestionsIdSuccess(questionsId: List<String>) {}
    open fun onGetQuestionsIdFail(throwable: Throwable) {}


    fun requestGetQuestionNewFormat(questionId: String, course: String) {
        mRequestManager.requestGetQuestionNewFormat(questionId, course, object : RequestManager.OnGetQuestionNewFormatListener {
            override fun onGetQuestionNewFormatLoaded(question: QuestionNewFormat) {
                onGetQuestionNewFormatSuccess(question)
            }

            override fun onGetQuestionNewFormatError(throwable: Throwable) {
                ongetQuestionNewFormatFail(throwable)
            }
        })
    }

    open fun onGetQuestionNewFormatSuccess(question: QuestionNewFormat) {}
    open fun ongetQuestionNewFormatFail(throwable: Throwable) {}

    fun requestGetWrongQuestionsAndProfileRefactor(course: String) {
        mRequestManager.requestGetWrongQuestionsAndProfileRefactor(course,object : RequestManager.OnGetWrongQuestionAndProfileListener {
            override fun onGetWrongQuestionsAndProfileLoaded(user: User) {
                onGetWrongQuestionsAndProfileRefactorSuccess(user)
            }

            override fun onGetWrongQuestionsAndProfileError(throwable: Throwable) {
                onGetWrongQuestionsAndProfileRefactorFail(throwable)
            }
        })
    }

    open fun onGetWrongQuestionsAndProfileRefactorSuccess(user : User) {}
    open fun onGetWrongQuestionsAndProfileRefactorFail(throwable: Throwable) {}

    fun requestGetMinimumVersion() {
        mRequestManager.requestGetMinimumVersion(object : RequestManager.OnGetMinimumVersionListener {
            override fun onGetMinimumVersionLoaded(minimumVersion: String) {
                onGetMinimumVersionSuccess(minimumVersion)
            }

            override fun onGetMinimumVersionError(throwable: Throwable) {
                onGetMinimumVersionFail(throwable)
            }
        })
    }

    open fun onGetMinimumVersionSuccess(minimumVersion: String) {}
    open fun onGetMinimumVersionFail(throwable: Throwable) {}

    override fun onConfirmationCancel() {}

    override fun onConfirmationNeutral() {}

    override fun onConfirmationAccept() {}


    fun requestSendUserComproPago(user : User, billingId: String, comproPagoStatus: ComproPagoStatus) {
        mRequestManager.requestSendUserComproPago(user, billingId, comproPagoStatus, object : RequestManager.OnSendUserComproPagoListener {
            override fun onSendUserComproPagoLoaded(success: Boolean) {
                onSendUserComproPagoSuccess(success)
            }

            override fun onSendUserComproPagoError(throwable: Throwable) {
                onSendUserComproPagoFail(throwable)
            }
        })
    }

    open fun onSendUserComproPagoSuccess(success: Boolean) {}
    open fun onSendUserComproPagoFail(throwable: Throwable) {}

    fun requestGetCoursesRefactor() {
        mRequestManager.requestGetCoursesrefactor(object : RequestManager.OnGetCourseRefactorListener {
            override fun onGetCoursesRefactorLoaded(courses: List<Course>) {
                onGetCoursesRefactorSuccess(courses)
            }

            override fun onGetCoursesRefactorError(throwable: Throwable) {
                onGetCoursesRefactorFail(throwable)
            }
        })
    }

    open fun onGetCoursesRefactorSuccess(courses: List<Course>) {}
    open fun onGetCoursesRefactorFail(throwable: Throwable) {}
}