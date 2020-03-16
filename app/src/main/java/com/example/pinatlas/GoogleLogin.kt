package com.example.pinatlas

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class GoogleLogin : AppCompatActivity() {

    private lateinit var context: Context

    // When the activity is created onCreate is invoked
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login)

        // Checks if the user is authenticated if they are redirect them to the main activity

        if (FirebaseAuth.getInstance().currentUser != null) {
            redirectToMainActivity()
        }

        context = this;
    }
    // onActivityResult Called after the google login screen goes through or fails
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // redirects the user or redirectToMainActivity (back to Google login); both w/ toast and your sign-in
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(context, "Signed in as '" + user!!.email + "'", Toast.LENGTH_LONG).show()
                redirectToMainActivity()
            } else {
                Toast.makeText(context, "Failed to login, please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // called when user logins in successfully, they're taken to
    fun redirectToMainActivity() {
        val intent = Intent(this, TravelDash::class.java)
        startActivity(intent)
    }

    fun createSignInIntent(view: View) {
        // List of authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
        RC_SIGN_IN)
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}
