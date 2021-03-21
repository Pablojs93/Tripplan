package com.pjas.tripplan.App.MyTrips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.pjas.tripplan.R
import kotlinx.android.synthetic.main.mytrips_layout.*

class MyTrips_ : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val x = inflater.inflate(R.layout.mytrips_layout,null)

        tabLayout = x.findViewById<View>(R.id.tabs) as TabLayout
        viewPager = x.findViewById<View>(R.id.viewpager) as ViewPager

        viewPager.adapter = MyAdapter(childFragmentManager)
        tabLayout.post{
            tabLayout.setupWithViewPager(viewPager)
        }
        return x
    }

    internal inner class MyAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm){
        override fun getItem(position: Int): Fragment {
            when(position){
                0 -> return FutureTrips()
                1 -> return OldTrips()
            }
            throw IllegalStateException("position $position is invalid for this viewpager")
        }

        override fun getCount(): Int {
            return items
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> return "Future Trips"
                1 -> return "Old Trips"
            }
            throw IllegalStateException("position $position is invalid for this viewpager")
        }
    }

    companion object{
        lateinit var tabLayout : TabLayout
        lateinit var viewPager: ViewPager
        lateinit var toolbar: Toolbar
        var items = 2
    }
}