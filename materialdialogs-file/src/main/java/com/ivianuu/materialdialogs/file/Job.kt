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

import android.os.Handler

internal typealias Execution<T> = (Job<T>) -> T
internal typealias PostExecution<T> = (T) -> Unit

// Can probably be replaced with coroutines
internal class Job<T>(private val execution: Execution<T>) {

  private var thread: Thread? = null
  private var after: ((T) -> Unit)? = null
  private var handler = Handler()

  var isAborted: Boolean = false
    private set

  fun after(after: PostExecution<T>): Job<T> {
    this.after = after
    return execute()
  }

  fun abort() {
    thread?.interrupt()
    thread = null
  }

  private fun execute(): Job<T> {
    thread = Thread(Runnable {
      val result = execution(this@Job)
      if (isAborted) return@Runnable
      handler.post { after?.invoke(result) }
    })
    thread?.start()
    return this
  }
}

internal fun <T> job(execution: Execution<T>): Job<T> {
  return Job(execution)
}
