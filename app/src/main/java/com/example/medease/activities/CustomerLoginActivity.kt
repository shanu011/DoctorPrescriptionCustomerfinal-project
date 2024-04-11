package com.example.medease.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ecom.example.medease.databinding.ActivityCustomerLoginBinding

class CustomerLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityCustomerLoginBinding
    var mAuth = Firebase.auth
    var progressBar: ProgressBar?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCustomerLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleSmall)
        progressBar=binding.pbar
        binding.tvregister.setOnClickListener { 
            startActivity(Intent(this,CustomerRegisterActivity::class.java))
        }

        binding.btnsave.setOnClickListener {
            if (binding.edtemail.text.isNullOrEmpty()) {
                binding.tilemail.isErrorEnabled = true
                binding.tilemail.error = "Enter Email"
            } else if (binding.edtPassword.text.isNullOrEmpty()) {
                binding.tilPassword.isErrorEnabled = true
                binding.tilPassword.error = "Enter password"
            } else {
                binding.tilPassword.isErrorEnabled = false
                binding.tilemail.isErrorEnabled = false
                binding.pbar.visibility=View.VISIBLE
                mAuth.signInWithEmailAndPassword(
                    binding.edtemail.text.toString(),
                    binding.edtPassword.text.toString()
                ).addOnCompleteListener { loginTask ->

                    if (loginTask.isSuccessful) {
                        // Login successful
//                binding.llProgressBar.visibility = View.GONE
                        binding.pbar.visibility=View.GONE
                        Snackbar.make(
                            binding.edtemail,
                            "Login Successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        this.finish()
                        println("Login Successfully")
                    } else {
                        // Login failed
                        binding.pbar.visibility=View.GONE
                        Snackbar.make(
                            binding.edtemail,
                            "Login Error",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        println("Login Error: ${loginTask.exception}")
                    }
                }
            }
        }

    }
}