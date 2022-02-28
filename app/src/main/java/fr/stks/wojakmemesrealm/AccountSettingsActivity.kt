package fr.stks.wojakmemesrealm

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import fr.stks.wojakmemesrealm.auth.SignInActivity
import fr.stks.wojakmemesrealm.databinding.ActivityAccountSettingsBinding
import fr.stks.wojakmemesrealm.model.User

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var user: FirebaseUser

    private var checker = ""
    private var url = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        setupListeners()
    }

    private fun setupListeners() {
        binding.editImageBtn.setOnClickListener {
            CropImage.activity().apply {
                setAspectRatio(1, 1)
                start(this@AccountSettingsActivity)
            }
        }

        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            Intent(this@AccountSettingsActivity, SignInActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
            finish()
        }

        binding.saveProfileBtn.setOnClickListener {
            if (checker == "clicked")
                uploadImageAndUpdateInfo()
            else
                updateUserInfoOnly()
        }

        binding.closeProfileBtn.setOnClickListener {
            if (checker == "clicked"){
                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
            }
            else
                finish()
        }

        userInfo()
    }

    private fun updateUserInfoOnly() {
        when {
            TextUtils.isEmpty(binding.fullName.text.toString()) -> {
                Toast.makeText(this, "Please write full name first.", Toast.LENGTH_SHORT).show()
            }
            binding.username.text.toString() == "" -> {
                Toast.makeText(this, "Please write username first.", Toast.LENGTH_SHORT).show()
            }
            binding.bio.text.toString() == "" -> {
                Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = binding.fullName.text.toString().lowercase()
                userMap["username"] = binding.username.text.toString().lowercase()
                userMap["bio"] = binding.bio.text.toString()

                usersRef.child(user.uid).updateChildren(userMap)
                Toast.makeText(this, "Your account infomations have been updated sucessfully.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun userInfo(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(binding.profileImg)
                    binding.username.setText(user.getUsername())
                    binding.fullName.setText(user.getFullname())
                    binding.bio.setText(user.getBio())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result?.uriContent
            imageUri?.let {
                binding.profileImg.setImageURI(imageUri)
            }
        }
    }

    private fun uploadImageAndUpdateInfo() {
        when {
            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.fullName.text.toString()) -> Toast.makeText(this, "Please write full name first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.username.text.toString()) -> Toast.makeText(this, "Please write username first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.bio.text.toString()) -> Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this).apply {
                    setTitle("Account Settings")
                    setMessage("Please wait, we are updating your profile...")
                    show()
                }

                val picRef = storageProfilePicRef!!.child(user.uid + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = picRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            progressDialog.dismiss()
                            throw it
                        }
                    }
                    return@Continuation picRef.downloadUrl
                }).addOnCompleteListener {
                    OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            url = task.result.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Users")

                            val userMap = HashMap<String, Any>()
                            userMap["fullname"] = binding.fullName.text.toString().lowercase()
                            userMap["username"] = binding.username.text.toString().lowercase()
                            userMap["bio"] = binding.bio.text.toString()
                            userMap["image"] = url

                            ref.child(user.uid).updateChildren(userMap)
                            Toast.makeText(this, "Your account informations have been updated sucessfully.", Toast.LENGTH_SHORT).show()

                            progressDialog.dismiss()

                            val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            progressDialog.dismiss()
                        }
                    }
                }
            }
        }
    }
}