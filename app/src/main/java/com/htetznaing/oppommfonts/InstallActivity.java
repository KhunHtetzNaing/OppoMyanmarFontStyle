package com.htetznaing.oppommfonts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstallActivity extends AppCompatActivity implements View.OnClickListener {
    String name,font,path;
    Button btnInstall,btnChange,btnChangeV3;
    AdRequest adRequest;
    AdView banner;
    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);
        name = getIntent().getStringExtra("name");
        font = getIntent().getStringExtra("font");
        path = Environment.getExternalStorageDirectory()+"/Android/data/"+getPackageName()+"/";
        createDir();
        setTitle(name);

        btnChange = findViewById(R.id.btnChange);
        btnInstall = findViewById(R.id.btnInstall);
        btnChangeV3 = findViewById(R.id.btnChangeV3);
        btnInstall.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        btnChangeV3.setOnClickListener(this);

        adRequest = new AdRequest.Builder().build();
        banner = findViewById(R.id.adView);
        banner.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-2780984156359274/1028443584");
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                loadAD();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                loadAD();
            }

            @Override
            public void onAdOpened() {
                loadAD();
            }
        });
    }

    public void loadAD(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(adRequest);
        }
    }

    public void showAD(){
        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }else{
            interstitialAd.loadAd(adRequest);
        }
    }

    public boolean createDir(){
        boolean b = false;
        File file =new File(path);
        file.mkdirs();
        if (file.exists()){
            b=true;
        }

        return b;
    }

    public boolean assets2SD(Context context, String inputFileName, String OutputDir, String OutputFileName) {
        boolean lol = false;
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            try {
                in = assetManager.open(inputFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = new FileOutputStream(OutputDir + OutputFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(OutputDir+OutputFileName);
        if (file.exists()!=false){
            lol=true;
        }else{
            lol=false;
        }
        return lol;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnInstall:
                install(font);
                break;
            case R.id.btnChange:
                changeFont();
                break;
            case R.id.btnChangeV3:
                v3();
                break;
        }
    }

    public void install(final String font){
        if (createDir()==true) {
            boolean b = assets2SD(this, font, path, font);
            if (b == true) {
                View view = getLayoutInflater().inflate(R.layout.change,null);
                WebView webView = view.findViewById(R.id.webview);
                webView.loadUrl("file:///android_asset/i.html");
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Attention!")
                        .setView(view)
                        .setPositiveButton("Install Font", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showAD();
                                File toInstall = new File(path+ font);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Uri apkUri = Uri.fromFile(toInstall);
                                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                    intent.setData(apkUri);
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intent);
                                } else {
                                    Uri apkUri = Uri.fromFile(toInstall);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });
                AlertDialog dialog =builder.create();
                dialog.show();
            } else {
                showAD();
                Toast.makeText(this, "Write Storage Permission Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changeFont(){
        View view = getLayoutInflater().inflate(R.layout.change,null);
        WebView webView = view.findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/change.html");
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Attention!")
                .setView(view)
                .setPositiveButton("Change Font", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       showAD();
                        Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(InstallActivity.this, "Font > "+name+"(iFont) > Apply :)", Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog dialog =builder.create();
        dialog.show();

    }

    public void v3(){
        if (ogay()==true){
            View view = getLayoutInflater().inflate(R.layout.change,null);
            WebView webView = view.findViewById(R.id.webview);
            webView.loadUrl("file:///android_asset/v3already.html");
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Attention!")
                    .setView(view)
                    .setPositiveButton("Change Font", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showAD();
                            Intent intent = getPackageManager().getLaunchIntentForPackage("com.nearme.themespace");
                            startActivity(intent);
                        }
                    });
            AlertDialog dialog =builder.create();
            dialog.show();
        }else{
            showAD();
            startActivity(new Intent(this,ColorOSv3.class));
        }
    }

    public boolean ogay(){
        boolean b =false;
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo("com.nearme.themespace", 0);
            int version = packageInfo.versionCode;
            Log.d("ThemeVersion",String.valueOf(version));
            if (version>=463){
                b=true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }
}
