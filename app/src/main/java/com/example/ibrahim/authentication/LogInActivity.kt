package com.example.ibrahim.authentication

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    //signby google
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    val REQUEST_CODE_GOOGLE = 100

    //fb
    lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        if (intent.hasExtra("email")) {
            edit_login_Email.setText(intent.extras.getString("email"))
        }
        mAuth = FirebaseAuth.getInstance()
        //fb
        callbackManager = CallbackManager.Factory.create()


        // google signIn
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        btn_signinByGoogle.setOnClickListener { view: View? ->
            signInGoogle()
            prograss_bar_login.visibility = View.VISIBLE
        }

        //fb

        btn_signInByFacebook.setReadPermissions("email")
        btn_signInByFacebook.setOnClickListener {
            signInByFB()
        }


        btn_login.setOnClickListener {
            prograss_bar_login.visibility = View.VISIBLE

            val email = edit_login_Email.text.toString()
            val password = edit_login_password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginByEmailandPass(email, password)

            } else {
                prograss_bar_login.visibility = View.INVISIBLE

                Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
            }

        }
        btn_signUp.setOnClickListener {
            val intentToregister: Intent = Intent(this, RegisterActivity::class.java)
            startActivity(intentToregister)
        }


    }

    private fun signInByFB() {
        btn_signInByFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {

                handelFbAcessToken(result!!.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@LogInActivity, "canceld", Toast.LENGTH_LONG).show()

            }

            override fun onError(error: FacebookException?) {
            }
        })
    }

    private fun handelFbAcessToken(accessToken: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        mAuth!!.signInWithCredential(credential)

            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {
                        prograss_bar_login.visibility = View.INVISIBLE
                        val intentToMain = Intent(this@LogInActivity, MainActivity::class.java)
                        Toast.makeText(this@LogInActivity, "تم الدخول بحساب فيس☺", Toast.LENGTH_LONG).show()
                        startActivity(intentToMain)
                    }else{
                        Toast.makeText(this@LogInActivity, p0.exception!!.message, Toast.LENGTH_LONG).show()

                    }
                }
            })

    }


    private fun loginByEmailandPass(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {

                    if (p0.isSuccessful) {
                        prograss_bar_login.visibility = View.INVISIBLE
                        isEmailVerify()

                    } else {
                        prograss_bar_login.visibility = View.INVISIBLE

                        Toast.makeText(this@LogInActivity, "${p0.exception!!.message}", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun isEmailVerify() {
        val user = mAuth!!.currentUser
        if (user!!.isEmailVerified) {
            startActivity(Intent(this@LogInActivity, MainActivity::class.java))
        } else {
            Toast.makeText(this@LogInActivity, "check your email", Toast.LENGTH_LONG).show()

        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)


        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GOOGLE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            handelResult(task)

        } else {

            Toast.makeText(this, "else in activity result", Toast.LENGTH_LONG).show()

        }


    }

    private fun handelResult(compliteTask: Task<GoogleSignInAccount>) {

        try {
            val account = compliteTask.getResult(ApiException::class.java)

            val token = account.idToken
            val credential = GoogleAuthProvider.getCredential(token, null)

            firebaseAuth(credential)
        } catch (e: ApiException) {
            Toast.makeText(this, "${e.message}", Toast.LENGTH_LONG).show()

        }
    }

    private fun firebaseAuth(credential: AuthCredential?) {

        mAuth!!.signInWithCredential(credential!!)
            .addOnSuccessListener { AuthResult ->
                prograss_bar_login.visibility = View.INVISIBLE
                val intentToMain = Intent(this, MainActivity::class.java)
                intentToMain.putExtra("name", AuthResult.user.email.toString())
                Toast.makeText(this, "تم الدخول بحساب جوجل☺", Toast.LENGTH_LONG).show()

                startActivity(intentToMain)
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
    }


}
