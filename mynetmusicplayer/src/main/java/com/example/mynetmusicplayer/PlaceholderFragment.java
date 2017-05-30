package com.example.mynetmusicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.io.UnsupportedEncodingException;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.example.mynetmusicplayer.R.id.webView;

public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    static Activity MyActivity;

    public PlaceholderFragment() {
    }

    public PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        MyActivity = getActivity();


        //webView
        //实例化WebView对象
        final WebView mWebview = (WebView) rootView.findViewById(webView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        //设置WebView属性，能够执行Javascript脚本
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.requestFocus();
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.setWebViewClient(new MyWebViewClient());
        mWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        mWebview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {  //表示按返回键
                        mWebview.goBack();   //后退
                        //webview.goForward();//前进
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
        switch (getArguments().getInt(ARG_SECTION_NUMBER,0)){
            case 0:
                mWebview.loadUrl("http://h.xiami.com");

                break;
            case 1:
                mWebview.loadUrl("https://m.y.qq.com");
                break;
            case 2:
                mWebview.loadUrl("http://www.baidu.com");
                break;
        }
        return rootView;
    }

    public void downSong(String songURL, String songName) {

        DownloadManager downloadManager = (DownloadManager) MyActivity.getSystemService(DOWNLOAD_SERVICE);
        String apkUrl = songURL;
        DownloadManager.Request request = new
                DownloadManager.Request(Uri.parse(apkUrl));

        request.setTitle(songName + ".mp3");
        request.setMimeType("audio/mpeg");
        request.allowScanningByMediaScanner();
        long downloadId = downloadManager.enqueue(request);
    }

    static class MyWebViewClient extends WebViewClient {
        private static String songURL;
        private static String songName;

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(final WebView view, String url, Bitmap favicon) {

        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            view.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    //页面加载结束后 去掉一些div
                    view.evaluateJavascript("javascript:document.getElementsByTagName('body')[0].removeChild(document.getElementsByClassName('navbar')[0])"
                            , null);
                    view.evaluateJavascript("javascript:document.getElementsByTagName('section')[0].removeChild(document.getElementById('J_Slide'))"
                            , null);
                }
            });
            super.onPageFinished(view, url);
        }

        @SuppressLint("NewApi")
        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view,
                                                          final WebResourceRequest request) {
            //获取歌曲url
            if (request != null && request.getUrl() != null
                    && request.getMethod().equalsIgnoreCase("get")
                    && request.getUrl().toString().contains("om5.alicdn.com")) {
                songURL = request.getUrl().toString();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.evaluateJavascript("javascript:document.getElementsByClassName('line current')[0].innerText"
                                , new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        if (value != null && !value.equals("null")) {
                                            songName = value.replace("\\n", "-").replace("\"", "");
                                            songName = songName.substring(0, songName.length() - 1);
                                        }

                                    }
                                });
                    }
                });
            }

            //如果点击了下载
            if (request != null && request.getUrl() != null
                    && request.getMethod().equalsIgnoreCase("get")
                    && request.getUrl().toString().contains("wgo.mmstat.com/xiamiwuxian")) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.evaluateJavascript("javascript:document.getElementsByTagName('body')[0].removeChild(document.getElementById('J_dialogTips'))"
                                , new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        //System.out.println("webView返回的数据" + value);
                                        //如果value不等于空，则说明出现了J_dialogTips，即点击了下载
                                        //调用下载方法
                                        if (!value.equals("null") && songURL != null) {
                                            //System.out.println(songURL);
                                            PlaceholderFragment placeholderFragment = new PlaceholderFragment();
                                            placeholderFragment.downSong(songURL, songName);

                                        }
                                    }
                                });
                    }
                });

            }


            return null;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
            if (!TextUtils.isEmpty(url) && Uri.parse(url).getScheme() != null
                    && url.contains("http://om5.alicdn.com")) {
                songURL = url;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.evaluateJavascript("javascript:document.getElementsByClassName('line current')[0].innerText"
                                , new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        if (value != null && !value.equals("null")) {
                                            String name = null;
                                            try {
                                                 name = new String(value.getBytes(),"utf-8");
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                            songName = name.replace("\\n", "-").replace("\"", "");
                                            songName = songName.substring(0, songName.length() - 1);

                                        }

                                    }
                                });
                    }
                });
            }

            //如果点击了下载
            if (url != null && url.contains("wgo.mmstat.com/xiamiwuxian")) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.evaluateJavascript("javascript:document.getElementsByTagName('body')[0].removeChild(document.getElementById('J_dialogTips'))"
                                , new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        //System.out.println("webView返回的数据" + value);
                                        //如果value不等于空，则说明出现了J_dialogTips，即点击了下载
                                        //调用下载方法
                                        if (!value.equals("null") && songURL != null) {
                                            //System.out.println(songURL);
                                            PlaceholderFragment placeholderFragment = new PlaceholderFragment();
                                            placeholderFragment.downSong(songURL, songName);

                                        }
                                    }
                                });
                    }
                });

            }
            return null;
        }

    }

}

