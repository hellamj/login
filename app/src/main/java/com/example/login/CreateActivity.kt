package com.example.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CreateActivity : AppCompatActivity() {

    private var btGoogle: Button? = null
    private var btCrear: Button? = null
    private var etMail: EditText? = null
    private var etPass2: EditText? = null
    private var etPass: EditText? = null

    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth

    val TAG = "Holi"
    val RC_SIGN_IN = 647654345

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        initAccount()
        initGoogle()
        initViews()
    }

    private fun initViews() {

        etPass = findViewById<EditText>(R.id.etCrPass)
        etPass2 = findViewById<EditText>(R.id.etCrPass2)
        etMail = findViewById<EditText>(R.id.etCrMail)
        btCrear = findViewById<Button>(R.id.btCrear2)
        btGoogle = findViewById<Button>(R.id.btGoogle)


        btGoogle?.setOnClickListener {
            signIn()
        }

        btCrear?.setOnClickListener {
            createUser()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initAccount() {
        auth = Firebase.auth
    }

    private fun initGoogle() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        auth = Firebase.auth

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        /*if (currentUser != null) {
            recreate()
        }*/
        updateUI(currentUser)
    }

    private fun createUser() {
        var email = etMail?.text.toString()
        var password = etPass?.text.toString()
        var check = etPass2?.text.toString()

        if (password == check && password.length > 5) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
        } else {
            Toast.makeText(
                baseContext, "Password doesn't match or is not long enough.",
                Toast.LENGTH_SHORT
            ).show()

        }


    }

    private fun updateUI(user: FirebaseUser?) {

        Log.i(TAG, "email: " + user?.email.toString())

        Log.i(TAG, "displayName: " + user?.displayName.toString())


    }

}