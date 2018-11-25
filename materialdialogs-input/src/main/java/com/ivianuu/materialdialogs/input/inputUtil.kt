/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.materialdialogs.input

import android.view.inputmethod.InputMethodManager
import com.ivianuu.materialdialogs.DialogButton.POSITIVE
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.action.setActionButtonEnabled

internal fun MaterialDialog.invalidateButtons(
    allowEmptyInput: Boolean
) {
    // max length
    val maxLength = getInputLayout()?.counterMaxLength ?: return
    val currentLength = getInputField()?.text?.length ?: 0

    val lengthSatisfied = maxLength <= 0 || currentLength <= maxLength
    val emptyInputSatisfied = allowEmptyInput || currentLength > 0

    setActionButtonEnabled(POSITIVE, lengthSatisfied && emptyInputSatisfied)
}

internal fun MaterialDialog.showKeyboardIfApplicable() {
    val editText = getInputField() ?: return
    editText.post {
        editText.requestFocus()
        val imm =
            context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }
}