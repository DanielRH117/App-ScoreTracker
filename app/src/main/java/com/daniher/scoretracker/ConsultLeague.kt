package com.daniher.scoretracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ConsultLeague : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var tournamentNameTextView: TextView
    private lateinit var tournamentName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consult_league)

        tournamentName = intent.getStringExtra("tournamentName") ?: ""

        tabLayout = findViewById(R.id.CLtabLayout)
        viewPager = findViewById(R.id.CLviewPager)
        tournamentNameTextView = findViewById(R.id.CLTounamentTV)

        val tabAdapter = TabAdapter(this)
        viewPager.adapter = tabAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "CL_Tabla"
                1 -> tab.text = "CL_Partidos"
            }
        }.attach()

        tournamentNameTextView.text = tournamentName
    }

    private inner class TabAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CLTableFragment.newInstance(tournamentName)
                1 -> CLMatchFragment.newInstance(tournamentName)
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }
}