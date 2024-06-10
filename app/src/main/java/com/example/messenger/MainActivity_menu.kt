package com.example.messenger

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.graphics.PorterDuff

class MainActivity_menu : AppCompatActivity() {


    lateinit var GoButton: Button
    lateinit var NameEditText: EditText


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        GoButton = findViewById(R.id.ConnectButton)

        NameEditText = findViewById(R.id.NameEditText)



        GoButton.setOnClickListener {
            var Name = NameEditText.text.toString()

            if (Name != null && Name != ""){
                var intent = Intent(this,MainActivity::class.java)

                intent.putExtra("Name",Name)

                startActivity(intent)
            } else{

                var text = "Нельзя без имени"
                var dur = Toast.LENGTH_SHORT

                var toast = Toast.makeText(applicationContext,text,dur)

                toast.show()


            }



        }



    }
}