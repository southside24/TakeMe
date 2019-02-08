package com.example.opeyemi.takeme.common;

import android.content.Context;
import android.content.Intent;

import com.example.opeyemi.takeme.SplashScreen;
import com.example.opeyemi.takeme.model.User;


public class Common {
    public static User currentUser;

    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 3;

    public static void logout(Context context){
        currentUser = null;
        context.startActivity(new Intent(context, SplashScreen.class));
    }

    public static String handlePhoneNumber(String phoneNumber){

        phoneNumber = phoneNumber.replaceAll(" ","");
        phoneNumber = phoneNumber.replaceAll("-","");
        phoneNumber = phoneNumber.replaceAll("\\(","").replaceAll("\\)", "");


        if(String.valueOf(phoneNumber.charAt(0)).equals("+")){
            phoneNumber = phoneNumber.replace("+234", "0");
        }
        return phoneNumber;
    }
}
