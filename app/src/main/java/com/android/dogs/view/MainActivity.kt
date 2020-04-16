package com.android.dogs.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.android.dogs.R
import com.android.dogs.util.PERMISSION_SEND_SMS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var handler : Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this,R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this,navController)
//        handler = Handler {
//             override fun onHandleMessage():Boolean{
//
//             }
//        }
//        handler.handleMessage()
//        val customThread = CustomThread()
//        customThread.setCallback(ge)
//        Thread(customThread).start()
//
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController,null)
    }

    fun checkSmsPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)){
                AlertDialog.Builder(this)
                    .setTitle("Send SMS permission")
                    .setMessage("This app requires access to send an SMS")
                    .setPositiveButton("Ask Me"){dialog, _ ->
                        requestSmsPermission()
                       // dialog.dismiss()
                    }
                    .setNegativeButton("NO"){dialog, _ ->
                        notifyDetailFragment(true)
                        //dialog.dismiss()
                    }
                    .show()
            }else{
                requestSmsPermission()
            }
        }else{
            notifyDetailFragment(true)
        }
    }

    private fun notifyDetailFragment(result: Boolean) {
        val activeFragment = fragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is DetailFragment){
            (activeFragment).onPermissionResult(result)
        }

    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
            PERMISSION_SEND_SMS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_SEND_SMS->{
                if (grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyDetailFragment(true)
                }else{
                    notifyDetailFragment(false)
                }
            }
        }
    }
}
