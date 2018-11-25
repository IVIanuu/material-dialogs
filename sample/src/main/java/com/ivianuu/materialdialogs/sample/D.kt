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

package com.ivianuu.materialdialogs.sample

import android.util.Log
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.callback.onCancel
import com.ivianuu.materialdialogs.callback.onDismiss
import com.ivianuu.materialdialogs.callback.onShow

inline fun Any.d(m: () -> String) {
    Log.d(javaClass.simpleName, m())
}

fun MaterialDialog.debugListeners() = apply {
    onShow { d { "on show" } }
    onCancel { d { "on cancel" } }
    onDismiss { d { "on dismiss" } }
    positiveButton { d { "on positive" } }
    negativeButton { d { "on negative" } }
    neutralButton { d { "on neutrail" } }
}