package fr.stks.wojakmemesrealm.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.stks.wojakmemesrealm.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupLinkBtn.setOnClickListener {
            Intent(this, SignUpActivity::class.java).run {
                startActivity(this)
            }
        }
    }
}