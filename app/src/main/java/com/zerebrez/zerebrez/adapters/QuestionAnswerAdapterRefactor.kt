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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.utils.FontUtil
import katex.hourglass.`in`.mathlib.MathView
import com.felipecsl.gifimageview.library.GifImageView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.zerebrez.zerebrez.models.User
import com.bumptech.glide.Glide
import com.zerebrez.zerebrez.utils.GifDataDownloader

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

class QuestionAnswerAdapterRefactor (isAnswer : Boolean,
                                     questionNewFormat : QuestionNewFormat,
                                     imagesPath : List<Image>,
                                     user: User,
                                     context: Context) : RecyclerView.Adapter<QuestionAnswerViewHolder>() {

    private val mQuestionNewFormat = questionNewFormat
    private val mImagesPath : List<Image> = imagesPath
    private val mUser: User = user
    private val mContext : Context = context
    private val mIsAnswer : Boolean = isAnswer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionAnswerViewHolder {
        // infalte the item Layout
        val view: View
        if (mIsAnswer) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_answer_refactor, parent, false)
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_question_refactor, parent, false)
        }
        // set the view's size, margins, paddings and layout parameters
        return QuestionAnswerViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (mIsAnswer) {
            return mQuestionNewFormat.stepByStepData.size
        }
        return mQuestionNewFormat.questionData.size + 1
    }

    override fun onBindViewHolder(holder: QuestionAnswerViewHolder, position: Int) {
        if (holder != null) {
            if (!mIsAnswer && position.equals(0)) {
                holder.mAnswerTheQuestion.typeface = FontUtil.getNunitoRegular(mContext)
                holder.mAnswerTheQuestion.visibility = View.VISIBLE
                holder.mOptionText.visibility = View.GONE
                holder.mOptionMath.visibility = View.GONE
                holder.mOptionImage.visibility = View.GONE
                holder.mOptionGifImage.visibility = View.GONE
            } else {

                val currentQuestion: String
                val questionType: String

                if (mIsAnswer) {
                    currentQuestion = getItem(position) as String
                    questionType = getItemType(position)
                } else {
                    currentQuestion = getItem(position) as String
                    questionType = getItemType(position)
                }

                when (questionType) {
                    "txt" -> {
                        holder.mOptionText.text = currentQuestion
                        holder.mOptionText.typeface = FontUtil.getNunitoRegular(mContext)
                        holder.mOptionText.visibility = View.VISIBLE
                        holder.mOptionMath.visibility = View.GONE
                        holder.mOptionImage.visibility = View.GONE
                        holder.mOptionGifImage.visibility = View.GONE

                    }
                    "eq" -> {
                        //optionView.mv_otion.text = "$$"+currentOption.getQuestion()+"$$"
                        holder.mOptionMath.setDisplayText("$$" + currentQuestion + "$$")
                        holder.mOptionText.visibility = View.GONE
                        holder.mOptionMath.visibility = View.VISIBLE
                        holder.mOptionImage.visibility = View.GONE
                        holder.mOptionGifImage.visibility = View.GONE
                    }
                    "img" -> {
                        val nameInStorage = getNameInStorage(currentQuestion, mImagesPath)
                        if (nameInStorage.contains(".gif")) {
                            val bitmap = getBitmap(nameInStorage)
                            holder.mOptionGifImage.setImageBitmap(bitmap)
                            holder.mOptionGifImage.startAnimation()

                            FirebaseStorage
                                    .getInstance()
                                    .getReference()
                                    .child(mUser.getCourse() + "/images/${nameInStorage}")
                                    .getDownloadUrl()
                                    .addOnSuccessListener(object: OnSuccessListener<Uri> {
                                        override fun onSuccess(uri: Uri?) {

                                            Glide.with(mContext)
                                                    .asGif()
                                                    .load(uri.toString())
                                                    .into(holder.mOptionImage);
                                        }
                                    }).addOnFailureListener(object: OnFailureListener {
                                        override fun onFailure(exception: java.lang.Exception) {

                                        }
                                    })
                            /*object : GifDataDownloader() {
                            override fun onPostExecute(bytes: ByteArray) {
                                holder.mOptionGifImage.setBytes(bytes)
                                holder.mOptionGifImage.startAnimation()
                            }
                            }.execute("http://katemobile.ru/tmp/sample3.gif")*/

                            holder.mOptionText.visibility = View.GONE
                            holder.mOptionMath.visibility = View.GONE
                            holder.mOptionImage.visibility = View.VISIBLE
                            holder.mOptionGifImage.visibility = View.GONE
                        } else {
                            holder.mOptionImage.setImageBitmap(getBitmap(nameInStorage))
                            holder.mOptionText.visibility = View.GONE
                            holder.mOptionMath.visibility = View.GONE
                            holder.mOptionImage.visibility = View.VISIBLE
                            holder.mOptionGifImage.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun getItem(position: Int): Any {
        if (mIsAnswer) {
            return mQuestionNewFormat.stepByStepData.get(position)
        }
        return mQuestionNewFormat.questionData.get(position - 1)
    }

    private fun getItemType(position: Int): String {
        if (mIsAnswer) {
            return mQuestionNewFormat.stepByStepTypes.get(position)
        }
        return mQuestionNewFormat.questionTypes.get(position -1)
    }

    private fun getNameInStorage(imageId : String, mImagesPath : List<Image>) : String {
        var nameInStorage = ""
        for (image in mImagesPath) {
            if (imageId.equals("i"+image.getImageId())) {
                nameInStorage = image.getNameInStorage()
                return nameInStorage
            }
        }
        return ""
    }

    fun getBitmap(path: String): Bitmap? {
        try {

            var bitmap: Bitmap? = null
            val f = mContext.openFileInput(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            bitmap = BitmapFactory.decodeStream(f, null, options)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }
}

class QuestionAnswerViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val mOptionText = view.findViewById(R.id.tv_option) as TextView
    val mOptionMath = view.findViewById(R.id.mv_option) as MathView
    val mOptionImage = view.findViewById(R.id.iv_option) as ImageView
    val mOptionGifImage = view.findViewById(R.id.giv_option) as GifImageView
    val mAnswerTheQuestion = view.findViewById(R.id.tv_answer_the_question) as TextView
}