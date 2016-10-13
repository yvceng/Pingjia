package com.umifish.pingjia;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

/**
 * 分离开的控制器
 * Created by lenovo on 2016/10/13.
 */

class MainPresenter {
    private String ida;
    private String token;
    private Handler UIHandler;
    private String ServerPath;
    MainPresenter(String serverPath,Handler uiHandler)
    {
        ServerPath=serverPath;
        UIHandler =uiHandler;
    }
    void setToken(String t){
        token=t;
    }
    void setIda(String id){
        ida=id;
    }
    void doEvaluating(int rank){
        DisplayToast("正在提交");
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
        DisplayToast("正在提交");
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
                        bd.putString("result", "success");
                    }else if( json.getString("act").equals("sat")) {//判断是否是评价操作
                        bd.putString("act", "sat");
                        bd.putString("result", "success");
                    }else if( json.getString("act").equals("sug")) {//判断是否是建议操作
                        bd.putString("act", "sug");
                        bd.putString("result", "success");
                    }else if( json.getString("act").equals("inp")) {//判断是否是建议操作
                        bd.putString("act", "sug");
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
