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

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.R
import com.ivianuu.materialdialogs.checkbox.getCheckBoxPrompt
import com.ivianuu.materialdialogs.customview.CUSTOM_VIEW_NO_PADDING
import com.ivianuu.materialdialogs.util.MDUtil.resolveDrawable
import com.ivianuu.materialdialogs.util.MDUtil.resolveString

internal fun MaterialDialog.setWindowConstraints() {
  //  (context as? Activity)?.window?.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE) ?: return
    val wm = (context.getSystemService(WINDOW_SERVICE) as WindowManager)

    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    val windowWidth = size.x
    val windowHeight = size.y

    context.resources.run {
        val windowVerticalPadding = getDimensionPixelSize(
            R.dimen.md_dialog_vertical_margin
        )
        val windowHorizontalPadding = getDimensionPixelSize(
            R.dimen.md_dialog_horizontal_margin
        )
        val maxWidth = getDimensionPixelSize(R.dimen.md_dialog_max_width)
        val calculatedWidth = windowWidth - windowHorizontalPadding * 2

        dialogLayout.maxWidth = Math.min(maxWidth, calculatedWidth)
        dialogLayout.maxHeight = windowHeight - windowVerticalPadding * 2
    }
}

internal fun MaterialDialog.setDefaults() {
    // Background color and corner radius
    var backgroundColor = resolveColor(attr = R.attr.md_background_color)
    if (backgroundColor == 0) {
        backgroundColor = resolveColor(attr = R.attr.colorBackgroundFloating)
    }
    colorBackground(color = backgroundColor)
    // Fonts
    this.titleFont = font(attr = R.attr.md_font_title)
    this.bodyFont = font(attr = R.attr.md_font_body)
    this.buttonFont = font(attr = R.attr.md_font_button)
}

fun MaterialDialog.invalidateDividers(
    scrolledDown: Boolean,
    atBottom: Boolean
) = dialogLayout.invalidateDividers(scrolledDown, atBottom)

internal fun MaterialDialog.preShow() {
    val customViewNoPadding = config[CUSTOM_VIEW_NO_PADDING] as? Boolean == true

    dialogLayout.run {
        if (titleLayout.shouldNotBeVisible() && !customViewNoPadding) {
            // Reduce top and bottom padding if we have no title
            contentLayout.modifyFirstAndLastPadding(
                top = frameMarginVerticalLess,
                bottom = frameMarginVerticalLess
            )
        }
        if (getCheckBoxPrompt().isVisible()) {
            // Zero out bottom content padding if we have a checkbox prompt
            contentLayout.modifyFirstAndLastPadding(bottom = 0)
        } else if (contentLayout.haveMoreThanOneChild()) {
            contentLayout.modifyScrollViewPadding(bottom = frameMarginVerticalLess)
        }
    }
}

internal fun MaterialDialog.populateIcon(
    imageView: ImageView,
    iconRes: Int?,
    icon: Drawable?
) {
    val drawable = resolveDrawable(context, res = iconRes, fallback = icon)
    if (drawable != null) {
        (imageView.parent as View).visibility = View.VISIBLE
        imageView.visibility = View.VISIBLE
        imageView.setImageDrawable(drawable)
    } else {
        imageView.visibility = View.GONE
    }
}

internal fun MaterialDialog.populateText(
    textView: TextView,
    textRes: Int? = null,
    text: CharSequence? = null,
    fallback: Int = 0,
    typeface: Typeface?,
    textColor: Int? = null
) {
    val value = text ?: resolveString(this, textRes, fallback)
    if (value != null) {
        (textView.parent as View).visibility = View.VISIBLE
        textView.visibility = View.VISIBLE
        textView.text = value
        if (typeface != null) {
            textView.typeface = typeface
        }
        textView.maybeSetTextColor(context, textColor)
    } else {
        textView.visibility = View.GONE
    }
}

internal fun MaterialDialog.hideKeyboard() {
    val imm =
        context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    val windowToken = container.windowToken
    if (windowToken != null) {
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

internal fun MaterialDialog.colorBackground(color: Int) = apply {
    val drawable = GradientDrawable()
    drawable.cornerRadius = dimen(attr = R.attr.md_corner_radius)
    drawable.setColor(color)
    // todo window?.setBackgroundDrawable(drawable)
}