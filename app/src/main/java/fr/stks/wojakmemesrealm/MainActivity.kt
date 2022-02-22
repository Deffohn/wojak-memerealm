package fr.stks.wojakmemesrealm

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.stks.wojakmemesrealm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var textView: TextView

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                textView.text = "Home"
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                textView.text = "Search"
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                textView.text = "Add Post"
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                textView.text = "Notifications"
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                textView.text = "Profile"
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        textView = findViewById(R.id.message)
        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)
    }
}