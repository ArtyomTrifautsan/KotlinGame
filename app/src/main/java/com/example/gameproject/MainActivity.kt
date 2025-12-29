package com.example.gameproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gameproject.fragments.MenuFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_container)

        supportActionBar?.hide()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MenuFragment())
                .commit()
        }
    }
}