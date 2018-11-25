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
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.R
import com.ivianuu.materialdialogs.assertOneSet

internal inline fun <reified T> List<T>.pullIndices(indices: IntArray): List<T> {
    val result = mutableListOf<T>()
    for (index in indices) {
        result.add(this[index])
    }
    return result
}

internal fun MaterialDialog.resolveColor(
    res: Int? = null,
    attr: Int? = null
): Int = MDUtil.resolveColor(context, res, attr)

internal fun TextView?.maybeSetTextColor(
    context: Context,
    attrRes: Int?
) {
    if (attrRes == null) return
    val color = MDUtil.resolveColor(context, attr = attrRes)
    if (color != 0) {
        this?.setTextColor(color)
    }
}

internal fun MaterialDialog.dimen(
    res: Int? = null,
    attr: Int? = null,
    fallback: Float = context.resources.getDimension(R.dimen.md_dialog_default_corner_radius)
): Float {
    assertOneSet("dimen", attr, res)
    if (res != null) {
        return context.resources.getDimension(res)
    }
    requireNotNull(attr)
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getDimension(0, fallback)
    } finally {
        a.recycle()
    }
}

internal fun MaterialDialog.font(
    res: Int? = null,
    attr: Int? = null
): Typeface? {
    assertOneSet("font", attr, res)
    if (res != null) {
        return ResourcesCompat.getFont(context, res)
    }
    requireNotNull(attr)
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        val resId = a.getResourceId(0, 0)
        if (resId == 0) return null
        return ResourcesCompat.getFont(context, resId)
    } finally {
        a.recycle()
    }
}

internal fun IntArray.appendAll(values: Collection<Int>): IntArray {
    val mutable = this.toMutableList()
    mutable.addAll(values)
    return mutable.toIntArray()
}

internal fun IntArray.removeAll(values: Collection<Int>): IntArray {
    val mutable = this.toMutableList()
    mutable.removeAll { values.contains(it) }
    return mutable.toIntArray()
}

internal fun MaterialDialog.getStringArray(res: Int?): Array<String>? {
    if (res == null) return emptyArray()
    return context.resources.getStringArray(res)
}

@Suppress("UNCHECKED_CAST")
internal fun <R : View> ViewGroup.inflate(
    ctxt: Context = context,
    res: Int
) = LayoutInflater.from(ctxt).inflate(res, this, false) as R

@Suppress("UNCHECKED_CAST")
internal fun <T> MaterialDialog.inflate(
    res: Int,
    root: ViewGroup? = null
) = LayoutInflater.from(context).inflate(res, root, false) as T

@Suppress("UNCHECKED_CAST")
internal fun <T> ViewGroup.inflate(
    res: Int,
    root: ViewGroup? = this
) = LayoutInflater.from(context).inflate(res, root, false) as T

internal fun <T : View> T?.updatePadding(
    left: Int = this?.paddingLeft ?: 0,
    top: Int = this?.paddingTop ?: 0,
    right: Int = this?.paddingRight ?: 0,
    bottom: Int = this?.paddingBottom ?: 0
) {
    if (this != null &&
        left == this.paddingLeft &&
        top == this.paddingTop &&
        right == this.paddingRight &&
        bottom == this.paddingBottom
    ) {
        // no change needed, don't want to invalidate layout
        return
    }
    this?.setPadding(left, top, right, bottom)
}

internal fun <T : View> T.topMargin() = (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin

internal fun <T : View> T.updateMargin(
    left: Int = -1,
    top: Int = -1,
    right: Int = -1,
    bottom: Int = -1
) {
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    if (left != -1) {
        layoutParams.leftMargin = left
    }
    if (top != -1) {
        layoutParams.topMargin = top
    }
    if (right != -1) {
        layoutParams.rightMargin = right
    }
    if (bottom != -1) {
        layoutParams.bottomMargin = bottom
    }
    this.layoutParams = layoutParams
}

internal inline fun <T : View> T.waitForLayout(crossinline f: T.() -> Unit) =
    viewTreeObserver.apply {
        addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                removeOnGlobalLayoutListener(this)
                this@waitForLayout.f()
            }
        })
    }!!

internal fun <T : View> T.isVisible(): Boolean {
    return if (this is Button) {
        this.visibility == View.VISIBLE && this.text.trim().isNotBlank()
    } else {
        this.visibility == View.VISIBLE
    }
}

internal fun <T : View> T.isNotVisible(): Boolean {
    return !isVisible()
}

internal fun <T : View> T.isRtl(): Boolean {
    return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

internal fun TextView.setGravityStartCompat() {
    this.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
}

internal fun TextView.setGravityEndCompat() {
    this.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
}