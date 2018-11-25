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

@file:Suppress("unused")

package com.ivianuu.materialdialogs.input

import com.google.android.material.textfield.TextInputLayout
import com.ivianuu.materialdialogs.DialogButton.POSITIVE
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.action.hasActionButtons
import com.ivianuu.materialdialogs.action.setActionButtonEnabled
import com.ivianuu.materialdialogs.callback.onShow
import com.ivianuu.materialdialogs.customview.customView
import com.ivianuu.materialdialogs.customview.getCustomView
import com.ivianuu.materialdialogs.util.MDUtil.textChanged

typealias InputCallback = ((MaterialDialog, CharSequence) -> Unit)?

fun MaterialDialog.getInputLayout() = getCustomView() as? TextInputLayout

fun MaterialDialog.getInputField() = getInputLayout()?.editText

/**
 * Shows an input field as the content of the dialog. Can be used with a message and checkbox
 * prompt, but cannot be used with a list.
 */
fun MaterialDialog.input(
    hint: String? = null,
    hintRes: Int? = null,
    prefill: CharSequence? = null,
    prefillRes: Int? = null,
    inputType: Int = android.text.InputType.TYPE_CLASS_TEXT,
    maxLength: Int? = null,
    allowEmptyInput: Boolean = false,
    waitForPositiveButton: Boolean = true,
    callback: InputCallback = null
) = apply {
    customView(R.layout.md_dialog_stub_input)
    onShow { showKeyboardIfApplicable() }
    if (!hasActionButtons()) {
        positiveButton(android.R.string.ok)
    }

    if (callback != null && waitForPositiveButton) {
        // Add an additional callback to invoke the input listener after the positive AB is pressed
        positiveButton { callback.invoke(this@input, getInputField()?.text ?: "") }
    }

    val resources = context.resources
    val editText = getInputField() ?: return this

    val prefillText = prefill ?: if (prefillRes != null) resources.getString(prefillRes) else null
    if (prefillText != null) {
        editText.setText(prefillText)
        onShow { editText.setSelection(prefillText.length) }
    }
    setActionButtonEnabled(
        POSITIVE,
        !waitForPositiveButton || prefillText?.isNotEmpty() == true
    )

    editText.hint = hint ?: if (hintRes != null) resources.getString(hintRes) else null
    editText.inputType = inputType

    if (maxLength != null) {
        getInputLayout()?.run {
            isCounterEnabled = true
            counterMaxLength = maxLength
        }
    }

    // Add text change listener to invalidate max length enabled state
    editText.textChanged { invalidateButtons(allowEmptyInput) }
    invalidateButtons(allowEmptyInput)

    editText.textChanged {
        setActionButtonEnabled(POSITIVE, it.isNotEmpty())
        if (!waitForPositiveButton && callback != null) {
            // We aren't waiting for positive, so invoke every time the text changes
            callback.invoke(this, it)
        }
    }
}