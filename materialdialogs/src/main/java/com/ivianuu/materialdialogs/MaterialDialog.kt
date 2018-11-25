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

package com.ivianuu.materialdialogs

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.OneShotPreDrawListener
import com.ivianuu.materialdialogs.DialogButton.NEGATIVE
import com.ivianuu.materialdialogs.DialogButton.NEUTRAL
import com.ivianuu.materialdialogs.DialogButton.POSITIVE
import com.ivianuu.materialdialogs.action.getActionButton
import com.ivianuu.materialdialogs.callback.invokeAll
import com.ivianuu.materialdialogs.internal.DialogAdapter
import com.ivianuu.materialdialogs.internal.DialogContainer
import com.ivianuu.materialdialogs.list.getListAdapter
import com.ivianuu.materialdialogs.util.hideKeyboard
import com.ivianuu.materialdialogs.util.inflate
import com.ivianuu.materialdialogs.util.isVisible
import com.ivianuu.materialdialogs.util.populateIcon
import com.ivianuu.materialdialogs.util.populateText
import com.ivianuu.materialdialogs.util.preShow
import com.ivianuu.materialdialogs.util.setDefaults
import com.ivianuu.materialdialogs.util.setWindowConstraints

internal fun assertOneSet(
    method: String,
    b: Any?,
    a: Int?
) {
    if ((a == null || a == 0) && b == null) {
        throw IllegalArgumentException("$method: You must specify a resource ID or literal value")
    }
}

typealias DialogCallback = (MaterialDialog) -> Unit

