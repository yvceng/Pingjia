package com.umifish.pingjia;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private WebView mWebView;

    AlertDialog.Builder loginDlg;
    AlertDialog.Builder suggestDlg;
    EditText etUserName;
    EditText etPassword;
//    EditText etAdviserName;
//    EditText etAdviserTel;
//    EditText etSuggestion;
    ImageView imView;

    public String ida;
    public String token;
    LayoutInflater loginInflater;
    LayoutInflater suggestInflater;
    View loginDialogView;
    View suggestDialogView;
    public UIHandler UIhandler=new UIHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mContentView = findViewById(R.id.main_layout);
        imView = (ImageView) findViewById(R.id.imageView);
        mWebView = (WebView)findViewById(R.id.user_info_WebView);

        DisplayUserInfo("");
        DisplayPortrait("");
        //登陆对话框
        loginDlg = new AlertDialog.Builder(MainActivity.this);
        loginInflater = LayoutInflater.from(MainActivity.this);
        loginDialogView = loginInflater.inflate(R.layout.activity_login, null,false);
        suggestDlg = new AlertDialog.Builder(MainActivity.this);
        suggestInflater = LayoutInflater.from(MainActivity.this);
        suggestDialogView = loginInflater.inflate(R.layout.activity_suggest, null);
        LoginDialog();
        SuggestDialog();
    }
//登陆按钮
    public void OnLoginBtnClick(View V) {
        //判断之前是否已加载到父窗口，已加载要先去掉
        if(loginDialogView.getParent()!=null)
            ((ViewGroup) loginDialogView.getParent()).removeView(loginDialogView);
        loginDlg.show();
    }
    //不到位按钮
        public void OnNotInPlaceBtnClick(View V)
    {
        if(suggestDialogView.getParent()!=null)
            ((ViewGroup) suggestDialogView.getParent()).removeView(suggestDialogView);
        suggestDlg.show();
    }
    //不满意按钮
    public void OnSatisfactionBtn0Click(View V)
    {
        //判断之前是否已加载到父窗口，已加载要先去掉
        if(suggestDialogView.getParent()!=null)
            ((ViewGroup) suggestDialogView.getParent()).removeView(suggestDialogView);
        suggestDlg.show();
    }
    public void OnInPlaceBtnClick(View V)
    {
        DisplayToast("正在提交评价");
    }
    public void OnSatisfactionBtn2Click(View V)
    {
        DisplayToast("正在提交评价");
    }
    public void OnSatisfactionBtn1Click(View V)
    {
        DisplayToast("正在提交评价");
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //提取显示用户信息网页到webView
    public void DisplayUserInfo(String str) {
        if(str==null||str.equals("")) str="0";
        mWebView.loadUrl(getString(R.string.ServerPath)+"APPShowInfo.php?id="+str);
    }
    /* 显示头像  */
    //异步请求网络图片到imageView
    public void DisplayPortrait(String str) {
        if(str==null||str.equals("")) str="default";
        String imageUrl = getString(R.string.ServerPath)+"photo/"+str+".jpg";
        new DownLoadImage((ImageView) findViewById(R.id.imageView)).execute(imageUrl);
    }
    /* 显示Toast  */
    public void DisplayToast(String str)
    {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
        //设置toast显示的位置
        toast.setGravity(Gravity.TOP, 0, 220);
        //调整字体大小
        LinearLayout linearLayout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(25);
        //显示该Toast
        toast.show();
    }
    //登陆对话框
    public void LoginDialog() {
        etUserName = (EditText) loginDialogView.findViewById(R.id.etUserName);//注意这个地方mydialogview这个一定要加，否则指针不正确，会在读它的时候异常退出
        etPassword = (EditText) loginDialogView.findViewById(R.id.etPassword);

        loginDlg.setTitle("登陆账号")
                .setView(loginDialogView)//在这一步实现了和资源文件中的mylogindialog的关联
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        hide();
                    }
                })
                .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                        DisplayToast("正在验证账号密码");
                        Map<String, String> urlParams=new HashMap<>();
                        String httpType="POST";
                        String Url=getString(R.string.ServerPath)+"APPLoginReceive.php";
                        urlParams.put("nn",etUserName.getText().toString());
                        urlParams.put("pp",etPassword.getText().toString());
                        urlParams.put("act","login");
                        HttpURLConnectionTools conn=new HttpURLConnectionTools(ifHttpCallback,httpType,Url,urlParams);
                        conn.start();
                        hide();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        DisplayToast("取消登陆");
                        hide();
                    }
                });
        loginDlg.create();
    }

    //建议对话框
    public void SuggestDialog() {
        final EditText etAdviserTel = (EditText) suggestDialogView.findViewById(R.id.etAdviserTel);
        final EditText etSuggestion = (EditText) suggestDialogView.findViewById(R.id.etSuggestion);

        suggestDlg.setTitle("提交您的建议")
                .setView(suggestDialogView)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        hide();
                    }
                })
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                        DisplayToast("正在提交");
                        Map<String, String> urlParams=new HashMap<>();
                        String httpType="POST";
                        String Url=getString(R.string.ServerPath)+"APPSuggestReceive.php";
                        urlParams.put("token",token);
                        urlParams.put("tel",etAdviserTel.getText().toString());
                        urlParams.put("sug",etSuggestion.getText().toString());
                        urlParams.put("act","login");
                        HttpURLConnectionTools conn=new HttpURLConnectionTools(ifHttpCallback,httpType,Url,urlParams);
                        conn.start();
                        hide();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        DisplayToast("取消建议");
                        hide();
                    }
                });
        loginDlg.create();
    }
    protected void onResume() {

        hide();//需要重新隐藏
        super.onResume();
    }
    private static class UIHandler extends Handler{
        private WeakReference<MainActivity> main;
        UIHandler(MainActivity activity){
           main =new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String act=bundle.getString("act");
            if(act!=null&&act.equals("updateIDa")){
                String ida=bundle.getString("ida");
                Log.d("message get ida:",ida);
                main.get().ida = ida;
                main.get().token= bundle.getString("ida");
                main.get().DisplayPortrait(ida);
                main.get().DisplayUserInfo(ida);
            }
        }
    }
    //http请求回调函数
    HttpConnectInterface ifHttpCallback =new HttpConnectInterface(){
    @Override
    public void httpCallback(String response) {
        Message msg=new Message();
        Bundle bd=new Bundle();
        if(response==null||response.equals("")) {
            Log.e("httpCallback。response：", "null");
            return;
        }
        //解析json
        Log.d("httpCallback。response：",response);
        try {
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject json = (JSONObject) jsonParser.nextValue();
            // 网络请求成功
            if( json.getString("result").equals("success")) {

                if( json.getString("act").equals("login")) {//判断是否是登陆操作
                    bd.putString("act", "updateIDa");
                    bd.putString("ida", json.getString("ida"));
                    bd.putString("token", json.getString("token"));
                }else if( json.getString("act").equals("sat")) {//判断是否是评价操作
                    bd.putString("act", "sat");
                }else if( json.getString("act").equals("sug")) {//判断是否是建议操作
                    bd.putString("act", "sug");
                 }else{
                    return;
                }
            }else{
                //网络请求不成功
                bd.putString("act","netError");
            }
        } catch (JSONException ex) {
            // 异常处理代码
            Log.e("httpCallback","json parser error");
            ex.printStackTrace();
            return;
        }
        msg.setData(bd);
        UIhandler.sendMessage(msg);
        Log.e("return:",response);
    }};
}