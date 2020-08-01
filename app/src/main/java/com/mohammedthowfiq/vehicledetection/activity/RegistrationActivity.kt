package com.mohammedthowfiq.vehicledetection.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mohammedthowfiq.vehicledetection.R
import com.mohammedthowfiq.vehicledetection.model.UserModel

class RegistrationActivity : AppCompatActivity() {

    lateinit var name: TextInputEditText
    lateinit var emailAddress: TextInputEditText
    lateinit var phoneNumber: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var confirmPassword: TextInputEditText

    lateinit var registerButton: Button
    lateinit var alreadyRegisteredButton: Button


    private var rootNode: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        sharedPreferences =
            getSharedPreferences(
                getString(R.string.preferences_file),
                Context.MODE_PRIVATE
            )

       val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)

        name = findViewById(R.id.register_username)
        emailAddress = findViewById(R.id.register_email)
        phoneNumber = findViewById(R.id.register_phoneNumber)
        password = findViewById(R.id.register_password)
        confirmPassword = findViewById(R.id.register_confirm_password)

        registerButton = findViewById(R.id.register_sign_up_button)
        alreadyRegisteredButton = findViewById(R.id.btn_AlreadyRegistered)

        mAuth = FirebaseAuth.getInstance()


        registerButton.setOnClickListener(View.OnClickListener {

            val Name = name.text.toString().trim()
            val EmailAddress = emailAddress.text.toString().trim()
            val PhoneNumber = phoneNumber.text.toString().trim()
            val Password = password.text.toString().trim()
            val ConfirmPassword = confirmPassword.text.toString().trim()

            if (Name.isEmpty()) {
                name.error = "Enter your Name"
            } else if (EmailAddress.isEmpty()) {
                emailAddress.error = "Enter your Email Address"
            } else if (PhoneNumber.isEmpty()) {
                phoneNumber.error = "Enter your Phone Number"
            } else if (Password.isEmpty()) {
                password.error = "Enter your Password"

            } else if (ConfirmPassword.isEmpty()) {
                confirmPassword.error = "Enter your Confirm Password"
            } else {

                if (Password == ConfirmPassword) {


                    mAuth.createUserWithEmailAndPassword(EmailAddress, Password)
                        .addOnCompleteListener {

                            if (it.isSuccessful) {

                            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                               sharedPreferences.edit().putString("Email Address",EmailAddress.replace('.',',')).apply()


                                rootNode = FirebaseDatabase.getInstance()
                                reference = rootNode?.getReference("Users")

                                val properEmailAddress = EmailAddress.replace('.', ',')

                                val userModelObject = UserModel(
                                    Name,
                                    properEmailAddress,
                                    PhoneNumber,
                                    Password,
                                    ConfirmPassword
                                )

                                reference?.child(properEmailAddress)?.setValue(userModelObject)

                                startActivity(
                                    Intent(
                                        this@RegistrationActivity,
                                        UploadActivity::class.java
                                    )
                                )
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Registered Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()


                            }else{

                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "User Already Exist!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        }


                } else {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please enter the same password in both password fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }

        })

        alreadyRegisteredButton.setOnClickListener(View.OnClickListener {

            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))


        })

    }
}