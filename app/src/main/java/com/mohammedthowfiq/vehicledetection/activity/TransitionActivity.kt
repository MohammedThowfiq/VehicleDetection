package com.mohammedthowfiq.vehicledetection.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.mohammedthowfiq.vehicledetection.R
import com.mohammedthowfiq.vehicledetection.fragment.HistoryFragment
import kotlinx.android.synthetic.main.activity_transistion.*

class TransitionActivity : AppCompatActivity() {

    lateinit var historyToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transistion)


        historyToolbar = findViewById(R.id.historyToolbar)


        setSupportActionBar(historyToolbar)
        supportActionBar?.title = "History"




        supportFragmentManager.beginTransaction().replace(R.id.frame, HistoryFragment()).commit()




    }
}