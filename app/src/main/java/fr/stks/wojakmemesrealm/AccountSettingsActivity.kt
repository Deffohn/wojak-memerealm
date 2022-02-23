package fr.stks.wojakmemesrealm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.stks.wojakmemesrealm.databinding.ActivityAccountSettingsBinding

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        
    }
}