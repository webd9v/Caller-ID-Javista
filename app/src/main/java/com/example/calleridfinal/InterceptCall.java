package com.example.calleridfinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class InterceptCall extends BroadcastReceiver {
    private static final String TAG = "CustomBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            boolean isFound;


            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
//                Toast.makeText(context,"Incoming Call State",Toast.LENGTH_SHORT).show();
//                Toast.makeText(context,"Ringing State Number is :"+incomingNumber,Toast.LENGTH_SHORT).show();

                if(incomingNumber!=null) {
                    try{
                        String contactInfo=MainActivity.contactsByPhone.get(incomingNumber);
                        System.out.println("++++++++++HashInfo from intercept:"+contactInfo);


                        contactInfo=contactInfo.split(":")[0];
                        isFound=true;
                        Intent intent1 = new Intent(context, CallScreen.class);
                        intent1.putExtra("isFound",isFound);
                        intent1.putExtra("fullname",contactInfo);
                        intent1.putExtra("phoneNumber", incomingNumber);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent1);
                    }catch (Exception e){
                        isFound=false;
                        Intent intent1 = new Intent(context, CallScreen.class);
                        intent1.putExtra("isFound",isFound);
                        intent1.putExtra("phoneNumber", incomingNumber);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent1);
                    }

                }
            }
//            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
//                Toast.makeText(context,"Call Received State",Toast.LENGTH_SHORT).show();
//            }
//            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                Toast.makeText(context,"Call Idle State",Toast.LENGTH_SHORT).show();
//            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
