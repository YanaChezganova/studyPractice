package com.example.texteditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.texteditor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    val repository = Repository()
        binding.textView.text = repository.getText(this)

        binding.saveButton.setOnClickListener {
            repository.saveText(this, binding.editText.text.toString())
            binding.textView.text = repository.getText(this)
        }
        binding.clearButton.setOnClickListener {
            repository.clearText(this)
            binding.textView.text = repository.getText(this)
        }

    }
}