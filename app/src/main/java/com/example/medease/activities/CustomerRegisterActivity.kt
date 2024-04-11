package com.example.medease.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.medease.Constants
import com.example.medease.models.CustomerRegisterModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ecom.example.medease.databinding.ActivityCustomerRegisterBinding

class CustomerRegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityCustomerRegisterBinding
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCustomerRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mAuht = Firebase.auth

      binding.tvLogin.setOnClickListener {
          startActivity(Intent(this, CustomerLoginActivity::class.java))
          finish()
      }
        binding.btnsave.setOnClickListener {
            if (binding.edtitems.text.toString().isNullOrEmpty()) {
                binding.tilitemName.isErrorEnabled = true
                binding.tilitemName.error = "Enter Name"
            }
            else if (binding.edtemail.text.toString().isNullOrEmpty()) {
                binding.tilemail.isErrorEnabled = true
                binding.tilemail.error = "Enter Qualification"
            }
            else if (binding.edtPassword.text.toString().isNullOrEmpty()) {
                binding.tilPassword.isErrorEnabled = true
                binding.tilPassword.error = "Enter Experience"
            }else{
            mAuht.createUserWithEmailAndPassword( binding.edtemail.text.toString(), binding.edtPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        val user = mAuht.currentUser
                        val registerModel = CustomerRegisterModel()
                        registerModel.username = binding.edtitems.text.toString()
                        registerModel.useremail = binding.edtemail.text.toString()
                        registerModel.userauthId = user?.uid
                        startActivity(Intent(this, CustomerLoginActivity::class.java))
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        finish()
                        // Save user details to Firestore database
                        db.collection(Constants.customers).add(registerModel)
                            .addOnCompleteListener { registrationTask ->
                                if (registrationTask.isSuccessful) {
                                    // Registration and data save successful
                                } else {
                                    Toast.makeText(this, "Registration error", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Registration failed
                        // Handle error appropriately
                    }
                }
        }
        }
    }
}