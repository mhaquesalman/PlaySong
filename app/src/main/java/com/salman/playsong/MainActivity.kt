package com.salman.playsong

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity


private const val TAG = "Main"
class MainActivity : AppCompatActivity() {
    private val viewPagerFragment: ViewpagerFragment = ViewpagerFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment, viewPagerFragment, null)
        transaction.commit()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun switchContent(it: MusicFiles) {
        viewPagerFragment.hideTabLayout()
        val fragment = AlbumDetailsFragment()
        val bundle = Bundle()
        bundle.putString("title", it.title)
        bundle.putString("artist", it.artist)
        fragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(ViewpagerFragment())
        transaction.add(R.id.fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun hideTabLayout() = viewPagerFragment.hideTabLayout()

    fun backAction() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
/*            supportFragmentManager.popBackStack(
                    "AlbumDetailsFragment",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            )*/
            supportFragmentManager.popBackStack()
//            viewPagerFragment.showTabLayout()
            //supportFragmentManager.beginTransaction().show(AlbumFragment())
            viewPagerFragment.refresh()
        }
    }


    override fun onBackPressed() {
//        viewPagerFragment.refresh()
        super.onBackPressed()

//        viewPagerFragment.showTabLayout()

    }

}