package com.worksop.app.az;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WebViewActivity extends AppCompatActivity {

    private WebView webViewPopUp;
    private AlertDialog builder;
    private Context globalContext;

    private String url = "https://ownersdirect-croatia.com/";

    private WebView webView;
    private String userAgent;
    SwipeRefreshLayout swipe;
    RelativeLayout im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.myWebView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        im=  findViewById(R.id.im);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipe.setRefreshing(true);

                LoadWebPull();
                //LoadWeb();
            }
        });

        // Set User Agent
        userAgent = System.getProperty("http.agent");
        webSettings.setUserAgentString(userAgent + "yourAppName");

        // Enable Cookies
        CookieManager.getInstance().setAcceptCookie(true);
        if (android.os.Build.VERSION.SDK_INT >= 21)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);



        // Handle Popups
        webView.setWebChromeClient(new CustomChromeClient());
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        globalContext = this.getApplicationContext();

        // WebView Tweaks
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipe.setRefreshing(true);


            }

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                swipe.setRefreshing(false);
                im.setVisibility(View.GONE);


            }
        });
    }
    public void LoadWebPull(){







        String url = webView.getUrl();
        webView.loadUrl(url);




    }

    class CustomChromeClient extends WebChromeClient {


        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            webViewPopUp = new WebView(globalContext);
            webViewPopUp.setVerticalScrollBarEnabled(false);
            webViewPopUp.setHorizontalScrollBarEnabled(false);
            webViewPopUp.setWebChromeClient(new CustomChromeClient());
            webViewPopUp.getSettings().setJavaScriptEnabled(true);
            webViewPopUp.getSettings().setSaveFormData(true);
            webViewPopUp.getSettings().setEnableSmoothTransition(true);
            webViewPopUp.getSettings().setUserAgentString(userAgent + "yourAppName");

            // pop the  webview with alert dialog
//            builder = new AlertDialog.Builder(WebViewActivity.this).create();
//            builder.setTitle("");
//            builder.setView(webViewPopUp);
//
//            builder.setButton("Close", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    webViewPopUp.destroy();
//                    dialog.dismiss();
//                }
//            });
//
//            builder.show();
            //builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                cookieManager.setAcceptThirdPartyCookies(webViewPopUp, true);
                cookieManager.setAcceptThirdPartyCookies(webView, true);
            }

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(webViewPopUp);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            //Toast.makeText(contextPop,"onCloseWindow called",Toast.LENGTH_SHORT).show();
            try {
                webViewPopUp.destroy();
            } catch (Exception e) {
                Log.d("Destroyed with Error ", e.getStackTrace().toString());
            }

            try {
                builder.dismiss();
            } catch (Exception e) {
                Log.d("Dismissed with Error: ", e.getStackTrace().toString());
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }
        }
        super.onBackPressed();
    }
}