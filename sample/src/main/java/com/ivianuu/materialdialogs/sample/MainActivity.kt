package com.ivianuu.materialdialogs.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.color.colorChooser
import com.ivianuu.materialdialogs.list.listItems
import com.ivianuu.materialdialogs.setupWithActivity
import kotlinx.android.synthetic.main.activity_main.container
import kotlinx.android.synthetic.main.activity_main.show

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        show.setOnClickListener {
            /*supportFragmentManager.beginTransaction()
                .add(R.id.container, TestDialogFragment())
                .addToBackStack(null)
                .commit()
        */
           // list()
            //simple()
            color()
        }
    }

    private fun color() {
        MaterialDialog().showInContainer(container) {
            debugListeners()
            title(text = "Baum")
            colorChooser(allowCustomArgb = true, showAlphaSelector = true)
            positiveButton(text = "OK") {

            }
            negativeButton(text = "Cancel") {

            }
            neutralButton(text = "Neutral") {

            }
        }
    }


    private fun list() {
        MaterialDialog().showInContainer(container) {
            debugListeners()
            title(text = "Baum")
            listItems(items = (0..100).map { it.toString() }) { _, _, _ ->

            }
            positiveButton(text = "OK") {

            }
            negativeButton(text = "Cancel") {

            }
            neutralButton(text = "Neutral") {

            }
        }
    }

    private fun simple() {
        MaterialDialog(this).showInContainer(container) {
            setupWithActivity(this@MainActivity)
            title(text = "Title")
            message(text = "Message")
            positiveButton(text = "OK") {
                Log.d("testt", "on click")
            }
        }
    }

}
