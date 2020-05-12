package com.getstarted.instafire

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class login : AppCompatActivity() {
    //TODO autenticacion
    var auth:FirebaseAuth?=null
    //TODO autenticacion con google
    var googleSignInClient:GoogleSignInClient?=null

    var RC_SIGN_IN=4443

    var callbackManager:CallbackManager?=null

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
        //Login con Facebook
        btnLoginFacebook.setOnClickListener {
            facebookLogin()
        }

        //Configuracion de login con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        callbackManager= CallbackManager.Factory.create()
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

        //Requerido para login con facebook
        callbackManager?.onActivityResult(requestCode,resultCode,data)
        //Para login con google
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

    //TODO login con Facebook
    fun facebookLogin(){
        LoginManager.getInstance()
            .logInWithReadPermissions(this,Arrays.asList("public_profile","email"))
        LoginManager.getInstance().registerCallback(callbackManager,object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                //Si funciona
                permitirAccesoAlToken(result?.accessToken)
            }

            override fun onCancel() {
                //nada
            }

            override fun onError(error: FacebookException?) {
                //nada
            }

        })
    }

    fun permitirAccesoAlToken(token: AccessToken?){
        var credencialFacebook=FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credencialFacebook)
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
