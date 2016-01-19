package com.example.xuzhi.easykitchen;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by xuzhi on 2016/1/19.
 */
public class FetchDbTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchDbTask.class.getSimpleName();
    private final Context mContext;
    public FetchDbTask(Context context) {
            mContext = context;
        }
    private Socket client;
    private PrintWriter printwriter;
    private String messsage = "Hi!This is a test";
    @Override
    protected Void doInBackground(String... params) {
        try {

            client = new Socket("192.168.0.15", 4444); // connect to the server
            printwriter = new PrintWriter(client.getOutputStream(), true);
            printwriter.write(messsage); // write the message to output stream

            printwriter.flush();
            printwriter.close();
            client.close(); // closing the connection

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
