/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.speechtotext;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author truongnq
 */
public class HttpClient {

    private final OkHttpClient client;

    private static class UtilsLoader {

        private static final HttpClient INSTANCE = new HttpClient();
    }

    public static HttpClient getInstance() {
        return UtilsLoader.INSTANCE;
    }

    public HttpClient() {
        if (UtilsLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
        client = new OkHttpClient.Builder()
                .readTimeout(120000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .connectTimeout(120000, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(20, 5L, TimeUnit.MINUTES))
                .build();
    }

    public void executeHttpRequest(String id, Request request) throws IOException {
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();
                        System.out.println("BEGIN-----------------" + id + "-----------------------");
                        System.out.println(res);
                        System.out.println("END-------------------" + id + "-----------------------");

                    }
                });
    }

}
