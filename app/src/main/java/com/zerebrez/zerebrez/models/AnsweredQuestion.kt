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

package com.zerebrez.zerebrez.models

import com.zerebrez.zerebrez.models.enums.SubjectType

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

class AnsweredQuestion {
    private var questionId : Integer = Integer(0)
    private var moduleId : Integer = Integer(0)
    private var answer : String = ""
    private var optionChoosed : String = ""
    private var wasOK : Boolean = false
    private var subjectType : SubjectType = SubjectType.NONE

    /**
     * @param questionId
     *      Set the question id
     */
    fun setQuestionId(questionId : Integer) {
        this.questionId = questionId
    }

    /**
     * @return
     *      the question id
     */
    fun getQuestionId() : Integer {
        return this.questionId
    }

    /**
     * @param moduleId
     *      Set the module id
     */
    fun setModuleId(moduleId : Integer) {
        this.moduleId = moduleId
    }

    /**
     * @return
     *      the module id
     */
    fun getModuleId() : Integer {
        return this.moduleId
    }

    /**
     * @param answer
     *      Set the question answer
     */
    fun setAnswer(answer : String) {
        this.answer = answer
    }

    /**
     * @return
     *      the question answer
     */
    fun getAnswer() : String {
        return this.answer
    }

    /**
     * @param optionChoosed
     *      Set the option choosed
     */
    fun setOptionChoosed(optionChoosed : String) {
        this.optionChoosed = optionChoosed
    }

    /**
     * @return
     *      The option choosed
     */
    fun getOptionChoosed() : String {
        return this.optionChoosed
    }

    /**
     * @param wasOK
     *      if was ok the answer
     */
    fun setWasOK(wasOK : Boolean) {
        this.wasOK = wasOK
    }

    /**
     * @return
     *      if answer was ok
     */
    fun getWasOK() : Boolean {
        return this.wasOK
    }

    /**
     * @param subjectType
     *      the subject type
     */
    fun setSubjectType(subjectType : SubjectType) {
        this.subjectType = subjectType
    }

    /**
     * @return
     *      the subject type
     */
    fun getSubjectType() : SubjectType {
        return this.subjectType
    }
}
