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

package com.zerebrez.zerebrez.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.SelectedSchoolsListAdapter
import com.zerebrez.zerebrez.models.Institute
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.NetworkUtil

/*
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

class ChooseSchoolsActivity : BaseActivityLifeCycle(), ErrorDialog.OnErrorDialogListener {

    /*
     * Tags
     */
    private val TAG : String = "ChooseSchoolsActivity"
    private val SHOW_CONTINUE_BUTTON : String = "show_continue_button"

    /*
     * UI accessors
     */
    private lateinit var mToolBar : Toolbar
    private lateinit var mFirstSchoolContainer : View
    private lateinit var mSecondSchoolContainer : View
    private lateinit var mThirdSchoolContainer : View
    private lateinit var mContinueContainer : View
    private lateinit var mFirstSchoolText : TextView
    private lateinit var mSecondSchoolText : TextView
    private lateinit var mThirdSchoolText : TextView
    private lateinit var mDropFirstSchool : ImageView
    private lateinit var mDropSecondSchool : ImageView
    private lateinit var mDropThirdSchool : ImageView
    private lateinit var mInstitutesSchoolList : ExpandableListView
    private lateinit var mContinueButton : Button

    /*
     * Adapters
     */
    private lateinit var mSelectedSchoolsListAdapter: SelectedSchoolsListAdapter

    /*
     * Objects
     */
    private var mSchools = arrayListOf<School>()

    /*
     * Some variables
     */
    private var showContinueButton : Boolean = false
    private var hasChanges : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_schools)

        mToolBar = findViewById(R.id.toolbar)

        setSupportActionBar(mToolBar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true);

        mFirstSchoolContainer = findViewById(R.id.rl_first_option)
        mSecondSchoolContainer = findViewById(R.id.rl_second_option)
        mThirdSchoolContainer = findViewById(R.id.rl_third_option)
        mContinueContainer = findViewById(R.id.rl_continue_container)
        mFirstSchoolText = findViewById(R.id.tv_first_selected_school)
        mSecondSchoolText = findViewById(R.id.tv_second_selected_school)
        mThirdSchoolText = findViewById(R.id.tv_third_selected_school)
        mDropFirstSchool = findViewById(R.id.iv_cross_1)
        mDropSecondSchool = findViewById(R.id.iv_cross_2)
        mDropThirdSchool = findViewById(R.id.iv_cross_3)
        mInstitutesSchoolList = findViewById(R.id.lv_schools_list)
        mContinueButton = findViewById(R.id.btn_continue)

        mDropFirstSchool.setOnClickListener(mDropFirstSchoolListener)
        mDropSecondSchool.setOnClickListener(mDropSecondSchoolListener)
        mDropThirdSchool.setOnClickListener(mDropThridSchoolListener)
        mContinueButton.setOnClickListener(mContinueListener)

        mFirstSchoolContainer.visibility = View.GONE
        mSecondSchoolContainer.visibility = View.GONE
        mThirdSchoolContainer.visibility = View.GONE

        showContinueButton = intent.extras.getBoolean(SHOW_CONTINUE_BUTTON)
        if (showContinueButton) {
            // this is shown after signUp fragment
            mContinueButton.setText(resources.getString(R.string.continue_text))

        } else {
            val user = getUser()

            if (user != null) {
                // this is shown in profile configuration
                mSchools.addAll(user.getSelectedSchools())
                if (mSchools.isNotEmpty()) {
                    for (i in 0..mSchools.size - 1) {
                        if (i == 0) {
                            mFirstSchoolText.text = mSchools.get(i).getSchoolName()
                            mFirstSchoolContainer.visibility = View.VISIBLE
                        } else if (i == 1) {
                            mSecondSchoolText.text = mSchools.get(i).getSchoolName()
                            mSecondSchoolContainer.visibility = View.VISIBLE
                        } else if (i == 2) {
                            mThirdSchoolText.text = mSchools.get(i).getSchoolName()
                            mThirdSchoolContainer.visibility = View.VISIBLE
                        }
                    }
                }
                mContinueButton.setText(resources.getString(R.string.ok_text))
            }
        }

        val dataHelper = DataHelper(baseContext)
        val institutes = dataHelper.getInstitutes()

        if (institutes.isNotEmpty()) {
            mSelectedSchoolsListAdapter = SelectedSchoolsListAdapter(institutes, baseContext)
            mInstitutesSchoolList.setAdapter(mSelectedSchoolsListAdapter)
            mInstitutesSchoolList.setOnChildClickListener(onSchoolClickListener)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (hasChanges) {
            ErrorDialog.newInstance("Seguro que quiere salir sin guardar cambios",
                    DialogType.ACCEPT_CANCEL_DIALOG, this)!!.show(supportFragmentManager, "warningDialog")
        } else {
            onBackPressed()
        }
        return true
    }

    private val mDropFirstSchoolListener = View.OnClickListener {
        onDeleteSchool(0)

        if (!mSecondSchoolText.text.equals("") && !mThirdSchoolText.text.equals("")) {
            mFirstSchoolText.text = mSecondSchoolText.text
            mSecondSchoolText.text = mThirdSchoolText.text
            mThirdSchoolText.text = ""
            mThirdSchoolContainer.visibility = View.GONE
        } else if (!mSecondSchoolText.text.equals("") && mThirdSchoolText.text.equals("")) {
            mFirstSchoolText.text = mSecondSchoolText.text
            mSecondSchoolText.text = ""
            mSecondSchoolContainer.visibility = View.GONE
        } else {
            mFirstSchoolText.text = ""
            mFirstSchoolContainer.visibility = View.GONE
        }

        hasChanges = true
    }

    private val mDropSecondSchoolListener = View.OnClickListener {
        onDeleteSchool(1)

        if (!mThirdSchoolText.text.equals("")) {
            mSecondSchoolText.text = mThirdSchoolText.text
            mThirdSchoolText.text = ""
            mThirdSchoolContainer.visibility = View.GONE
        } else {
            mSecondSchoolText.text = ""
            mSecondSchoolContainer.visibility = View.GONE
        }

        hasChanges = true
    }

    private val mDropThridSchoolListener = View.OnClickListener {
        onDeleteSchool(2)
        mThirdSchoolText.text = ""
        mThirdSchoolContainer.visibility = View.GONE
        hasChanges = true
    }

    private val onSchoolClickListener = object : ExpandableListView.OnChildClickListener {
        override fun onChildClick(expandableListView: ExpandableListView?, view: View?, groupPosition: Int, childPosition: Int, l: Long): Boolean {
            val school = mSelectedSchoolsListAdapter.getChild(groupPosition, childPosition) as School
            val institute = mSelectedSchoolsListAdapter.getGroup(groupPosition) as Institute
            school.setInstituteId(institute.getInstituteId())
            onSchoolSelected(school)
            hasChanges = true
            return false
        }
    }

    private fun onSchoolSelected(school: School) {
        var moreThanOne = false
        for (i in 0 .. mSchools.size - 1) {
            if (mSchools.get(i).getSchoolId().equals(school.getSchoolId())) {
                moreThanOne = true
                break
            }
        }
        if (mSchools.size < 3 && !moreThanOne) {
            if (mFirstSchoolText.text.equals("")) {
                mFirstSchoolText.text = school.getSchoolName()
                mFirstSchoolContainer.visibility = View.VISIBLE
                mSchools.add(school)
            } else if (mSecondSchoolText.text.equals("")) {
                mSecondSchoolText.text = school.getSchoolName()
                mSecondSchoolContainer.visibility = View.VISIBLE
                mSchools.add(school)
            } else if (mThirdSchoolText.text.equals("")) {
                mThirdSchoolText.text = school.getSchoolName()
                mThirdSchoolContainer.visibility = View.VISIBLE
                mSchools.add(school)
            }
        }
    }

    private fun onDeleteSchool(position : Int) {
        mSchools.removeAt(position)
    }

    private val mContinueListener = View.OnClickListener {
        if (NetworkUtil.isConnected(baseContext)) {
            requestSendSelectedSchools(mSchools)
        } else {
            requestSendSelectedSchools(mSchools)
            val user = getUser()
            if (user != null) {
                user.setSelectedShools(mSchools)
                saveUser(user)
            }
            onBackPressed()
        }
    }

    private fun goContentActivity() {
        val intent = Intent(this, ContentActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun onSendSelectedSchoolsSuccess(success: Boolean) {
        super.onSendSelectedSchoolsSuccess(success)

        val user = getUser()
        if (user != null) {
            user.setSelectedShools(mSchools)
            saveUser(user)
        }

        if (showContinueButton) {
            goContentActivity()
        } else {
            onBackPressed()
        }
    }

    override fun onSendSelectedSchoolsFail(throwable: Throwable) {
        super.onSendSelectedSchoolsFail(throwable)
        ErrorDialog.newInstance("Ocurrio un Error", "Vuelve a intentarlo",
                DialogType.OK_DIALOG, this)!!.show(supportFragmentManager, "warningDialog")
    }

    /*
     * Dialog listeners
     */
    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {
        onBackPressed()
    }

}