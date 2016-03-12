package com.smapley.prints.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.smapley.prints.R;
import com.smapley.prints.activity.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by smapley on 15/10/23.
 */
public class Web extends Fragment {

    public static String url;
    private String url1 = "http://";
    public static String CookieStr;
    public int nowNum;
    public static String name;
    public static String qihao;
    public static String ip;
    private WebView webView;
    private SharedPreferences sharedPreferences;
    public static String formhash;

    public boolean isThread = false;

    private String url_now = null;

    private CookieManager cookieManager;
    private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web, container, false);
        sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        sp = getActivity().getSharedPreferences("URL", getActivity().MODE_PRIVATE);
        cookieManager = CookieManager.getInstance();
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        webView = (WebView) view.findViewById(R.id.webView);
        //支持JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.requestFocus();
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setSavePassword(true);
    }



    final class InJavaScriptLocalObj {

        @JavascriptInterface
        public void showSource(String html) {
            try {
                Document document = Jsoup.parse(html);
                Elements body = document.getElementsByTag("body");
                Elements span = body.get(0).getElementsByTag("span");
                String[] num = span.get(1).text().split(":");
                nowNum = Integer.parseInt(num[3]);
                Log.e("nowNum", "nowNum======>>" + nowNum);
                name = num[1].substring(0, num[1].length() - 3);
                Log.e("name", "name======>>" + name);
                qihao = num[2].substring(0, num[2].length() - 3);
                Log.e("qihao", "qihao======>>" + qihao);
                //保存
                if (name != null && !name.equals("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", name);
                    if (formhash.length() > 5) {
                        editor.putString(url, formhash);
                    }
                    editor.commit();
                }
                Log.e("webview", "----->" + webView.getVisibility());
                Elements script = document.getElementsByTag("script");
                String[] formhashs = script.get(2).toString().split(";");
                formhash = formhashs[1].split("'")[1];
                Log.e("script", "----->" + formhash);


            } catch (Exception e) {
//                e.printStackTrace();
                webView.setVisibility(View.VISIBLE);
            }


        }
    }

    /**
     * 获取IP地址
     *
     * @return
     */
    public void GetNetIp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ipaddr = "http://www.cmyip.com/";
                URL infoUrl = null;
                InputStream inStream = null;
                try {
                    infoUrl = new URL(ipaddr);
                    URLConnection connection = infoUrl.openConnection();
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    int responseCode = httpConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                        StringBuilder strber = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null)
                            strber.append(line + "\n");
                        inStream.close();
                        Document document = Jsoup.parse(strber.toString());
                        String result = document.getElementsByTag("h1").get(0).text();
                        String[] ips = result.split(" ");
                        ip = ips[4];
                    }
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private class WebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
            Log.d("asd","----");
            handler.proceed("14789","hainan0898");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            GetNetIp();
            return false;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            url_now = url;


            String CookieStrs = cookieManager.getCookie(url);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString(url, CookieStrs);
            editor.commit();
            view.loadUrl("javascript:window.local_obj.showSource('<body>'+"
                    + "document.getElementsByTagName('html')[0].innerHTML+'</body>');");
            try {
                CookieStr = CookieStrs.split("=")[1].split(";")[0];
                if (qihao.length() > 3 && name != null) {
                    Log.e("cookie", CookieStr);
                    if (name != null) {
                        ((MainActivity) getActivity()).print.getData();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void initData() {

        url = sp.getString("url", "7.600600111.com");
        cookieManager.setCookie(url1 + url, sp.getString(url1 + url, ""));
        formhash = sp.getString(url, "");
        webView.loadUrl(url1 + url);
        GetNetIp();
        if (!isThread) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        isThread = true;
                        try {
                            Thread.sleep(180000);
                            if (url_now != null && webView != null) {
                                mhandler.obtainMessage().sendToTarget();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            webView.loadUrl(url_now);
            Log.i("webthread", "run");
        }
    };

}
