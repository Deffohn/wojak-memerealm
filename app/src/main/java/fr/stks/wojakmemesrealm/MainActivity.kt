package fr.stks.wojakmemesrealm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.stks.wojakmemesrealm.databinding.ActivityMainBinding
import fr.stks.wojakmemesrealm.fragments.HomeFragment
import fr.stks.wojakmemesrealm.fragments.NotificationsFragment
import fr.stks.wojakmemesrealm.fragments.ProfileFragment
import fr.stks.wojakmemesrealm.fragments.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    internal var selectedFragment: Fragment? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                selectedFragment = HomeFragment()
            }
            R.id.nav_search -> {
                selectedFragment = SearchFragment()
            }
            R.id.nav_add_post -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                selectedFragment = NotificationsFragment()
            }
            R.id.nav_profile -> {
                selectedFragment = ProfileFragment()
            }
        }

        selectedFragment?.let {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                selectedFragment!!
            ).commit()
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)

        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            HomeFragment()
        ).commit()
    }
}