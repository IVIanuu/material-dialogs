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
import android.view.View
import android.widget.FrameLayout
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.R

internal class DialogContainer(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    internal lateinit var dialog: MaterialDialog
    internal lateinit var dialogLayout: DialogLayout

    private lateinit var touchOutside: View

    override fun onFinishInflate() {
        super.onFinishInflate()

        dialogLayout = findViewById(R.id.md_dialog_layout)
        touchOutside = findViewById(R.id.md_touch_outside)

        touchOutside.setOnTouchListener { v, event ->
            if (dialog.cancelable && dialog.isShowing && dialog.cancelOnTouchOutside) {
                dialog.cancel()
                true
            } else {
                false
            }
        }
    }

}