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

package com.ivianuu.materialdialogs.util

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.ivianuu.materialdialogs.MaterialDialog

object MDUtil {

    fun resolveString(
        materialDialog: MaterialDialog,
        res: Int? = null,
        fallback: Int? = null,
        html: Boolean = false
    ): CharSequence? {
        return resolveString(
            context = materialDialog.context,
            res = res,
            fallback = fallback,
            html = html
        )
    }

    fun resolveString(
        context: Context,
        res: Int? = null,
        fallback: Int? = null,
        html: Boolean = false
    ): CharSequence? {
        val resourceId = res ?: (fallback ?: 0)
        if (resourceId == 0) return null
        val text = context.resources.getText(resourceId)
        if (html) {
            @Suppress("DEPRECATION")
            return Html.fromHtml(text.toString())
        }
        return text
    }

    fun resolveDrawable(
        context: Context,
        res: Int? = null,
        attr: Int? = null,
        fallback: Drawable? = null
    ): Drawable? {
        if (attr != null) {
            val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
            try {
                var d = a.getDrawable(0)
                if (d == null && fallback != null) {
                    d = fallback
                }
                return d
            } finally {
                a.recycle()
            }
        }
        if (res == null) return fallback
        return ContextCompat.getDrawable(context, res)
    }

    fun resolveColor(
        context: Context,
        res: Int? = null,
        attr: Int? = null
    ): Int {
        if (attr != null) {
            val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
            try {
                return a.getColor(0, 0)
            } finally {
                a.recycle()
            }
        }
        return ContextCompat.getColor(context, res ?: 0)
    }

    fun Int.isColorDark(): Boolean {
        if (this == Color.TRANSPARENT) {
            return false
        }
        val darkness =
            1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
        return darkness >= 0.5
    }

    fun <T : View> T.dimenPx(res: Int): Int {
        return context.resources.getDimensionPixelSize(res)
    }

    fun isLandscape(context: Context) =
        context.resources.configuration.orientation == ORIENTATION_LANDSCAPE

    fun EditText.textChanged(callback: (CharSequence) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) = Unit

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) = callback.invoke(s)
        })
    }
}
