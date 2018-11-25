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

package com.ivianuu.materialdialogs.checkbox

import android.view.View
import android.widget.CheckBox
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.R
import com.ivianuu.materialdialogs.assertOneSet
import com.ivianuu.materialdialogs.util.MDUtil.resolveString
import com.ivianuu.materialdialogs.util.maybeSetTextColor

typealias BooleanCallback = ((Boolean) -> Unit)?

fun MaterialDialog.getCheckBoxPrompt(): CheckBox = buttonsLayout.checkBoxPrompt

fun MaterialDialog.isCheckPromptChecked() = getCheckBoxPrompt().isChecked

/**
 */
fun MaterialDialog.checkBoxPrompt(
    res: Int = 0,
    text: String? = null,
    isCheckedDefault: Boolean = false,
    onToggle: BooleanCallback
) = apply {
    assertOneSet("checkBoxPrompt", text, res)
    buttonsLayout.checkBoxPrompt.run {
        visibility = View.VISIBLE
        this.text = text ?: resolveString(this@checkBoxPrompt, res)
        isChecked = isCheckedDefault
        setOnCheckedChangeListener { _, checked ->
            onToggle?.invoke(checked)
        }
        maybeSetTextColor(context, R.attr.md_color_content)
    }
}
