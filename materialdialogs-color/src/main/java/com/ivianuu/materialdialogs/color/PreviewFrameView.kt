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

package com.ivianuu.materialdialogs.color

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.ivianuu.materialdialogs.color.util.hexValue
import com.ivianuu.materialdialogs.color.util.toColor
import com.ivianuu.materialdialogs.util.MDUtil.isColorDark
import com.ivianuu.materialdialogs.util.MDUtil.textChanged

internal typealias HexColorChanged = (Int) -> Boolean


internal class PreviewFrameView(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        const val HEX_VALUE_ALPHA_THRESHOLD = 50
    }

    lateinit var argbView: View
    lateinit var hexPrefixView: TextView
    lateinit var hexValueView: EditText

    var supportCustomAlpha: Boolean = true
    var onHexChanged: HexColorChanged = { true }

    init {
        setBackgroundResource(R.drawable.transparent_rect_repeat)
        LayoutInflater.from(context)
            .inflate(R.layout.md_color_chooser_preview_frame, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        argbView = findViewById(R.id.argbView)
        hexPrefixView = findViewById(R.id.hexPrefixView)
        hexValueView = findViewById(R.id.hexValueView)

        hexValueView.textChanged {
            if (it.length < 4) {
                return@textChanged
            }
            val newColor = it.toString().toColor() ?: return@textChanged
            if (onHexChanged(newColor)) {
                hexValueView.post { hexValueView.setSelection(hexValueView.text.length) }
            }
        }
    }

    fun setColor(color: Int) {
        argbView.background = ColorDrawable(color)
        hexValueView.setText(color.hexValue(supportCustomAlpha))
        hexValueView.post { hexValueView.setSelection(hexValueView.text.length) }

        val tintColor = if (color.isColorDark() &&
            Color.alpha(color) >= HEX_VALUE_ALPHA_THRESHOLD
        ) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        hexPrefixView.setTextColor(tintColor)
        hexValueView.setTextColor(tintColor)
        ViewCompat.setBackgroundTintList(hexValueView, ColorStateList.valueOf(tintColor))
    }
}
