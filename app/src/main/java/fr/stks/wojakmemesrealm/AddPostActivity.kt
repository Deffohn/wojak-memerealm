package fr.stks.wojakmemesrealm

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.canhub.cropper.CropImage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import fr.stks.wojakmemesrealm.databinding.ActivityAddPostBinding
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private var url = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null
    private lateinit var user: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_add_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        save_new_post_btn.setOnClickListener { uploadImage()}

        CropImage.activity().apply {
            setAspectRatio(2, 1)
            start(this@AddPostActivity)
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                val result = CropImage.getActivityResult(data)
                imageUri = result?.uriContent
                imageUri?.let {
                    binding.imagePost.setImageURI(imageUri)
                }
            }
        }
    }

    private fun uploadImage() {
        when {
            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.descriptionPost.text.toString()) -> Toast.makeText(this, "Please write a description.", Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this).apply {
                    setTitle("Adding New Post")
                    setMessage("Please wait, we are adding your picture post...")
                    show()
                }

                val picRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

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

                            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                            val postId = ref.push().key
                            val postMap = HashMap<String, Any>()
                            postMap["postid"] = postId!!
                            postMap["description"] = binding.descriptionPost.text.toString().lowercase()
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["postimage"] = url

                            ref.child(postId).updateChildren(postMap)
                            Toast.makeText(this, "Your post have been uploaded sucessfully.", Toast.LENGTH_SHORT).show()

                            progressDialog.dismiss()

                            val intent = Intent(this@AddPostActivity, MainActivity::class.java)
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