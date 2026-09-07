package com.amadeus.kurisu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // 注入原生桥，供 HTML 调用 DeepSeek API（绕过浏览器 CORS 限制）
        webView.addJavascriptInterface(new AmadeusBridge(), "AmadeusBridge");

        // 加载本地 assets 中的页面
        webView.loadUrl("file:///android_asset/amadeus.html");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 供 JavaScript 调用的原生桥。
     * 负责向 DeepSeek（及其他 OpenAI 兼容接口）发起 HTTP 请求，
     * 并把响应文本原样返回给 JS。
     */
    public class AmadeusBridge {

        @JavascriptInterface
        public String callApi(String endpoint, String apiKey, String model, String payloadJson) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(endpoint);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                conn.setDoOutput(true);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(120000);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payloadJson.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                InputStream is = (code >= 200 && code < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                StringBuilder sb = new StringBuilder();
                if (is != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    }
                }
                // 如果不是 2xx，包装成带 error 的 JSON 让前端统一处理
                if (code < 200 || code >= 300) {
                    JSONObject err = new JSONObject();
                    err.put("error", true);
                    err.put("code", code);
                    err.put("message", sb.toString());
                    return err.toString();
                }
                return sb.toString();

            } catch (Exception e) {
                JSONObject err = new JSONObject();
                try {
                    err.put("error", true);
                    err.put("code", -1);
                    err.put("message", e.getMessage());
                    return err.toString();
                } catch (Exception ex) {
                    return "{\"error\":true,\"code\":-1,\"message\":\"bridge exception\"}";
                }
            } finally {
                if (conn != null) conn.disconnect();
            }
        }
    }
}
