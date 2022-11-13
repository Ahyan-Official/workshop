package com.worksop.app.az;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainWebActivity extends AppCompatActivity {

    //initializing WebView
    private WebView mwebView;

    SwipeRefreshLayout swipe;

    MyApplication myApplication;


    LinearLayout network;
    TextView retry;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;

    RelativeLayout im;

    ImageView back,reload,forward;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web);


        myApplication =  MyApplication.getmInstense();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        retry= (TextView) findViewById(R.id.retry);
        network= (LinearLayout) findViewById(R.id.network);
        //isWriteStoragePermissionGranted();

        im=  findViewById(R.id.im);



        Questions question1 = new Questions("What is the capital of US?","New York","Chicago","","","","");

//        back=  findViewById(R.id.back);
//        reload=  findViewById(R.id.relaod);
//        forward=  findViewById(R.id.forward);


        AdView adView = (AdView) findViewById(R.id.adView2);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle((Html.fromHtml("<font color=\"#ffffff\">" + "KEYLOAN NG" + "</font>")));
        }








        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipe.setRefreshing(true);

                LoadWebPull();
                //LoadWeb();
            }
        });


        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadWebPull();

            }
        });










        LoadWeb();




    }



    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted2");\
                isReadStoragePermissionGranted();
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted2");
            return true;
        }
    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void LoadWebPull(){







        String url = mwebView.getUrl();
        mwebView.loadUrl(url);




    }

    public void LoadWeb(){

        mwebView = (WebView) findViewById(R.id.myWebView);

        if (isNetworkAvailable()) {

            mwebView.setVisibility(View.VISIBLE);
            network.setVisibility(View.GONE);

        }else {

            mwebView.setVisibility(View.GONE);
            network.setVisibility(View.VISIBLE);

        }

        //improve webView performance
        mwebView.getSettings().getJavaScriptEnabled();
        mwebView.getSettings().setJavaScriptEnabled(true);
//        mwebView.getSettings().setLoadWithOverviewMode(true);
//        mwebView.getSettings().setUseWideViewPort(true);
        mwebView.getSettings().setDomStorageEnabled(true);
        mwebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //  mwebView.getSettings().setUserAgentString("Android Mozilla/5.0 AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        //mwebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        //        mwebView.getSettings().setAllowFileAccess(true);
//        mwebView.getSettings().setAllowFileAccess(true);
//        mwebView.getSettings().setAllowContentAccess(true);
        mwebView.getSettings().setDomStorageEnabled(true);
        mwebView.getSettings().setAppCacheEnabled(true);
        mwebView.getSettings().setLoadsImagesAutomatically(true);
        mwebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        // mwebView.getSettings().setUserAgentString(getString(R.string.app_name));
        mwebView.getSettings().setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        mwebView.getSettings().setDatabaseEnabled(true);
        mwebView.getSettings().setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");
        mwebView.getSettings().supportZoom();
        mwebView.setWebViewClient(new MyWebviewClient());


        mwebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                final String filename= URLUtil.guessFileName(url, contentDisposition, mimetype);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();

            }
        });

        mwebView.setWebChromeClient(new ChromeClient());




        if ( !isNetworkAvailable() ) {
            mwebView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

        }else{

            mwebView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

            mwebView.loadUrl("https://pepperfishpublishi.wixsite.com/website-4");

        }












    }

    ProgressBar progressBar;


    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == this.mUploadMessage) {
                    return;

                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {

                        result = null;

                    } else {

                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
        }

        return;
    }

    public class ChromeClient extends WebChromeClient {

        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;


        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {

                mFilePathCallback.onReceiveValue(null);

            }
            mFilePathCallback = filePath;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e("lllll", "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;

        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard

            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");

            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }

            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            mCapturedImageURI = Uri.fromFile(file);

            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[] { captureIntent });

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);


        }
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }


        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

            openFileChooser(uploadMsg, acceptType);
        }


        ChromeClient() {}



        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request.grant(request.getResources());
            }
        }
        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(         MainWebActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mwebView != null) {


                            mwebView.loadUrl("https://pepperfishpublishi.wixsite.com/website-4");
                            isWriteStoragePermissionGranted();

                        }
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        isReadStoragePermissionGranted();

                    }
                    //startLocationUpdates();
                } else {
                    // Permission Denied
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    }
                    //startLocationUpdates();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private class MyWebviewClient extends WebViewClient {



        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // do your handling codes here, which url is the requested url
            // probably you need to open that url rather than redirect:
            Log.e("weburl===",url.toString());
            if (url.startsWith("mailto:")) {
                final Activity activity =          MainWebActivity.this;
                if (activity != null) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(activity, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    activity.startActivity(i);
                    view.reload();
                    return true;
                }
            }

            if((url.startsWith("intent"))){

                view.stopLoading();
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

            }


            PackageManager pm = getApplicationContext().getPackageManager();



            if (url != null && (url.startsWith("https://api.whatsapp.com/send?phone=27670381226"))) {

                view.stopLoading();
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));


                Log.e("insta","lassan");

                return false;

            }else

            if (url.startsWith("tel:")) {
                view.stopLoading();

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                view.reload();
                return false;
            }else


            if (url != null && (url.startsWith("https://www.instagram.com") || url.startsWith("http://instagram.com/") || url.startsWith("https://instagram.com/"))) {

                view.stopLoading();
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));


                Log.e("insta","lassan");

                return false;

            }else
            if ((url.startsWith("https://www.linkedin.com"))) {

                view.stopLoading();
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));


                Log.e("insta","lassan");

                return false;

            }else
            if(url.startsWith("https://www.facebook.com/sharer") || url.startsWith("https://m.facebook.com/sharer")){


                view.stopLoading();

                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return false;
            }else




            if(url.startsWith("https://www.facebook.com") || url.startsWith("https://m.facebook.com")){

                view.stopLoading();

                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return false;
            }else

            if (url != null && (url.startsWith("https://www.facebook.com/") || url.startsWith("https://m.facebook.com/"))) {
                view.stopLoading();

                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return false;
            }else
            if (url != null && (url.startsWith("https://www.pinterest.com/") || url.startsWith("https://pinterest.com"))) {
                view.stopLoading();

                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return false;
            }else
            if (url != null && (url.startsWith("https://www.youtube.com/") || url.startsWith("https://m.youtube.com/"))) {
                view.stopLoading();

                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return false;
            }else
            if (url != null && (url.startsWith("https://twitter.com/") || url.startsWith("https://www.twitter.com/") || url.startsWith("https://mobile.twitter.com/"))) {
                view.stopLoading();

                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return false;
            }else

            {

                view.loadUrl(url);

                return true;
            }

