package com.example.ais3.moneytaps.Models;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.ais3.moneytaps.R;

public class ConnectionDetector extends AppCompatActivity
{
    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }


    public boolean isConnectingToInternet()
    {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet

                return true;
            } else {
                return false;
                // not connected to the internet
            }
        }
        else {
            return false;
        }
    }

    public void noInternetConectionShowToast()
    {
        ProgressDialog dialogs = new ProgressDialog(_context);
        dialogs.setCancelable(false);

        final Dialog openDialog = new Dialog(_context);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.custom_alert_dialog_checking_internet_connection);
        openDialog.setCancelable(false);
        // openDialog.getWindow().setTitleColor(getResources().getColor(R.color.colorPrimary));
        dialogs.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
        //openDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogs.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialogs.getWindow().setAttributes(lp);
        //itemsValue = "";

        Button dialogButtonYes = (Button)openDialog.findViewById(R.id.btn_select);
        dialogButtonYes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(dialogIntent);
                openDialog.dismiss();
            }
        });

        Button dialogButtonNo=(Button)openDialog.findViewById(R.id.btn_cancel);
        dialogButtonNo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                _context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                openDialog.dismiss();
            }
        });
        openDialog.show();

    }

}
