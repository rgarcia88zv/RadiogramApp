package com.example.radiogramapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private boolean run = true;
    private Socket client;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private Thread listeningThread;
    TextView tvMessage;
    FloatingActionButton btSend;
    EditText etCampo;
    String texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMessage = findViewById(R.id.tvMessage);
        btSend = findViewById(R.id.btSend);
        etCampo = findViewById(R.id.etMessage);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startClient("10.0.2.2",5000);
            }
        });
    thread.start();

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = etCampo.getText().toString();
                Thread thread1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            flujoS.writeUTF(text);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread1.start();

                etCampo.setText("");

            }
        });
    }

    public void startClient(String host, int port){
        try {
            client = new Socket(host, port);
            flujoE = new DataInputStream(client.getInputStream());
            flujoS = new DataOutputStream(client.getOutputStream());
            listeningThread = new Thread(){
                @Override
                public void run() {

                    while(run){

                        try {
                            texto = flujoE.readUTF();
                            tvMessage.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvMessage.append(texto);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
            };
            listeningThread.start();

        } catch (IOException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void onDestroy() {
        
    }
}