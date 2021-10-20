package com.example.admin.itamartaki;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The main activity of the app.
 * The "Player VS Player" button starts the game.
 * In the top there's a button that send you to the help activity.
 * There's also a menu with an option to toggle background music ON/OFF
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BroadCastBattery broadCastBattery;
    private Button btPvp;
    private Button btSave;
    private Button btLoad;
    private boolean musictoggle;
    private int player1WinNumber=0;
    private int player2WinNumber=0;
    private TextView player1wins,player2wins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btPvp = findViewById(R.id.btPvp);
        btSave = findViewById(R.id.btSave);
        btLoad = findViewById(R.id.btLoad);
        player1wins = findViewById(R.id.player1wins);
        player2wins = findViewById(R.id.player2wins);

        btPvp.setOnClickListener(this);
        btSave.setOnClickListener(this);
        btLoad.setOnClickListener(this);

        broadCastBattery = new BroadCastBattery();
        musictoggle=false;

        int playerwon = getIntent().getIntExtra("playerwon",0);
        if (playerwon==1)
        {
            player1WinNumber++;
            player1wins.setText("Player 1 wins: "+ player1WinNumber);
        }
        else if (playerwon==2)
        {
            player2WinNumber++;
            player2wins.setText("Player 2 wins: "+ player2WinNumber);
        }
        else
        {
            player1wins.setText("Player 1 wins: 0");
            player2wins.setText("Player 2 wins: 0");
        }
    }

    @Override
    public void onClick(View v) {
        String player1WinCount;
        if (btPvp == v)
        {
            customDialog("Player VS Player","You will play a game of TAKI with another person by taking turns on the same device.","cancelMethod1","okMethod1");
        } else if (btSave==v)
        {
            try
            {
                player1WinCount = String.valueOf(player1WinNumber)+","+String.valueOf(player2WinNumber);
                FileOutputStream out = openFileOutput("details1", MODE_PRIVATE);
                if(player1WinCount !=null)
                {
                    try {
                        out.write(player1WinCount.getBytes());
                        out.close();
                        Toast.makeText(this, out.getChannel().toString(),Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        } else if (btLoad==v)
        {
            try
            {
                InputStream in = openFileInput("details1");
                byte[]buffer=new byte[4096];
                try {
                    in.read(buffer);
                    String playersRecord =  new String(buffer);
                    player1WinCount = playersRecord.split(",")[0];
                    String player2WinCount = playersRecord.split(",")[1];
                    in.close();
                    if(player1WinCount !=null)
                        player1wins.setText("Player 1 wins: "+ player1WinCount);
                    if(player2WinCount !=null)
                        player2wins.setText("Player 2 wins: "+ player2WinCount);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
    private class BroadCastBattery extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int battery = intent.getIntExtra("level",0);
            if (battery <= 20)
            {
                Toast.makeText(context,"Your battery is at "+battery+"%!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(broadCastBattery,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(broadCastBattery);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return  true;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.musicButton)
        {
            if (musictoggle)
            {
                stopService(new Intent(this,MusicService.class));
            }
            else
            {
                startService(new Intent(this,MusicService.class));
            }
            musictoggle = !musictoggle;
        }
        else if(id==R.id.btHelp)
        {
            Intent ihelp = new Intent(this,Help.class);
            startActivity(ihelp);
        }
        else if (id==R.id.backButton)
        {
            Intent mainintent = new Intent(this,MainActivity.class);
            startActivity(mainintent);
        }
        return true;
    }
    private void cancelMethod1(){
        Log.d("cancel 1", "cancelMethod1: Called.");
    }
    private void okMethod1(){
        Log.d("ok 1", "okMethod1: Called.");
        Intent ipvp = new Intent(this,pvp.class);
        startActivity(ipvp);
    }

    private void customDialog(String title, String message, final String cancelMethod, final String okMethod){
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.cover);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("onclick dialog1", "onClick: Cancel Called.");
                        if(cancelMethod.equals("cancelMethod1")){
                            cancelMethod1();
                        }

                    }
                });

        builderSingle.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("onclick dialog2", "onClick: OK Called.");
                        if(okMethod.equals("okMethod1")){
                            okMethod1();
                        }
                    }
                });


        builderSingle.show();
    }
}
