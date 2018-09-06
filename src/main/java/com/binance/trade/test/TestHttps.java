package com.binance.trade.test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.binance.trade.utils.SignatureUtils;

public class TestHttps {
	
	public static void main(String[] args ) {
		String accountUrl = "https://api.binance.com/api/v3/account";
		String timeObj = getHttp("https://api.binance.com/api/v1/time","");
		if(StringUtils.isNotBlank(timeObj)) {
			timeObj = JSONObject.parseObject(timeObj).getString("serverTime");
		}
		Map<String,String> map = new HashMap<String,String>();
		String secretKey = "dhBqV9dgobHNUcMfrceVbKiFw7el7oTcb5phx2cpXBjMYXPZrnPCpYJ0VCn2QmkI";
		//map.put("api_key", "sSFs18IGntXcK0gkEQ7aQrsjGGOCBk379F3m02VNTaHxYcoFj4QOEGFBZDJSqcGA");
		map.put("recvWindow", "5000");
		map.put("timestamp", timeObj);///api/v1/time
		String msg = SignatureUtils.enHmacSHA256(map, secretKey);
		String reqMsg = "recvWindow=5000&timestamp=1499827319559&signature="+msg;
		System.out.println(msg);
		getHttp(accountUrl,reqMsg);
	}
	public static String getHttp(String url,String param) {

        String result = null ;

        InputStream in = null;

        try {

            Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1", Integer.valueOf("1080")));

            String urlNameString =url +"?" +param;

            URL realUrl = new URL(urlNameString);

            System.out.println(urlNameString);

            // 打开和URL之间的连接

            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection(proxy);

            // 设置通用的请求属性
            //connection.s
            //connection.setRequestProperty("Connection","keep-alive");
            connection.setRequestProperty("X-MBX-APIKEY", "sSFs18IGntXcK0gkEQ7aQrsjGGOCBk379F3m02VNTaHxYcoFj4QOEGFBZDJSqcGA");
            // 建立实际的连接

            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应

            in = connection.getInputStream();

            byte[]inByte =new byte[in.available()];

            in.read(inByte, 0,in.available());

            result = new String(inByte,"utf-8");

            System.out.println(result);

        } catch (Exception e) {

            System.out.println("发送GET请求出现异常！" + e);

            e.printStackTrace();

        }

        // 使用finally块来关闭输入流

        finally {

            try {

                if (in !=null) {

                    in.close();

                }

            } catch (Exception e2) {

                e2.printStackTrace();

            }

        }

        return result;

  }

}
