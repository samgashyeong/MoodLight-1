package com.example.moodlight.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moodlight.R
import com.example.moodlight.dialog.CommonDialog
import com.example.moodlight.dialog.CommonDialogInterface
import com.example.moodlight.dialog.LogoutDialog
import com.example.moodlight.dialog.LogoutDialogInterface
import com.example.moodlight.screen.mainstatics.MainStatisticsFragment
import com.example.moodlight.screen.initial.InitialActivity
import com.example.moodlight.screen.main1.MainFragment1
import com.example.moodlight.screen.main2.MainFragment2
import com.example.moodlight.screen.main3.MainFragment3
import com.example.moodlight.util.FirebaseUtil
import com.example.moodlight.util.NetworkStatus
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), CommonDialogInterface, LogoutDialogInterface {

    private val mainFragment1 by lazy { MainFragment1() }
    private val mainFragment2 by lazy { MainFragment2() }
    private val mainFragment3 by lazy { MainFragment3() }
    private val mainStatisticsFragment by lazy { MainStatisticsFragment() }
    private val networkStatus: Int by lazy { NetworkStatus.getConnectivityStatus(applicationContext) }

    private lateinit var dialog: CommonDialog
    private lateinit var logoutDialog: LogoutDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeFragment(mainStatisticsFragment)
        findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.nullItem


        if (networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            Toast.makeText(baseContext, "???????????? ?????????????????? Wifi ????????? ???????????????.", Toast.LENGTH_SHORT).show()
        }

        dialog = CommonDialog(
            this, this, "????????????", "????????? ????????? ???????????????????\n?????? ????????? ????????? ????????? ??? ????????????.", "????????????", "??????"
        )
        logoutDialog = LogoutDialog(this, this, "????????????", "??????????????? ???????????????????", "????????????", "??????")




        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main2 -> {
                    changeFragment(mainFragment2)
                    true
                }
                R.id.main3 -> {
                    changeFragment(mainFragment3)
                    true
                }
                else -> false
            }
        }
        findViewById<FloatingActionButton>(R.id.faBtn).setOnClickListener {
            //?????? ?????????
        }

    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, fragment).commit()
    }

    fun onClickBtnInFragment(i: Int) {
        when (i) {
            1 -> {
                dialogShow()
            }
            2 -> {
                logoutDialogShow()
            }
        }
    }

    private fun logoutDialogShow() {
        logoutDialog.show()
    }

    private fun dialogShow() {
        dialog.show()
    }

    override fun onCheckBtnClick() {
        FirebaseUtil.getAuth().currentUser!!.delete()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        FirebaseUtil.getFireStoreInstance().collection("users")
                            .document(FirebaseUtil.getUid())
                            .delete()
                    }
                    Toast.makeText(this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, InitialActivity::class.java))
                    dialog.dismiss()
                    finish()
                } else {
                    Toast.makeText(this, "????????? ?????????????????????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "????????? ?????????????????????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCancleBtnClick() {
        dialog.cancel()
    }

    override fun onClickLogout() {

        FirebaseUtil.getAuth().signOut()
        logoutDialog.dismiss()
        startActivity(Intent(this, InitialActivity::class.java))
        logoutDialog.dismiss()
        finish()
    }

    override fun onCancelLogout() {
        logoutDialog.dismiss()
    }


}