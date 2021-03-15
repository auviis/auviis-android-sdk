package com.auviis.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.auviis.sdk.*;

public class MainActivity extends Activity implements AuviisDelegate, CompoundButton.OnCheckedChangeListener, View.OnTouchListener {
    private boolean audioPermission = false;
    private long active_channel_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        AuviisClass.getInstance().setDelegate(this);
        AuviisClass.loadLibrary(this);
        //checking if you have the permission
        AuviisClass.getInstance().requestAudioPermission();
        //
        Switch s = (Switch) findViewById(R.id.swFreeTalk);

        if (s != null) {
            s.setOnCheckedChangeListener(this);
        }
        //
        Button b = (Button) findViewById(R.id.btPushToTalk);

        if (b != null) {
            b.setOnTouchListener(this);
        }
        //
        Button b2 = (Button) findViewById(R.id.btVoiceMsg);

        if (b2 != null) {
            b2.setOnTouchListener(this);
        }
    }

    @Override
    public void onEnableAudioPermission(){
        audioPermission = true;
    }

    @Override
    public void onDisableAudioPermission(){

    }
    public void startSDK(View v){
        AuviisClass.init(this,"6785JH889bhFGKU8904","PnHDEHHEIhjjAvcgQWUbcv");
        AuviisClass.getInstance().connect();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\n SDK is connecting");
            }
        });
    }
    public void stopSDK(View v){

        AuviisClass.getInstance().stop();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\n SDK stopped");
            }
        });
    }
    public void speakerOut(View v){
        AuviisClass.getInstance().outputToSpeaker();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\n Voice output to speaker");
            }
        });
    }
    public void micOut(View v){
        AuviisClass.getInstance().outputToDefault();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\n Voice output to headset");
            }
        });
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Button b = (Button) findViewById(R.id.btPushToTalk);


        if(isChecked) {
            //do stuff when Switch is ON
            if (b != null) {
                b.setEnabled(false);
            }
            AuviisClass.getInstance().unmuteSend();;
        } else {
            //do stuff when Switch if OFF
            if (b != null) {
                b.setEnabled(true);
            }
            AuviisClass.getInstance().muteSend();;
        }
    }

    @Override
    public void onSDKInitSuccess(long peer_id) {
        Log.i("AuviisSDK","AuviisSDK init successfully");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText("AuviisSDK init successfully");
            }
        });
    }

    @Override
    public void onSDKActivated(long peer_id) {
        Log.i("AuviisSDK","AuviisSDK has assigned your peer id of " + peer_id);
        AuviisClass.getInstance().joinChannel(123);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\nAuviisSDK has assigned your peer id of " + peer_id);
            }
        });
    }

    @Override
    public void onSDKError(int code, String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\nAuviisSDK has error " + reason);
            }
        });
    }

    @Override
    public void onSDKJoinChannel(int code, long channel_id, long member_count, String msg) {
        AuviisClass.getInstance().setActiveVoiceChannel(channel_id);
        active_channel_id = channel_id;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\nAuviisSDK has joined channel " + channel_id);
            }
        });
    }

    @Override
    public void onReceiveChannelMembers(long channel_id, long[] members) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\nAuviisSDK has " + members.length + " online");
            }
        });
    }

    @Override
    public void onSDKTextMessage(long sender_id, long channel_id, String msg) {
        Log.i("AuviisSDK","onSDKTextMessage");
    }

    @Override
    public void onSDKVoiceMessage(long sender_id, long channel_id, String msg_id) {
        Log.i("AuviisSDK","onSDKVoiceMessage");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtChatContent = (TextView)findViewById(R.id.txtChatContent);
                txtChatContent.setText(txtChatContent.getText() + "\nVoice message comes " + msg_id);
            }
        });
    }

    @Override
    public void onSDKVoiceMessageReady(int code, long record_id) {
        Log.i("AuviisSDK","onSDKVoiceMessageReady");
        if(active_channel_id <=0) return;
        AuviisClass.getInstance().sendVoiceChat(active_channel_id);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch ( motionEvent.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                if(view.getId() == R.id.btPushToTalk) AuviisClass.getInstance().unmuteSend();
                if(view.getId() == R.id.btVoiceMsg) {
                    AuviisClass.getInstance().recordVoice();
                    Switch s = (Switch) findViewById(R.id.swFreeTalk);
                    if (s!=null){
                        s.setChecked(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(view.getId() == R.id.btPushToTalk) AuviisClass.getInstance().muteSend();
                if(view.getId() == R.id.btVoiceMsg) {
                    AuviisClass.getInstance().stopRecord();
                }
                break;
        }
        return false;
    }
}