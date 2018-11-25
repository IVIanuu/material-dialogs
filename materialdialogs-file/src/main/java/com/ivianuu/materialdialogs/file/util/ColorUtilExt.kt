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

package com.ivianuu.materialdialogs.file.util

import android.content.Context
import android.widget.TextView
import com.ivianuu.materialdialogs.util.MDUtil.resolveColor

internal fun TextView?.maybeSetTextColor(
  context: Context,
  attrRes: Int?
) {
  if (attrRes == null) return
  val color = resolveColor(context, attr = attrRes)
  if (color != 0) {
    this?.setTextColor(color)
  }
}
