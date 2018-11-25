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

package com.ivianuu.materialdialogs.file

import android.os.Environment.getExternalStorageDirectory
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivianuu.materialdialogs.DialogButton.POSITIVE
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.action.setActionButtonEnabled
import com.ivianuu.materialdialogs.customview.customView
import com.ivianuu.materialdialogs.customview.getCustomView
import com.ivianuu.materialdialogs.file.util.hasReadStoragePermission
import com.ivianuu.materialdialogs.file.util.hasWriteStoragePermission
import com.ivianuu.materialdialogs.file.util.maybeSetTextColor
import com.ivianuu.materialdialogs.internal.DialogRecyclerView
import java.io.File

/**
 * Shows a dialog that lets the user select a local folder.
 *
 */
fun MaterialDialog.folderChooser(
  initialDirectory: File = getExternalStorageDirectory(),
  filter: FileFilter = { !it.isHidden },
  waitForPositiveButton: Boolean = true,
  emptyTextRes: Int = R.string.files_default_empty_text,
  allowFolderCreation: Boolean = false,
  folderCreationLabel: Int? = null,
  selection: FileCallback = null
): MaterialDialog {
  if (allowFolderCreation) {
    check(hasWriteStoragePermission()) {
      "You must have the WRITE_EXTERNAL_STORAGE permission first."
    }
  }
  check(hasReadStoragePermission()) {
    "You must have the READ_EXTERNAL_STORAGE permission first."
  }

  customView(R.layout.md_file_chooser_base)
  setActionButtonEnabled(POSITIVE, false)

  val customView = getCustomView() ?: return this
  val list: DialogRecyclerView = customView.findViewById(R.id.list)
  val emptyText: TextView = customView.findViewById(R.id.empty_text)
  emptyText.setText(emptyTextRes)
  emptyText.maybeSetTextColor(context, R.attr.md_color_content)

  list.attach(this)
  list.layoutManager = LinearLayoutManager(context)
  val adapter = FileChooserAdapter(
      dialog = this,
      initialFolder = initialDirectory,
      waitForPositiveButton = waitForPositiveButton,
      emptyView = emptyText,
      onlyFolders = true,
      filter = filter,
      allowFolderCreation = allowFolderCreation,
      folderCreationLabel = folderCreationLabel,
      callback = selection
  )
  list.adapter = adapter

  if (waitForPositiveButton && selection != null) {
    positiveButton {
      val selectedFile = adapter.selectedFile
      if (selectedFile != null) {
        selection.invoke(this, selectedFile)
      }
    }
  }

  return this
}