class MaterialDialog internal constructor(
    val context: Context,
    unit: Unit // workaround
) {

    val onBackPressedCallback = OnBackPressedCallback {
        if (cancelable && isShowing) {
            cancel()
            true
        } else {
            false
        }
    }

    /**
     * A named config map, used like tags for extensions.
     *
     * Developers extending functionality of Material Dialogs should not use things
     * like static variables to store things. They instead should be stored at a dialog
     * instance level, which is what this provides.
     */
    val config: MutableMap<String, Any> = mutableMapOf()

    var autoDismissEnabled: Boolean = true
        internal set

    var titleFont: Typeface? = null
        internal set
    var bodyFont: Typeface? = null
        internal set
    var buttonFont: Typeface? = null
        internal set

    val isShowing get() = container.windowToken != null
    var cancelable = true
        internal set
    var cancelOnTouchOutside = true
        internal set

    var isDismissed = false
        private set

    var isCanceled = false
        private set

    var animationsEnabled = true
        private set

    var dimBackground = true
        private set(value) {
            field = value
            container.dialogDim.visibility =
                    if (value) View.VISIBLE else View.INVISIBLE
        }

    private var addedInContainer = false

    val view: View get() = container

    internal val container = inflate<DialogContainer>(R.layout.md_dialog_base)
    internal val dialogLayout = container.dialogLayout
    internal val titleLayout = dialogLayout.titleLayout
    internal val content = dialogLayout.contentLayout
    internal val buttonsLayout = dialogLayout.buttonsLayout

    internal val showListeners = mutableListOf<DialogCallback>()
    internal val dismissListeners = mutableListOf<DialogCallback>()
    internal val cancelListeners = mutableListOf<DialogCallback>()

    private val positiveListeners = mutableListOf<DialogCallback>()
    private val negativeListeners = mutableListOf<DialogCallback>()
    private val neutralListeners = mutableListOf<DialogCallback>()

    private var currentAnimator: Animator? = null

    init {
        container.dialog = this
        dialogLayout.dialog = this
        setWindowConstraints()
        setDefaults()

        container.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                preShow()
                showListeners.invokeAll(this@MaterialDialog)
                container.removeOnAttachStateChangeListener(this)
            }

            override fun onViewDetachedFromWindow(v: View?) {
                container.removeOnAttachStateChangeListener(this)
            }
        })
    }

    /**
     * Shows an drawable to the left of the dialog title.
     */
    fun icon(
        res: Int? = null,
        drawable: Drawable? = null
    ) = apply {
        assertOneSet("icon", drawable, res)
        populateIcon(
            dialogLayout.titleLayout.iconView,
            iconRes = res,
            icon = drawable
        )
    }

    /**
     * Shows a title, or header, at the top of the dialog.
     */
    fun title(
        res: Int? = null,
        text: String? = null
    ) = apply {
        assertOneSet("title", text, res)
        populateText(
            dialogLayout.titleLayout.titleView,
            textRes = res,
            text = text,
            typeface = this.titleFont,
            textColor = R.attr.md_color_title
        )
    }

    /**
     * Shows a message, below the title, and above the action buttons (and checkbox prompt).
     */
    fun message(
        res: Int? = null,
        text: CharSequence? = null,
        html: Boolean = false,
        lineHeightMultiplier: Float = 1f
    ) = apply {
        assertOneSet("message", text, res)
        dialogLayout.contentLayout.setMessage(
            dialog = this,
            res = res,
            text = text,
            html = html,
            lineHeightMultiplier = lineHeightMultiplier,
            typeface = this.bodyFont
        )
    }

    /**
     * Shows a positive action button, in the far right at the bottom of the dialog.
     */
    fun positiveButton(
        res: Int? = null,
        text: CharSequence? = null,
        click: DialogCallback? = null
    ): MaterialDialog {
        if (click != null) {
            positiveListeners.add(click)
        }

        val btn = getActionButton(POSITIVE)
        if (res == null && text == null && btn.isVisible()) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }

        populateText(
            btn,
            textRes = res,
            text = text,
            fallback = android.R.string.ok,
            typeface = this.buttonFont
        )
        return this
    }

    /** Clears any positive action button listeners set via usages of [positiveButton]. */
    fun clearPositiveListeners() = apply { positiveListeners.clear() }

    /**
     * Shows a negative action button, to the left of the positive action button (or at the far
     * right if there is no positive action button).
     */
    fun negativeButton(
        res: Int? = null,
        text: CharSequence? = null,
        click: DialogCallback? = null
    ): MaterialDialog {
        if (click != null) {
            negativeListeners.add(click)
        }

        val btn = getActionButton(NEGATIVE)
        if (res == null && text == null && btn.isVisible()) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }

        populateText(
            btn,
            textRes = res,
            text = text,
            fallback = android.R.string.cancel,
            typeface = this.buttonFont
        )
        return this
    }

    /** Clears any negative action button listeners set via usages of [negativeButton]. */
    fun clearNegativeListeners() = apply { negativeListeners.clear() }

    fun neutralButton(
        res: Int? = null,
        text: CharSequence? = null,
        click: DialogCallback? = null
    ): MaterialDialog {
        if (click != null) {
            neutralListeners.add(click)
        }

        val btn = getActionButton(NEUTRAL)
        if (res == null && text == null && btn.isVisible()) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }

        populateText(
            btn,
            textRes = res,
            text = text,
            typeface = this.buttonFont
        )
        return this
    }

    fun clearNeutralListeners() = apply { neutralListeners.clear() }

    /**
     * Turns off auto dismiss. Action button and list item clicks won't dismiss the dialog on their
     * own. You have to handle dismissing the dialog manually with the [dismiss] method.
     */
    fun autoDismiss(enabled: Boolean) = apply { autoDismissEnabled = enabled }

    /** A fluent version of [setCancelable]. */
    fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }

    /** A fluent version of [setCanceledOnTouchOutside]. */
    fun cancelOnTouchOutside(cancelable: Boolean) = apply { cancelOnTouchOutside = cancelable }

    fun animationsEnabled(enabled: Boolean) = apply { animationsEnabled = enabled }

    fun dimBackground(enabled: Boolean) = apply { dimBackground = enabled }

    fun showInContainer(viewGroup: ViewGroup) = apply {
        if (isDismissed) return@apply
        viewGroup.addView(container)
        addedInContainer = true

        if (animationsEnabled) {
            OneShotPreDrawListener.add(container) {
                if (isDismissed) return@add
                val animator = AnimatorSet()
                animator.play(
                    ObjectAnimator.ofFloat(container, View.ALPHA, 0f, 1f)
                        .setDuration(150)
                )

                currentAnimator = animator
                animator.start()
            }
        }
    }

    fun dismiss() {
        if (isDismissed) return
        isDismissed = true
        dismissListeners.invokeAll(this)
        (context as? ComponentActivity)?.removeOnBackPressedCallback(onBackPressedCallback)
        hideKeyboard()

        if (addedInContainer) {
            if (animationsEnabled) {
                currentAnimator?.cancel()
                val animator = AnimatorSet()
                animator.play(
                    ObjectAnimator.ofFloat(container, View.ALPHA, view.alpha, 0f)
                        .setDuration(150)
                )

                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator?) {
                        super.onAnimationCancel(animation)
                        (container.parent as? ViewGroup)?.removeView(container)
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        (container.parent as? ViewGroup)?.removeView(container)
                    }
                })

                animator.start()
            } else {
                (container.parent as? ViewGroup)?.removeView(container)
            }
        }
    }

    fun cancel() {
        if (isDismissed || isCanceled) return
        isCanceled = true
        cancelListeners.invokeAll(this)
        dismiss()
    }

    /** Applies multiple properties to the dialog and opens it. */
    inline fun showInContainer(viewGroup: ViewGroup, func: MaterialDialog.() -> Unit) = apply {
        func()
        showInContainer(viewGroup)
    }

    internal fun onActionButtonClicked(which: DialogButton) {
        when (which) {
            POSITIVE -> {
                positiveListeners.invokeAll(this)
                val adapter = getListAdapter() as? DialogAdapter<*, *>
                adapter?.positiveButtonClicked()
            }
            NEGATIVE -> negativeListeners.invokeAll(this)
            NEUTRAL -> neutralListeners.invokeAll(this)
        }
        if (autoDismissEnabled) {
            dismiss()
        }
    }

}

fun MaterialDialog(context: Context) = MaterialDialog(context, Unit)
