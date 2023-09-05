package com.example.photos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.photos.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    }
