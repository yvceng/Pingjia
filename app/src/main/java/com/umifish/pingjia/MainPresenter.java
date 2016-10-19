package com.umifish.pingjia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * 分离开的控制器
 * Created by lenovo on 2016/10/13.
 */

class MainPresenter {
    private String ida;
    private String token;
    private Handler UIHandler;
    private String ServerPath;
    private boolean restored;
    private SharedPreferences sp;
    private UpdateManager um;
    MainPresenter(Context ctx,String serverPath,Handler uiHandler)
    {
        sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
        ServerPath=serverPath;
        UIHandler =uiHandler;
        restored=false;
        um=new UpdateManager(ctx);
    }
    void checkUpdate()
    {
        um.checkUpdate();
    }
    boolean restorePermanentData()
    {
        //返回STRING_KEY的值
        token=sp.getString("token", "");
        //如果NOT_EXIST不存在，则返回值为""
        ida=sp.getString("ida", "");
        restored=sp.getBoolean("restored",false);
        Log.e("restored ","ida"+ida);
        if(restored)
        return true;
        else return false;
    }
    void savePermanentData()
    {
        //获取SharedPreferences对象
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.putString("ida",ida);
        editor.putBoolean("restored", true);
        editor.apply();
        Log.e("save ","ida"+ida);
    }
    void setToken(String t){
        token=t;
    }
    String getToken( ){return token;}
    void setIda(String id){
        ida=id;
    }
    String getIda(){return  ida;}
    void doLogin(String username,String password){
        Map<String, String> urlParams=new HashMap<>();
        String httpType="POST";
        String Url=ServerPath+"APPLoginReceive.php";
        urlParams.put("nn",username);
        urlParams.put("pp",password);
        urlParams.put("act","login");
        HttpURLConnectionTools conn=new HttpURLConnectionTools(ifHttpCallback,httpType,Url,urlParams);
        conn.start();
    }
    void doSuggesting(String tel,String suggestion){
        Map<String, String> urlParams=new HashMap<>();
        String httpType="POST";
        String Url=ServerPath+"APPComReceive.php";
        urlParams.put("token",token);
        urlParams.put("tel",tel);
        urlParams.put("sug",suggestion);
        urlParams.put("act","sug");
        HttpURLConnectionTools conn=new HttpURLConnectionTools(ifHttpCallback,httpType,Url,urlParams);
        conn.start();
    }
    void doEvaluating(int rank){
        Map<String, String> urlParams=new HashMap<>();
        String httpType="POST";
        String Url=ServerPath+"APPComReceive.php";
        urlParams.put("token",token);
        urlParams.put("rank", Integer.toString(rank));
        urlParams.put("act","sat");
        HttpURLConnectionTools conn=new HttpURLConnectionTools(ifHttpCallback,httpType,Url,urlParams);
        conn.start();
    }
    void doInPlace(int rank){
        Map<String, String> urlParams=new HashMap<>();
        String httpType="POST";
        String Url=ServerPath+"APPComReceive.php";
        urlParams.put("token",token);
        urlParams.put("rank", Integer.toString(rank));
        urlParams.put("act","inp");
        HttpURLConnectionTools conn=new HttpURLConnectionTools(ifHttpCallback,httpType,Url,urlParams);
        conn.start();
    }

    //http请求回调函数
    private HttpConnectInterface ifHttpCallback =new HttpConnectInterface(){
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
                        bd.putString("act", "log");
                        ida= json.getString("ida");
                        bd.putString("ida",ida);
                        token=json.getString("token");
                        bd.putString("token", token);
                        bd.putString("result", "success");
                    }else if( json.getString("act").equals("sat")) {//判断是否是评价操作
                        bd.putString("act", "sat");
                        bd.putString("result", "success");
                    }else if( json.getString("act").equals("sug")) {//判断是否是满意度建议操作
                        bd.putString("act", "sug");
                        bd.putString("result", "success");
                    }else if( json.getString("act").equals("inp")) {//判断是否是到位评价操作
                        bd.putString("act", "inp");
                        bd.putString("result", "success");
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
            UIHandler.sendMessage(msg);
            Log.e("return:",response);
        }};
    private void DisplayToast(String context){
        Message msg=new Message();
        Bundle bd=new Bundle();
        bd.putString("act", "toast");
        bd.putString("string", context);
        msg.setData(bd);
        UIHandler.sendMessage(msg);
    }
}
