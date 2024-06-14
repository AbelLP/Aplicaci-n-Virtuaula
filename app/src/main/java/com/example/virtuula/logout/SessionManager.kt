package com.example.virtuula.logout

import android.os.Handler
import android.os.Looper

class SessionManager(private val logoutListener:LogoutListener) {
    private val sessionTimeout:Long=10*60*1000
    private val handler= Handler(Looper.getMainLooper())
    private var lastActionTime=System.currentTimeMillis()

    init{
        startSessionTimer()
    }

    fun onUserAction(){
        lastActionTime=System.currentTimeMillis()
    }

    private fun startSessionTimer(){
        handler.postDelayed({
            val elapsedTime=System.currentTimeMillis()-lastActionTime
            if(elapsedTime>=sessionTimeout){
                logoutListener.onSessionTimeout()
            }else{
                startSessionTimer()
            }
        }, sessionTimeout)
    }

    interface LogoutListener{
       fun onSessionTimeout()
    }



}