package com.salman.playsong

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var fragments: ArrayList<Fragment> = ArrayList()
    var titles: ArrayList<String> = ArrayList()

    fun addFragments(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

/*    override fun getItemPosition(ob: Any): Int {
        if (fragments.contains(ob)) {
            return POSITION_UNCHANGED
        }
        return POSITION_NONE
    }*/

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }



}