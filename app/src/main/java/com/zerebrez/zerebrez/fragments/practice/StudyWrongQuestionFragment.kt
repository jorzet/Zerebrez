/*
 * Copyright [2018] [Jorge Zepeda Tinoco]
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

package com.zerebrez.zerebrez.fragments.practice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.QuestionActivity

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

class StudyWrongQuestionFragment : BaseContentFragment() {

    /*
     * Tags
     */
    private val TAG : String = "StudyWrongQuestionF"
    private val MODULE_ID : String = "module_id"
    private val QUESTION_ID : String = "question_id"
    private val ANONYMOUS_USER : String = "anonymous_user"
    private val FROM_WRONG_QUESTION : String = "from_wrong_question"

    /*
     * UI accessors
     */
    private lateinit var mLeftTableLayout : GridLayout
    private lateinit var mCenterTableLayout : GridLayout
    private lateinit var mRightTableLayout : GridLayout
    private lateinit var mNotWrongQuestionsCurrently : TextView
    private lateinit var mMainContainer : View

    /*
     * Data accessor
     */
    private lateinit var mDataHelper : DataHelper

    /*
     * Variables
     */
    private var mTotalQuestions : Int = 0

    /*
     * Objects
     */
    private var mQuestionList = arrayListOf<Question>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.study_wrong_question_fragment, container, false)!!

        mLeftTableLayout = rootView.findViewById(R.id.table_left)
        mCenterTableLayout = rootView.findViewById(R.id.table_center)
        mRightTableLayout = rootView.findViewById(R.id.table_right)
        mNotWrongQuestionsCurrently = rootView.findViewById(R.id.tv_not_wrong_questions_currently)
        mMainContainer = rootView.findViewById(R.id.sv_main_container)

        mDataHelper = DataHelper(context!!)
        val wrongQuestions = mDataHelper.getWrongQuestions()
        if (wrongQuestions.isEmpty()) {
            mMainContainer.visibility = View.GONE
            mNotWrongQuestionsCurrently.visibility = View.VISIBLE
        } else {
            updateQuestionList(wrongQuestions)
            drawQuestions()
        }

        return rootView
    }

    private fun updateQuestionList(questions : List<Question>) {
        var row = 1
        for (i in 0 .. questions.size - 1) {
            when (row) {
                1 -> {
                    mQuestionList.add(questions.get(i))
                    val nothing = Question()
                    nothing.setQuestionId(Integer(-1))
                    mQuestionList.add(nothing)
                    val padding = Question()
                    padding.setQuestionId(Integer(-2))
                    mQuestionList.add(padding)
                }
                2 -> {
                    val nothing = Question()
                    nothing.setQuestionId(Integer(-1))
                    mQuestionList.add(nothing)
                    mQuestionList.add(questions.get(i))
                    val padding = Question()
                    padding.setQuestionId(Integer(-1))
                    mQuestionList.add(padding)
                }
                3 -> {
                    val nothing = Question()
                    nothing.setQuestionId(Integer(-2))
                    mQuestionList.add(nothing)
                    val padding = Question()
                    padding.setQuestionId(Integer(-1))
                    mQuestionList.add(padding)
                    mQuestionList.add(questions.get(i))
                }
                4 -> {
                    val nothing = Question()
                    nothing.setQuestionId(Integer(-1))
                    mQuestionList.add(nothing)
                    mQuestionList.add(questions.get(i))
                    val padding = Question()
                    padding.setQuestionId(Integer(-1))
                    mQuestionList.add(padding)
                }
            }
            row++
            if(row == 5)
                row = 1
        }

    }

    private fun drawQuestions() {

        var cnt : Int = 0

        for (i in 0 .. mQuestionList.size - 1) {

            val view = LayoutInflater.from(context).inflate(R.layout.custom_wrong_question, null, false)
            val image : ImageView = view.findViewById(R.id.image)

            val number = mQuestionList.get(i).getQuestionId().toString()

            when (mQuestionList.get(i).getSubjectType()) {
                SubjectType.NONE -> {
                    image.background = resources.getDrawable(R.drawable.main_icon)
                }
                SubjectType.MATHEMATICS -> {
                    image.background = resources.getDrawable(R.drawable.math_icon)
                }
                SubjectType.SPANISH -> {
                    image.background = resources.getDrawable(R.drawable.spanish_icon)
                }
                SubjectType.VERBAL -> {
                    // TODO
                    image.background = resources.getDrawable(R.drawable.user_unselected_icon)
                }
                SubjectType.CHEMISTRY -> {
                    image.background = resources.getDrawable(R.drawable.chemic_icon)
                }
                SubjectType.BIOLOGY -> {
                    // TODO
                    image.background = resources.getDrawable(R.drawable.user_selected_icon)
                }
                SubjectType.GEOGRAPHY -> {
                    image.background = resources.getDrawable(R.drawable.geo_icon)
                }
                SubjectType.MEXICO_HISTORY -> {
                    // TODO
                    image.background = resources.getDrawable(R.drawable.school_selected_icon)
                }
                SubjectType.UNIVERSAL_HISTORY -> {
                    // TODO
                    image.background = resources.getDrawable(R.drawable.school_unselected_icon)
                }
            }

            // params for module
            val param = GridLayout.LayoutParams()

            if (number.equals("-1")) {
                view.background = resources.getDrawable(R.drawable.empty_square)
                image.visibility = View.GONE
                param.height = resources.getDimension(R.dimen.height_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
            } else if(number.equals("-2")) {
                view.background = resources.getDrawable(R.drawable.square_first_module_background)
                image.visibility = View.GONE
                param.height = resources.getDimension(R.dimen.height_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
            } else {
                param.height = resources.getDimension(R.dimen.height_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
                view.setOnClickListener(View.OnClickListener {
                    Log.d(TAG, "onClick: number --- " + number)
                    goQuestionActivity(Integer.parseInt(number))
                })
            }



            when (cnt) {
                0 -> {
                    mLeftTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
                1 -> {
                    mCenterTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
                2 -> {
                    mRightTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
            }

            cnt++
            if (cnt==3) { cnt = 0 }
        }

    }

    private fun goQuestionActivity(questionId : Int) {
        val intent = Intent(activity, QuestionActivity::class.java)
        intent.putExtra(QUESTION_ID, questionId)
        intent.putExtra(ANONYMOUS_USER, false)
        intent.putExtra(FROM_WRONG_QUESTION, true)
        this.startActivity(intent)
    }
}