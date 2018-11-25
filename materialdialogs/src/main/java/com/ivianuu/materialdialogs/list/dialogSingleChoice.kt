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

import com.ivianuu.materialdialogs.DialogButton.POSITIVE
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.action.setActionButtonEnabled
import com.ivianuu.materialdialogs.assertOneSet
import com.ivianuu.materialdialogs.internal.DialogAdapter
import com.ivianuu.materialdialogs.internal.SingleChoiceDialogAdapter
import com.ivianuu.materialdialogs.util.getStringArray

/**
 */
fun MaterialDialog.listItemsSingleChoice(
    res: Int? = null,
    items: List<String>? = null,
    disabledIndices: IntArray? = null,
    initialSelection: Int = -1,
    waitForPositiveButton: Boolean = true,
    selection: SingleChoiceListener = null
) = apply {

    val array = items ?: getStringArray(res)?.toList() ?: return this
    val adapter = getListAdapter()

    if (adapter is SingleChoiceDialogAdapter) {
        adapter.replaceItems(array)
        adapter.setListener(selection)
        if (disabledIndices != null) {
            adapter.disableItems(disabledIndices)
        }
        return this
    }

    assertOneSet("listItemsSingleChoice", items, res)
    setActionButtonEnabled(POSITIVE, initialSelection > -1)
    customListAdapter(
        SingleChoiceDialogAdapter(
            dialog = this,
            items = array,
            disabledItems = disabledIndices,
            initialSelection = initialSelection,
            waitForActionButton = waitForPositiveButton,
            selection = selection
        )
    )
}

/** Checks a single or multiple choice list item. */
fun MaterialDialog.checkItem(index: Int) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.checkItems(intArrayOf(index))
        return
    }
    throw UnsupportedOperationException(
        "Can't check item on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Unchecks a single or multiple choice list item. */
fun MaterialDialog.uncheckItem(index: Int) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.uncheckItems(intArrayOf(index))
        return
    }
    throw UnsupportedOperationException(
        "Can't uncheck item on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Checks or unchecks a single or multiple choice list item. */
fun MaterialDialog.toggleItemChecked(index: Int) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.toggleItems(intArrayOf(index))
        return
    }
    throw UnsupportedOperationException(
        "Can't toggle checked item on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Returns true if a single or multiple list item is checked. */
fun MaterialDialog.isItemChecked(index: Int): Boolean {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        return adapter.isItemChecked(index)
    }
    throw UnsupportedOperationException(
        "Can't check if item is checked on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}
