package fr.stks.wojakmemesrealm.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.stks.wojakmemesrealm.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinLinkBtn.setOnClickListener {
            Intent(this, SignInActivity::class.java).run {
                startActivity(this)
            }
        }
    }
}