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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.util.AttributeSet
import android.util.Log
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.getSize
import android.view.View.MeasureSpec.makeMeasureSpec
import com.google.android.material.card.MaterialCardView
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.R
import com.ivianuu.materialdialogs.util.MDUtil.dimenPx

val DEBUG_COLOR_PINK = Color.parseColor("#EAA3CF")
val DEBUG_COLOR_DARK_PINK = Color.parseColor("#E066B1")
val DEBUG_COLOR_BLUE = Color.parseColor("#B5FAFB")

/**
 * The root layout of a dialog. Contains a [DialogTitleLayout], [DialogContentLayout],
 * and [DialogActionButtonLayout].
 */
internal class DialogLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    var maxHeight = 0
    var maxWidth = 0

    internal val frameMarginVerticalLess = dimenPx(R.dimen.md_dialog_frame_margin_vertical_less)

    internal lateinit var dialog: MaterialDialog
    internal lateinit var titleLayout: DialogTitleLayout
    internal lateinit var contentLayout: DialogContentLayout
    internal lateinit var buttonsLayout: DialogActionButtonLayout

    override fun onFinishInflate() {
        super.onFinishInflate()
        titleLayout = findViewById(R.id.md_title_layout)
        contentLayout = findViewById(R.id.md_content_layout)
        buttonsLayout = findViewById(R.id.md_button_layout)
    }

    internal fun invalidateDividers(
        scrolledDown: Boolean,
        atBottom: Boolean
    ) {
        titleLayout.drawDivider = scrolledDown
        buttonsLayout.drawDivider = atBottom
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        var specWidth = getSize(widthMeasureSpec)
        var specHeight = getSize(heightMeasureSpec)
        if (specHeight > maxHeight) {
            specHeight = maxHeight
        }
        if (specWidth > maxWidth) {
            specWidth = maxWidth
        }

        titleLayout.measure(
            makeMeasureSpec(specWidth, EXACTLY),
            makeMeasureSpec(0, UNSPECIFIED)
        )
        if (buttonsLayout.shouldBeVisible()) {
            buttonsLayout.measure(
                makeMeasureSpec(specWidth, EXACTLY),
                makeMeasureSpec(0, UNSPECIFIED)
            )
        }

        val titleAndButtonsHeight =
            titleLayout.measuredHeight + buttonsLayout.measuredHeight
        val remainingHeight = specHeight - titleAndButtonsHeight
        contentLayout.measure(
            makeMeasureSpec(specWidth, EXACTLY),
            makeMeasureSpec(remainingHeight, AT_MOST)
        )

        val totalHeight = titleLayout.measuredHeight +
                contentLayout.measuredHeight +
                buttonsLayout.measuredHeight
        setMeasuredDimension(specWidth, totalHeight)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        val titleLeft = 0
        val titleTop = 0
        val titleRight = measuredWidth
        val titleBottom = titleLayout.measuredHeight
        titleLayout.layout(
            titleLeft,
            titleTop,
            titleRight,
            titleBottom
        )

        val buttonsTop =
            measuredHeight - buttonsLayout.measuredHeight
        if (buttonsLayout.shouldBeVisible()) {
            val buttonsLeft = 0
            val buttonsRight = measuredWidth
            val buttonsBottom = measuredHeight
            buttonsLayout.layout(
                buttonsLeft,
                buttonsTop,
                buttonsRight,
                buttonsBottom
            )
        }

        val contentLeft = 0
        val contentRight = measuredWidth
        contentLayout.layout(
            contentLeft,
            titleBottom,
            contentRight,
            buttonsTop
        )
    }
}
