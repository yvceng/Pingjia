package com.umifish.pingjia;

import android.net.Uri;
import android.util.Log;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import static java.net.Proxy.Type.HTTP;

public class HttpURLConnectionTools  extends Thread {

    private Map<String, String> urlParams;
    private String httpType;
    private String Url;
    private HttpConnectInterface connectInterface;
    HttpURLConnectionTools(HttpConnectInterface cb,String pHttpType,String url,Map<String, String> pUrlParams)
    {
        urlParams=pUrlParams;
        httpType=pHttpType.toUpperCase();
        Url=url;
        connectInterface=cb;
    }
    @Override
    public void run() {
        String result = "";
        if (httpType.equals("POST")) {
            result = obtainPostUrlContext(Url, urlParams);
        } else if (httpType.equals("GET")){
            result = obtainGetUrlContext(Url, urlParams);
        }else{
            result="Type should be POST or GET.";
        }
        connectInterface.httpCallback(result);
    }


    /**
     * 通过GET获取某个url的内容
     *
     * @param strUrl
     * @param params
     * @return String
     */
    public static String obtainGetUrlContext(String strUrl,
                                             Map<String, String> params) {
        String responseStr = "";
        InputStream is = null;
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        try {
            Log.e(HttpTools.TAG, getRequestUrl(strUrl, params));
            URL url = new URL(getRequestUrl(strUrl, params));
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(HttpTools.ReadOutTime /* milliseconds */);
            conn.setConnectTimeout(HttpTools.ConnectOutTime /* milliseconds */);
            conn.setRequestMethod(HttpTools.GET);
            conn.setDoInput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
// is = conn.getInputStream();
// responseStr = readInputStream(is);
                bufferedReader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseStr += line;
                }
            }
        } catch (Exception e) {
            Log.e(HttpTools.TAG, "obtainGetUrlContext is err");
// e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    conn.disconnect();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseStr;
    }

    /**
     * 通过POST获取某个url的内容
     *
     * @param strUrl 调用的url
     * @param params 参数
     * @return String
     */
    public static String obtainPostUrlContext(String strUrl,
                                              Map<String, String> params) {
        String responseStr = "";
        PrintWriter printWriter = null;
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        StringBuffer paramsBuffer=PostRequestUrl(params);
        try {
            URL url = new URL(strUrl);
// 打开和URL之间的连接
            conn = (HttpURLConnection) url.openConnection();
// 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(paramsBuffer.toString().getBytes().length));
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestMethod("POST");
// 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
// 获取URLConnection对象对应的输出流
             printWriter= new PrintWriter(conn.getOutputStream());
// 发送请求参数ew OutputStream(
            //printWriter.write(android.net.Uri.encode(URLEncoder.encode(paramsBuffer.toString(), "utf-8")));

            printWriter.write(paramsBuffer.toString());
// flush输出流的缓冲
            printWriter.flush();
// 根据ResponseCode判断连接是否成功
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                Log.e(HttpTools.TAG, "data is err");
            } else {
// responseStr = readInputStream(conn// .getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(),Charset.forName("UTF-8")));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseStr += line;
                }
            }
        } catch (Exception e) {
            Log.e(HttpTools.TAG, "obtainPostUrlContext is err");
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return responseStr;
    }

    /**
     * 拼装get访问URL
     *
     * @param strUrl
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getRequestUrl(String strUrl,
                                        Map<String, String> params) {
        String requestUrl = strUrl;
        int i = 0;
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (i == 0) {
                requestUrl += "?";
            } else {
                requestUrl += "&";
            }
            try {
                if ((null == param.getValue()) || "".equals(param.getValue())) {
                    requestUrl += param.getKey() + "=";
                } else {
                    requestUrl += param.getKey() + "="
                            +param.getValue();
// +param.getValue();
                }
            } catch (Exception e) {
                Log.e(HttpTools.TAG, "getRequestUrl第" + i + "个数据 is err");
// e.printStackTrace();
            }
            i++;
        }
        return requestUrl;
    }

    /**
     * 拼装POST访问URL
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("rawtypes")
    private static StringBuffer PostRequestUrl(Map<String, String> params) {
        StringBuffer buffers = new StringBuffer();
        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry element = (Map.Entry) it.next();
            buffers.append(element.getKey());
            buffers.append("=");
            try {
                //buffers.append(URLEncoder.encode(element.getValue().toString(), "UTF-8"));
                buffers.append(element.getValue().toString());
            }catch (Exception e) {

            }
            buffers.append("&");
        }
        if (buffers.length() > 0) {
            buffers.deleteCharAt(buffers.length() - 1);
        }
        Log.e("PostRequestUrl.Param:",buffers.toString());
        return buffers;
    }
}
