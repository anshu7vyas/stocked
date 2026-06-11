package io.github.anshu7vyas.stocked

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import io.github.anshu7vyas.stocked.ui.HomePagerAdapter

/** Hosts the three tabs: Home (pantry), Shopping List, and Timeline. */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        requestNotificationPermissionIfNeeded()

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        supportActionBar?.setIcon(R.mipmap.ic_launcher)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout).apply {
            addTab(newTab().setText(R.string.tab_home))
            addTab(newTab().setText(R.string.tab_shopping_list))
            addTab(newTab().setText(R.string.tab_timeline))
            setTabTextColors(Color.WHITE, Color.BLACK)
            tabGravity = TabLayout.GRAVITY_FILL
        }

        val viewPager = findViewById<ViewPager>(R.id.pager).apply {
            adapter = HomePagerAdapter(supportFragmentManager, tabLayout.tabCount)
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0,
            )
        }
    }
}
