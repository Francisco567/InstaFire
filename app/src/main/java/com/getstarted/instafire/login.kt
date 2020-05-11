package com.getstarted.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class login : AppCompatActivity() {
    //TODO autenticacion
    var auth:FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Instancia de FirebaseAuth
        auth= FirebaseAuth.getInstance()

        //Login Con Email y Contraseña
        btnLoginEmail.setOnClickListener {
            validar()
        }
    }

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

    //TODO cuenta con correo y contraseña
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


    //ir a la pagina de Inicio
    fun irPaginaPrincipal(user:FirebaseUser?){
        //Si el usuario es diferente de null
        if(user!=null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}
