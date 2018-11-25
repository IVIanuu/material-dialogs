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

package com.ivianuu.materialdialogs.list

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.assertOneSet
import com.ivianuu.materialdialogs.internal.PlainListDialogAdapter
import com.ivianuu.materialdialogs.util.MDUtil.resolveDrawable
import com.ivianuu.materialdialogs.util.getStringArray

/** Gets the RecyclerView for a list dialog, if there is one. */
fun MaterialDialog.getRecyclerView(): RecyclerView? =
    content.recyclerView

/** A shortcut to [RecyclerView.getAdapter] on [getRecyclerView]. */
fun MaterialDialog.getListAdapter(): RecyclerView.Adapter<*>? =
    getRecyclerView()?.adapter

/**
 * Sets a custom list adapter to render custom list content.
 *
 * Cannot be used in combination with message, input, and some other types of dialogs.
 */
fun MaterialDialog.customListAdapter(
    adapter: RecyclerView.Adapter<*>,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
) = apply {
    content.addRecyclerView(
        dialog = this,
        adapter = adapter,
        layoutManager = layoutManager
    )
}

/**
 */
fun MaterialDialog.listItems(
    res: Int? = null,
    items: List<String>? = null,
    disabledIndices: IntArray? = null,
    waitForPositiveButton: Boolean = true,
    selection: ItemListener = null
): MaterialDialog {
    assertOneSet("listItems", items, res)
    val array = items ?: getStringArray(res)?.toList() ?: return this
    val adapter = getListAdapter()

    if (adapter is PlainListDialogAdapter) {
        adapter.replaceItems(array)
        adapter.setListener(selection)
        if (disabledIndices != null) {
            adapter.disableItems(disabledIndices)
        }
        return this
    }

    return customListAdapter(
        PlainListDialogAdapter(
            dialog = this,
            items = array,
            disabledItems = disabledIndices,
            waitForActionButton = waitForPositiveButton,
            selection = selection
        )
    )
}

internal fun MaterialDialog.getItemSelector() =
    resolveDrawable(context = context, attr = android.R.attr.selectableItemBackground) // todo
