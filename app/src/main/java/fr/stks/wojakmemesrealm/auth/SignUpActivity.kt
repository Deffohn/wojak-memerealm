package fr.stks.wojakmemesrealm.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.stks.wojakmemesrealm.MainActivity
import fr.stks.wojakmemesrealm.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.signinLinkBtn.setOnClickListener {
            Intent(this, SignInActivity::class.java).run {
                startActivity(this)
            }
        }

        binding.signupBtn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val fullName = binding.fullnameSignup.text.toString()
        val username = binding.usernameSignup.text.toString()
        val email = binding.emailSignup.text.toString()
        val password = binding.passwordSignup.text.toString()

        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "Full name is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this, "Username is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required!", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.apply {
                    setTitle("SignUp")
                    setMessage("Welcome among us! Please wait a bit...")
                    setCanceledOnTouchOutside(false)
                    show()
                }

                val fAuth = FirebaseAuth.getInstance()
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveUserInfo(fullName, username, email, progressDialog)
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

    private fun saveUserInfo(fullName: String, username: String, email: String, progressDialog: ProgressDialog) {
        val fAuth = FirebaseAuth.getInstance()
        val dbUsers = FirebaseDatabase.getInstance().reference.child("Users")

        val uid = fAuth.currentUser!!.uid
        dbUsers.child(uid).setValue(HashMap<String, Any>().apply {
            put("uid", uid)
            put("fullname", fullName)
            put("username", username)
            put("email", email)
            put("image", "https://firebasestorage.googleapis.com/v0/b/wojak-memes-realm.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=e8d07ce3-99a4-4b3c-bd63-1037511ee086")
            put("bio", "")
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(this, "Your account has been created sucessfully.", Toast.LENGTH_SHORT).show()

                Intent(this@SignUpActivity, MainActivity::class.java).apply {
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