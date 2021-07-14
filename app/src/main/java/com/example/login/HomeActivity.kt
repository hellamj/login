package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private var tvUserData: TextView? = null
    private var tvName: TextView? = null
    private var tvUser: TextView? = null
    private var btLogout: Button? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    val TAG = "HomeActivity"
    val RC_SIGN_IN = 647654345

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initGoogle()
        initViews()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.perfil -> {
                newUser()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun newUser() {
        Toast.makeText(this, "Boton pulsado!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)

    }

    private fun initViews() {
        tvUser = findViewById<TextView>(R.id.tvHomeUser)
        tvName = findViewById<TextView>(R.id.tvname)
        btLogout = findViewById<Button>(R.id.btLogout)

        var user = Firebase.auth.currentUser

        user?.let { user->
            if (user.displayName?.isEmpty() == true || user.displayName == null) {
                tvUser?.text = "Estás dentro ${user.email}"
            } else {

                tvUser?.text = "¡${user.displayName} estás dentro!"
            }
        }
        leerDatosUser()
        btLogout?.setOnClickListener {
            logout()
        }


    }


    private fun initGoogle() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun leerDatosUser() {
        var user = Firebase.auth.currentUser
        val db = Firebase.firestore
        Log.i(TAG, user?.uid.toString())
        user?.uid?.let {
            db.collection("users").document(it)
                .get()
                .addOnSuccessListener { result ->

                        Log.i(TAG, "${result.id}-> ${result.data}")

                    val usuario = result.toObject(Usuario::class.java)
                    rellenarDatosUsuario(usuario)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error", exception)

                }
        }
    }

    private fun rellenarDatosUsuario(usuario: Usuario?) {
        tvName = findViewById<TextView>(R.id.tvname)
        tvUserData = findViewById<TextView>(R.id.tvUserData)
        tvName?.setText("Nombre ${usuario?.nombre ?: "---"} ${usuario?.apellidos ?: ""}")
        tvUserData?.setText("Edad ${usuario?.edad ?: "---"} \nProfesion: ${usuario?.profesion ?: "---"}")


    }

    private fun logout() {
        googleSignInClient.signOut()?.addOnCompleteListener {
            googleSignInClient.revokeAccess()?.addOnCompleteListener {
                Firebase.auth.signOut()
                finish()
                //Cuidado con la linea del intent, tiene que estar en el mismo sitio que el finish si no se vuelve sincrono
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }


    }
}