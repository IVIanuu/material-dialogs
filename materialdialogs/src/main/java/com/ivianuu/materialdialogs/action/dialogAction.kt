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

package com.ivianuu.materialdialogs.action

import androidx.appcompat.widget.AppCompatButton
import com.ivianuu.materialdialogs.DialogButton
import com.ivianuu.materialdialogs.MaterialDialog

/** Returns true if the dialog has visible action buttons. */
fun MaterialDialog.hasActionButtons() = buttonsLayout.visibleButtons.isNotEmpty()

/** Returns the underlying view for an action button in the dialog. */
fun MaterialDialog.getActionButton(which: DialogButton) =
    buttonsLayout.actionButtons[which.index] as AppCompatButton

/** Enables or disables an action button. */
fun MaterialDialog.setActionButtonEnabled(
    which: DialogButton,
    enabled: Boolean
) {
    getActionButton(which).isEnabled = enabled
}
