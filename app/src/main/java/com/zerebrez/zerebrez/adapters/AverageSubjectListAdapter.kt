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

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.Subject
import com.zerebrez.zerebrez.models.enums.SubjectType
import android.graphics.drawable.GradientDrawable
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_average_by_subject.view.*

/*
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

class AverageSubjectListAdapter (subjects : List<Subject>, context : Context) : BaseAdapter() {
    private val mSubject: List<Subject> = subjects
    private val mContext: Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentSubject = getItem(position) as Subject

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val averageBySubject = inflator.inflate(R.layout.custom_average_by_subject, null)

        // generate random color
        val color = ColorGenerator.MATERIAL.getColor(getItem(position))
        val gd = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(color,color))
        gd.cornerRadius = 5f

        //val layers = arrayOf<Drawable>(layer1, layer2, layer3)

        averageBySubject.subject_type.text = currentSubject.getsubjectType().value

        averageBySubject.tv_average_by_subject.text = currentSubject.getSubjectAverage().toString()
        averageBySubject.pb_average_by_subject.setProgress((currentSubject.getSubjectAverage() * 10).toInt())

        averageBySubject.subject_type.typeface = FontUtil.getNunitoSemiBold(mContext)
        averageBySubject.tv_average_by_subject.typeface = FontUtil.getNunitoSemiBold(mContext)

        //averageBySubject.pb_average_by_subject.progressDrawable = gd

        when (currentSubject.getsubjectType()) {
            SubjectType.ENGLISH -> {
                //averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.geo_icon)
            }
            SubjectType.VERBAL_HABILITY -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.hab_ver_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_hab_ver_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.verbal_habilitiy_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.verbal_habilitiy_subject_color))
            }
            SubjectType.MATHEMATICAL_HABILITY -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.hab_mat_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_hab_mat_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.mathematical_habilitiy_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.mathematical_habilitiy_subject_color))
            }
            SubjectType.SPANISH -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.esp_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_spanish_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.spanish_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.spanish_subject_color))
            }
            SubjectType.MATHEMATICS -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.mat_1_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_mathematics_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
            }
            SubjectType.BIOLOGY -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.bio_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_biology_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.biology_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.biology_subject_color))
            }
            SubjectType.PHYSICS -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.fis_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_physics_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.physics_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.physics_subject_color))
            }
            SubjectType.CHEMISTRY -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.quim_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_chemistry_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.chemistry_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.chemistry_subject_color))
            }
            SubjectType.GEOGRAPHY -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.geo_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_geography_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.geography_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.geography_subject_color))
            }
            SubjectType.MEXICO_HISTORY -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.his_mex_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_mexico_history_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.mexico_history_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.mexico_history_subject_color))
            }
            SubjectType.UNIVERSAL_HISTORY -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.his_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_history_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
            }
            SubjectType.FCE -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.civ_et_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_fce_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.fce_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.fce_subject_color))
            }
            SubjectType.PHILOSOPHY_AREA -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.filo_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_history_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
            }
            SubjectType.PHILOSOPHY_AREA_4 -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.filo_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_history_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
            }
            SubjectType.PHILOSOPHY -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.filo_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_history_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
            }
            SubjectType.LITERATURE -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.hab_ver_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_hab_ver_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.verbal_habilitiy_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.verbal_habilitiy_subject_color))
            }
            SubjectType.CHEMISTRY_AREA -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.quim_plus_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_fce_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.fce_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.fce_subject_color))
            }
            SubjectType.CHEMISTRY_AREA_2 -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.quim_plus_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_fce_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.fce_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.fce_subject_color))
            }
            SubjectType.MATEMATICS_AREA -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.mat_plus_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_mathematics_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
            }
            SubjectType.MATEMATICS_AREA_1_2 -> {
                averageBySubject.iv_average_by_subject.background = mContext.resources.getDrawable(R.drawable.mat_plus_subject_icon_blue)
                averageBySubject.pb_average_by_subject.progressDrawable = mContext.resources.getDrawable(R.drawable.progress_mathematics_subject)
                averageBySubject.tv_average_by_subject.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
                averageBySubject.subject_type.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
            }
        }

        return averageBySubject
    }

    override fun getItem(position: Int): Any {
        return this.mSubject.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.mSubject.size
    }

}
