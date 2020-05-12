package com.getstarted.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class login : AppCompatActivity() {
    //TODO autenticacion
    var auth:FirebaseAuth?=null
    //TODO autenticacion con google
    var googleSignInClient:GoogleSignInClient?=null

    var RC_SIGN_IN=4443

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Instancia de FirebaseAuth
        auth= FirebaseAuth.getInstance()

        //Login Con Email y Contraseña
        btnLoginEmail.setOnClickListener {
            validar()
        }
        //Login con Google
        btnLoginGoogle.setOnClickListener {
            googleLogin()
        }

        //Configuracion de login con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)
    }

    //TODO Autenticacion Correo y Contraseña (Validaciones)

    //validar campos
    fun validar(){
        if(txtEmail.text.isEmpty() && txtPassword.text.isEmpty()){
            //Mostrar mensaje de error task.exception?.message
            Toast.makeText(this,"Llena los campos",Toast.LENGTH_SHORT).show()
        }else{
            loginAndRegister()
        }
    }

    //Validacion de Login con Email
    fun loginAndRegister(){
        auth?.createUserWithEmailAndPassword(txtEmail.text.toString().trim(),txtPassword.text.toString().trim())
            ?.addOnCompleteListener {
            task->
                if (task.isSuccessful){
                    //si se crea una cuenta de usuario
                    irPaginaPrincipal(task.result?.user)
                }else{
                    //ya tiene cuenta y solo se loguea
                    loginEmail()
                }
        }
    }

    //TODO login con correo y contraseña
    fun loginEmail(){
        auth?.createUserWithEmailAndPassword(txtEmail.text.toString().trim(),txtPassword.text.toString().trim())
            ?.addOnCompleteListener {
                task->
                if(task.isSuccessful){
                    //Login
                    irPaginaPrincipal(task.result?.user)
                }else{
                    //Mensaje de error
                    Toast.makeText(this,"La Webada del formato",Toast.LENGTH_SHORT).show()
                }
            }
    }


    //TODO Login con Google
    fun googleLogin() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==RC_SIGN_IN){
             var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                var cuenta=result.signInAccount
                //autenticamos
                autenticacionConGoogle(cuenta)
            }
        }
    }

    //Acciones al autenticar
    fun autenticacionConGoogle(cuenta: GoogleSignInAccount?){
        var credencial=GoogleAuthProvider.getCredential(cuenta?.idToken,null)
        auth?.signInWithCredential(credencial)
            ?.addOnCompleteListener {
                    task->
                if(task.isSuccessful){
                    //Login
                    irPaginaPrincipal(task.result?.user)
                }else{
                    //Mensaje de error
                    Toast.makeText(this, task.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
    }



    //TODO ir a la pagina de Principal
    fun irPaginaPrincipal(user:FirebaseUser?){
        //Si el usuario es diferente de null
        if(user!=null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

}
