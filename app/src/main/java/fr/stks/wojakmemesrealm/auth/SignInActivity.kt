package fr.stks.wojakmemesrealm.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import fr.stks.wojakmemesrealm.MainActivity
import fr.stks.wojakmemesrealm.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // App Check Initialization (Firebase)
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )


        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    override fun onStart() {
        super.onStart()

        FirebaseAuth.getInstance().currentUser?.let {
            Intent(this@SignInActivity, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
            finish()
        }
    }

    private fun loginUser() {
        val email = binding.emailLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required!", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.apply {
                    setTitle("SignIn")
                    setMessage("Welcome back! Please wait a bit...")
                    setCanceledOnTouchOutside(false)
                    show()
                }

                val fAuth = FirebaseAuth.getInstance()
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show()

                        Intent(this@SignInActivity, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(this)
                        }
                        finish()
                    } else {
                        val error = task.exception.toString()
                        Toast.makeText(this, "Error: $error.", Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                        fAuth.signOut()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.loginBtn.setOnClickListener {
            loginUser()
        }

        binding.signupLinkBtn.setOnClickListener {
            Intent(this, SignUpActivity::class.java).run {
                startActivity(this)
            }
        }
    }
}