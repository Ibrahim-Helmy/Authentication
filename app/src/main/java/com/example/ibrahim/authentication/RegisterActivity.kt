package com.example.ibrahim.authentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        mAuth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener {
            prograss_bar_register.visibility=View.VISIBLE
            val email = edit_register_Email.text.trim().toString()
            val password = edit_register_password.text.trim().toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
               registerNewUser(email,password)

            } else {
                prograss_bar_register.visibility = View.INVISIBLE

                Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun registerNewUser(email: String, password: String) {
        mAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {
                        prograss_bar_register.visibility=View.GONE
                        sendEmailVerfication()


                    } else {
                        prograss_bar_register.visibility=View.GONE

                        Toast.makeText(this@RegisterActivity, "${p0.exception!!.message}", Toast.LENGTH_LONG)
                            .show()

                    }
                }
            })

    }

    private fun sendEmailVerfication() {
        val user= mAuth!!.currentUser
        user!!.sendEmailVerification().addOnCompleteListener{
            if (it.isSuccessful){

                Toast.makeText(this@RegisterActivity, "success Go to your email to verify it", Toast.LENGTH_LONG).show()
                val i=Intent(this@RegisterActivity,LogInActivity::class.java)
                i.putExtra("email",user.email.toString())
                startActivity(i)
            }else{
                Toast.makeText(this@RegisterActivity, "please verify your account", Toast.LENGTH_LONG).show()

            }
        }

    }
}
