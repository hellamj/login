package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    private var guardar: Button? = null
    private var prof: TextView? = null
    private var age: EditText? = null
    private var surname: EditText? = null
    private var name: EditText? = null
    var userActual: String? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initGoogle()
        initViews()



    }

    private fun initViews() {
        name = findViewById<EditText>(R.id.etname)
        surname = findViewById<EditText>(R.id.etsurname)
        age = findViewById<EditText>(R.id.etedad)
        prof = findViewById<TextView>(R.id.etprof)
        guardar = findViewById<Button>(R.id.btguardar)

        var user = Firebase.auth.currentUser
        leerDatosUser()

        if(user != null){
            userActual = user.uid
        }


        guardar?.setOnClickListener {
          // var id = userActual
            if (userActual != null) {
                reescribirUsuario(userActual!!)
            }
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)



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

                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error", exception)

                }
        }
    }
    private fun reescribirUsuario(usuarioId: String) {
        val newname: String
        if (!name?.text.isNullOrEmpty()){
            newname = name?.text.toString()
        }else{
            newname = "Dato no facilitado"
        }

        val newsurname: String
        if (!surname?.text.isNullOrEmpty()){
            newsurname = surname?.text.toString()
        }else{
            newsurname = ""
        }
        val newage: Int?
        if (!age?.text.isNullOrEmpty()){
            newage = age?.text.toString().toIntOrNull()
        }else{
            newage = 0
        }

        val newprof: String
        if (!prof?.text.isNullOrEmpty()){
            newprof = prof?.text.toString()
        }else{
            newprof = "Dato no facilitado"
        }


      /* val city = hashMapOf(

            newname to name?.text.toString(),
            newsurname to surname?.text.toString() ,
            newage to age?.text.toString().toInt(),
            newprof to prof?.text.toString()
        )*/

        val city: Usuario = Usuario()
        city.nombre = newname
        city.apellidos = newsurname
        city.edad = newage ?: 0
        city.profesion = newprof




        val db = Firebase.firestore
        db.collection("users").document("$usuarioId")
            .set(city)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }




    }
}