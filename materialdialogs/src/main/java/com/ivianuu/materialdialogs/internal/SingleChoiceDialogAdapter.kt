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

import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.materialdialogs.DialogButton.POSITIVE
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.R
import com.ivianuu.materialdialogs.action.hasActionButtons
import com.ivianuu.materialdialogs.action.setActionButtonEnabled
import com.ivianuu.materialdialogs.list.SingleChoiceListener
import com.ivianuu.materialdialogs.list.getItemSelector
import com.ivianuu.materialdialogs.util.inflate
import com.ivianuu.materialdialogs.util.maybeSetTextColor

internal class SingleChoiceViewHolder(
    itemView: View,
    private val adapter: SingleChoiceDialogAdapter
) : RecyclerView.ViewHolder(itemView), OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    val controlView: AppCompatRadioButton = itemView.findViewById(R.id.md_control)
    val titleView: TextView = itemView.findViewById(R.id.md_title)

    var isEnabled: Boolean
        get() = itemView.isEnabled
        set(value) {
            itemView.isEnabled = value
            controlView.isEnabled = value
            titleView.isEnabled = value
        }

    override fun onClick(view: View) = adapter.itemClicked(adapterPosition)
}

internal class SingleChoiceDialogAdapter(
    private var dialog: MaterialDialog,
    internal var items: List<String>,
    disabledItems: IntArray?,
    initialSelection: Int,
    private val waitForActionButton: Boolean,
    internal var selection: SingleChoiceListener
) : RecyclerView.Adapter<SingleChoiceViewHolder>(),
    DialogAdapter<String, SingleChoiceListener> {

    private var currentSelection: Int = initialSelection
        set(value) {
            val previousSelection = field
            field = value
            notifyItemChanged(previousSelection)
            notifyItemChanged(value)
        }
    private var disabledIndices: IntArray = disabledItems ?: IntArray(0)

    internal fun itemClicked(index: Int) {
        this.currentSelection = index
        if (waitForActionButton && dialog.hasActionButtons()) {
            // Wait for action button, don't call listener
            // so that positive action button press can do so later.
            dialog.setActionButtonEnabled(POSITIVE, true)
        } else {
            // Don't wait for action button, call listener and dismiss if auto dismiss is applicable
            this.selection?.invoke(dialog, index, this.items[index])
            if (dialog.autoDismissEnabled) {
                dialog.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SingleChoiceViewHolder {
        val listItemView: View =
            parent.inflate(dialog.context, R.layout.md_listitem_singlechoice)
        val viewHolder = SingleChoiceViewHolder(
            itemView = listItemView,
            adapter = this
        )
        viewHolder.titleView.maybeSetTextColor(dialog.context, R.attr.md_color_content)
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(
        holder: SingleChoiceViewHolder,
        position: Int
    ) {
        holder.isEnabled = !disabledIndices.contains(position)

        holder.controlView.isChecked = currentSelection == position
        holder.titleView.text = items[position]
        holder.itemView.background = dialog.getItemSelector()

        if (dialog.bodyFont != null) {
            holder.titleView.typeface = dialog.bodyFont
        }
    }

    override fun positiveButtonClicked() {
        if (currentSelection > -1) {
            selection?.invoke(dialog, currentSelection, items[currentSelection])
        }
    }

    override fun setListener(listener: SingleChoiceListener) {
        this.selection = listener
    }

    override fun replaceItems(items: List<String>) {
        this.items = items
        this.notifyDataSetChanged()
    }

    override fun disableItems(indices: IntArray) {
        this.disabledIndices = indices
        notifyDataSetChanged()
    }

    override fun checkItems(indices: IntArray) {
        val targetIndex = if (indices.isNotEmpty()) indices[0] else -1
        if (this.disabledIndices.contains(targetIndex)) return
        this.currentSelection = targetIndex
    }

    override fun uncheckItems(indices: IntArray) {
        val targetIndex = if (indices.isNotEmpty()) indices[0] else -1
        if (this.disabledIndices.contains(targetIndex)) return
        this.currentSelection = -1
    }

    override fun toggleItems(indices: IntArray) {
        val targetIndex = if (indices.isNotEmpty()) indices[0] else -1
        if (this.disabledIndices.contains(targetIndex)) return
        if (indices.isEmpty() || this.currentSelection == targetIndex) {
            this.currentSelection = -1
        } else {
            this.currentSelection = targetIndex
        }
    }

    override fun checkAllItems() = Unit

    override fun uncheckAllItems() = Unit

    override fun toggleAllChecked() = Unit

    override fun isItemChecked(index: Int) = this.currentSelection == index
}
