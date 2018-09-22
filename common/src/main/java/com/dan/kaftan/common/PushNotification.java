package com.dan.kaftan.common;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.provider.SyncStateContract;

import com.google.android.gms.common.api.Response;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PushNotification {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public void sendNotification(final String regToken, final String title, final String body) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body",body);
                    dataJson.put("title",title);
                    json.put("notification",dataJson);
                    json.put("to",regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+ "AIzaSyBgUls6B53xRbzJQ9LLfKPbGMJIwEcW2OE")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();

                    okhttp3.Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    System.out.println("sendnig notificaiton response: "+ finalResponse);

                }catch (Exception e){
                    System.out.println("failed sendnig notificaiton: "+ e);
                }
                return null;
            }
        }.execute();

    }
}
