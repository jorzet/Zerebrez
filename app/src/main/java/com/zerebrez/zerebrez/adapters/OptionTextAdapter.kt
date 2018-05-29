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

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zerebrez.zerebrez.R
import kotlinx.android.synthetic.main.custom_option_text.view.*

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

class OptionTextAdapter(optionText : List<String>, context : Context) : BaseAdapter() {
    private val mOptionText : List<String> = optionText
    private val mContext: Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentOption = this.mOptionText.get(position)

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val optionView = inflator.inflate(R.layout.custom_option_text, null)
        optionView.tv_option.text = currentOption

        return optionView
    }

    override fun getItem(position: Int): Any {
        return this.mOptionText.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.mOptionText.size
    }

}