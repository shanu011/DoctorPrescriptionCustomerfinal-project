package com.example.medease.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.medease.models.CustomerRegisterModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import ecom.example.medease.R

class MainActivity : AppCompatActivity() {
    var db = Firebase.firestore
    lateinit var navController : NavController
    lateinit var mainmenu: Unit
    var auth = Firebase.auth
    private lateinit var mAuth: FirebaseAuth
    var userModel = CustomerRegisterModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        navController = findNavController(R.id.fragment)

//        db.collection("UserProfile").document(auth.currentUser?.uid.toString()).get().addOnCompleteListener {document->
//            if(document.isSuccessful){
//                userModel = document.result.toObject(CustomerRegisterModel::class.java) as CustomerRegisterModel
//                println("UserModerl: $userModel")
//            }
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        mainmenu=menuInflater.inflate(R.menu.menu_main, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.myRequests ->{
                navController.navigate(R.id.myRequestsFragment)
                return true
            }
            R.id.logout->{
                logout()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun logout() {
        mAuth.signOut()
        // Redirect to LoginActivity
        startActivity(Intent(this, CustomerLoginActivity::class.java))
        finish() // Close MainActivity
        // Clear any saved user authentication state


    }
}

