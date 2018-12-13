/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.speechtotext;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import vn.vnpay.daos.DataDao;

/**
 *
 * @author truongnq
 */
public class HttpClient {

    private static final Logger LOGGER = LogManager.getLogger(HttpClient.class);

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

    public void executeHttpRequest(int id, long uid, Request request, CountDownLatch countDown) throws IOException {
        client.newCall(request)
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(final Call call, IOException e) {
                        LOGGER.error("ID: [{}] - UID: [{}] - Request: {} - Err: {}", id, uid, request, e);
                        countDown.countDown();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        try {

                            String res = response.body().string();
                            LOGGER.info("ID: [{}] - UID: [{}] - Res: {} - Response: {}", id, uid, res, response);
                            DataDao.getInstance().insertText(id, uid, res, 0);
                            JSONObject json = new JSONObject(res);
                            if (!json.has("results")) {
                                return;
                            }
                            JSONArray results = json.getJSONArray("results");
                            String text = "";
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject result = results.getJSONObject(i);
                                if (!result.has("alternatives")) {
                                    continue;
                                }
                                JSONArray alternatives = result.getJSONArray("alternatives");
                                for (int j = 0; j < alternatives.length(); j++) {
                                    JSONObject alternative = alternatives.getJSONObject(j);
                                    text += alternative.getString("transcript");
                                }
                            }
                            if (Strings.isNotBlank(text)) {
                                DataDao.getInstance().insertText(id, uid, text, 1);
                            }
                        } catch (Exception e) {
                            LOGGER.error("ID: [{}] - UID: [{}] - onResponse...failed. Error: {}", id, uid, e);
                        } finally {
                            countDown.countDown();
                        }

                    }
                });
    }

}
