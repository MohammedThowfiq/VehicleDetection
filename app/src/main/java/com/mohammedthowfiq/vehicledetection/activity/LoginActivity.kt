package com.mohammedthowfiq.vehicledetection.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.mohammedthowfiq.vehicledetection.R
import com.mohammedthowfiq.vehicledetection.util.ConnectionManager

class LoginActivity : AppCompatActivity() {

    lateinit var userEmailAddress:TextInputEditText
    lateinit var userPassword:TextInputEditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var loginProgressBar: ProgressBar

    lateinit var loginButton: Button
    lateinit var signUpButton: Button
    lateinit var forgetPasswordButton:Button

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences =
            getSharedPreferences(
                getString(R.string.preferences_file),
                Context.MODE_PRIVATE
            )



        userEmailAddress = findViewById(R.id.login_email)
        userPassword = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_sign_in_button)
        signUpButton = findViewById(R.id.login_register_button)
        forgetPasswordButton = findViewById(R.id.btn_ForgetPassword)
        loginProgressBar = findViewById(R.id.loginProgressBar)

        mAuth = FirebaseAuth.getInstance()

        if (ConnectionManager().checkConnectivity(applicationContext)) {
            Toast.makeText(this@LoginActivity, "Internet is turned on", Toast.LENGTH_SHORT).show()
        } else {

            InternetAlertDialog()


        }

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        loginProgressBar.visibility = View.GONE

        if(isLoggedIn){
            startActivity(Intent(this@LoginActivity,UploadActivity::class.java))
        }else{
            loginButton.setOnClickListener(View.OnClickListener {


                //logic for login

                val Email = userEmailAddress.text.toString().trim()
                val Password = userPassword.text.toString().trim()

                if(Email.isEmpty()||Password.isEmpty()){
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter the credentials",
                        Toast.LENGTH_SHORT
                    ).show()

                }else{

                    loginProgressBar.visibility = View.VISIBLE

                    mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener {
                        if(it.isSuccessful){
                            loginProgressBar.visibility = View.GONE

                            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                            sharedPreferences.edit().putString("Email Address",Email.replace('.',',')).apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Logged in successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent =
                                Intent(this@LoginActivity, UploadActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else if(!ConnectionManager().checkConnectivity(applicationContext)){
                            loginProgressBar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, "Network error please check your internet connection", Toast.LENGTH_SHORT).show()
                        } else{
                            loginProgressBar.visibility = View.GONE
                            Toast.makeText(
                                this@LoginActivity,
                                it.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }



                }



            })
        }



        signUpButton.setOnClickListener(View.OnClickListener {


            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))


        })

        forgetPasswordButton.setOnClickListener(View.OnClickListener {


            startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))


        })



    }

    private fun InternetAlertDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Turn on internet connection")
        builder.setIcon(R.drawable.ic_action_internet)


        builder.setPositiveButton("Open Settings") { text, listener ->


            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)


        }
        builder.setNegativeButton("Cancel") { text, listener ->

        }
        val alertDialog: AlertDialog = builder.create()

        alertDialog.show()


    }
}