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

package com.ivianuu.materialdialogs.internal

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER
import com.google.android.material.button.MaterialButton
import com.ivianuu.materialdialogs.R
import com.ivianuu.materialdialogs.util.MDUtil.dimenPx
import com.ivianuu.materialdialogs.util.setGravityEndCompat
import com.ivianuu.materialdialogs.util.updatePadding

internal class DialogActionButton(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialButton(context, attrs) {

    private val paddingDefault = dimenPx(R.dimen.md_action_button_padding_horizontal)
    private val paddingStacked = dimenPx(R.dimen.md_stacked_action_button_padding_horizontal)

    init {
        isClickable = true
        isFocusable = true
    }

    fun update(stacked: Boolean) {
        // Padding
        val sidePadding = if (stacked) paddingStacked else paddingDefault
        updatePadding(left = sidePadding, right = sidePadding)

        // Text alignment
        if (stacked) setGravityEndCompat()
        else gravity = CENTER
    }

}
