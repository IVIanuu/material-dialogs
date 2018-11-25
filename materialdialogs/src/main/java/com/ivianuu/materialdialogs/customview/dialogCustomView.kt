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

package com.ivianuu.materialdialogs.customview

import android.view.View
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.assertOneSet

internal const val CUSTOM_VIEW_NO_PADDING = "md.custom_view_no_padding"

/** Gets a custom view set by [customView]. */
fun MaterialDialog.getCustomView(): View? = content.customView

/**
 * Sets a custom view to display in the dialog, below the title and above the action buttons
 * (and checkbox prompt).
 */
fun MaterialDialog.customView(
    viewRes: Int? = null,
    view: View? = null,
    scrollable: Boolean = false,
    noVerticalPadding: Boolean = false
) = apply {
    assertOneSet("customView", view, viewRes)
    config[CUSTOM_VIEW_NO_PADDING] = noVerticalPadding
    content.addCustomView(
        res = viewRes,
        view = view,
        scrollable = scrollable
    )
}