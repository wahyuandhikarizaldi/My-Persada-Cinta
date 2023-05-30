package com.example.mypersadacinta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import android.widget.ScrollView
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var getCode: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_dropdown -> {
                val anchorView = findViewById<View>(R.id.menu_item_dropdown)

                val popupMenu = PopupMenu(this, anchorView)
                popupMenu.menuInflater.inflate(R.menu.dropdown_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {

                        R.id.menu_item_2 -> {
                            startActivity(Intent(this, AboutActivity::class.java))
                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the toolbar as the action bar for the activity
        val toolbar = findViewById<Toolbar>(R.id.toolbar1)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        getCode = findViewById(R.id.idhere)
        saveButton = findViewById(R.id.submitBtn)

        // Get the root view of the activity
        val rootView = findViewById<View>(android.R.id.content)

        // Set an OnTouchListener on the root view
        rootView.setOnTouchListener { v, event ->
            // Check if the user tapped outside of the EditText
            if (event.action == MotionEvent.ACTION_DOWN &&
                currentFocus is EditText &&
                !v.equals(currentFocus)
            ) {
                // Hide the keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
            false
        }

        saveButton.setOnClickListener{
            val code = getCode.text.toString().trim()

            val docIdList = mutableListOf<String>()


            db.collection(code).get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        docIdList.add(document.id)
                    }
                    // Do something with the list of document IDs (docIdList)
                    showDocumentsInScrollView(docIdList)
                }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                }


        }




    }

    private fun showDocumentsInScrollView(docIdList: List<String>) {
        val code = getCode.text.toString().trim()
        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout_documents)
        linearLayout.removeAllViews() // Remove any existing TextViews

        for (docId in docIdList) {
            val productLayout = LinearLayout(this)
            productLayout.orientation = LinearLayout.HORIZONTAL
            productLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            productLayout.setPadding(20, 20, 20, 20)

            // Add product to layout
            val productDetailsLayout = LinearLayout(this)
            productDetailsLayout.orientation = LinearLayout.VERTICAL
            productDetailsLayout.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            ) // Set weight to 1

            productDetailsLayout.setPadding(20, 0, 0, 0)

            val textView = TextView(this)
            textView.text = docId
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            textView.setTextColor(Color.BLACK)
            productDetailsLayout.addView(textView)

            productLayout.addView(productDetailsLayout)

            // Add OnClickListener to product layout
            productLayout.setOnClickListener {
                val intent = Intent(this, DecryptDetailsActivity::class.java)
                intent.putExtra("docId", docId)
                intent.putExtra("code", code)
                startActivity(intent)
            }
            linearLayout.visibility = View.VISIBLE
            linearLayout.addView(productLayout)
        }
    }


}