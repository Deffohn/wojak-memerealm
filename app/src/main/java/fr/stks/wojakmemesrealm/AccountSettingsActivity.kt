package fr.stks.wojakmemesrealm

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import fr.stks.wojakmemesrealm.auth.SignInActivity
import fr.stks.wojakmemesrealm.databinding.ActivityAccountSettingsBinding
import fr.stks.wojakmemesrealm.model.User
import kotlinx.android.synthetic.main.activity_sign_up.*

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var user: FirebaseUser
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance().currentUser!!
        setupListeners()
    }

    private fun setupListeners() {
        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            Intent(this@AccountSettingsActivity, SignInActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
            finish()
        }

        binding.saveProfileBtn.setOnClickListener {
            if (checker == "clicked"){

            }
            else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun updateUserInfoOnly() {
        when {
            TextUtils.isEmpty(binding?.fullName.text.toString()) -> {
                Toast.makeText(this, "Please write full name first.", Toast.LENGTH_SHORT).show()
            }
            binding?.username.text.toString() == "" -> {
                Toast.makeText(this, "Please write username first.", Toast.LENGTH_SHORT).show()
            }
            binding?.bio.text.toString() == "" -> {
                Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = binding?.fullName.text.toString().lowercase()
                userMap["username"] = binding?.username.text.toString().lowercase()
                userMap["bio"] = binding?.bio.text.toString()

                usersRef.child(user.uid).updateChildren(userMap)
                Toast.makeText(this, "Your account infomations have been updated sucessfully.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun userInfo(){
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    val user = dataSnapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(binding?.profileImg)
                    binding?.username?.setText(user!!.getUsername())
                    binding?.fullName?.setText(user!!.getFullname())
                    binding?.bio?.setText(user!!.getBio())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}