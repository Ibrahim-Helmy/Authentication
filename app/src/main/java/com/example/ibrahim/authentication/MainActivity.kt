package com.example.ibrahim.authentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
//
//        val token = AccessToken.getCurrentAccessToken()
//        if (token != null || !token!!.isExpired) {
//
//
//        }
//
            if (mAuth?.currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))

        } else {
            ////to verify
            if (mAuth!!.currentUser!!.isEmailVerified!!) {
                val name: String = mAuth!!.currentUser!!.email.toString()
                txt_main.setText(name)
            } else {
                startActivity(Intent(this, LogInActivity::class.java))
                Toast.makeText(this, "go to your email and verify it", Toast.LENGTH_LONG).show()
            }

        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var id = item!!.itemId
        if (id == R.id.item_logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            Toast.makeText(this, "signOut", Toast.LENGTH_LONG).show()
        }

        return true
    }
}
