package com.oneconnect.demoapp.ui


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */


import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManager
import com.infideap.drawerbehavior.AdvanceDrawerLayout
import com.oneconnect.demoapp.R


abstract class SideBarActivity : AppCompatActivity() {

    private lateinit var manager : ReviewManager
    protected var toolbar: Toolbar? = null
        private set

    @get:LayoutRes
    protected abstract val layoutRes: Int

    private var mDrawerLayout: AdvanceDrawerLayout? = null
    private var navigationView: NavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)

        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

    }


    override fun onBackPressed() {
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}