// then it is not handled by default action

        }

        private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
            try {
                packageManager.getPackageInfo(packageName, 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_CC, cc);
            intent.setType("message/rfc822");
            return intent;
        }
        //ProgressDialogue
        ProgressDialog pd = null;
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            Log.e("error",description);
            if (isNetworkAvailable()) {
                mwebView.setVisibility(View.VISIBLE);
                network.setVisibility(View.GONE);
            }else {

                mwebView.setVisibility(View.GONE);
                network.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            pd=new ProgressDialog(MainWebActivity.this);
//            pd.setTitle("Please Wait..");
//            pd.setMessage("Website is Loading..");
//            pd.show();

            if (!isNetworkAvailable()) {
                mwebView.setVisibility(View.GONE);
                network.setVisibility(View.VISIBLE);
            }

            swipe.setRefreshing(true);

            //swipe.setRefreshing(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //pd.dismiss();

            // Load ads into Interstitial Ads
            im.setVisibility(View.GONE);

            swipe.setRefreshing(false);





            super.onPageFinished(view, url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {

            if (isNetworkAvailable()) {
                mwebView.setVisibility(View.VISIBLE);
                network.setVisibility(View.GONE);
            }else {

                mwebView.setVisibility(View.GONE);
                network.setVisibility(View.VISIBLE);
            }
            super.onPageCommitVisible(view, url);
        }

    }
    //goto previous page when pressing back button

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mwebView.canGoBack()) {
                        mwebView.goBack();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Close App")
                                .setMessage("Are you sure you want to exit from app?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }




    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //toggle.syncState();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
