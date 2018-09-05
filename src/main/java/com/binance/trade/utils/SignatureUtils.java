package com.binance.trade.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SignatureUtils {
	private static final String UTF8 = "utf-8";
	private static final String MD5 = "MD5";
	/**
	 * 执行加密加签
	 * @param map
	 * @param secret
	 * @return 加密加签字符串
	 */
	public static String encrypt(Map<String,String> map,String secret) {		
		String sortStr = SignatureUtils.signBody(map);
		String finalStr = sortStr +"&secret_key="+secret;
		return getMD5Str(finalStr);
	}
	/**
	 * HmacSHA256 加密
	 * @param map
	 * @param secret
	 * @return
	 */
	public static String enHmacSHA256(Map<String,String> map,String secret) {		
		String sortStr = SignatureUtils.signBody(map);
		String finalStr = sortStr +"&secret_key="+secret;
		return getHMAC_SHA256(finalStr,secret);
	}
	/**
	 * 排序并加签
	 * @param params
	 * @return
	 */
	public static String signBody(Map<String,String> params) {
		    
		List<String> paramKeys = new LinkedList(); 
		for(Map.Entry entry:params.entrySet()){
			if (entry.getValue() == null) {
				return null;
			}
			if ((entry.getValue() instanceof String) && ((String) entry.getValue()).isEmpty()) {
				return null;
			}
			paramKeys.add((String) entry.getKey());
		}
		Collections.sort(paramKeys);
		StringBuilder sb = new StringBuilder(100);
		for(String paramKey:paramKeys){
			sb.append(paramKey).append("=").append(params.get(paramKey)).append("&");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	private static String getHMAC_SHA256(String str,String secret) {
		String hash = "";
		
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");      
		    sha256_HMAC.init(secret_key);       
		    byte[] bytes = sha256_HMAC.doFinal(str.getBytes());   
		    hash = byteArrayToHexString(bytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			log.error("加密处理异常");
		}     
	     
		return hash;
		
	}
	
	/**
	 * 32位MD5加密
	 * @param str
	 * @return 加密字符串
	 */
	private static String getMD5Str(String str)  
    {  
        MessageDigest messageDigest = null;  
        try  
        {  
            messageDigest = MessageDigest.getInstance(MD5);  
            messageDigest.reset();  
            messageDigest.update(str.getBytes(UTF8));  
        } catch (NoSuchAlgorithmException e)  
        {  
            System.out.println("NoSuchAlgorithmException caught!");  
            System.exit(-1);  
        } catch (UnsupportedEncodingException e)  
        {  
            e.printStackTrace();  
        }  
  
        byte[] byteArray = messageDigest.digest();  
  
        StringBuffer md5StrBuff = new StringBuffer();  
  
        for (int i = 0; i < byteArray.length; i++)  
        {  
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
        }  
        return md5StrBuff.toString().toUpperCase();  
    }
	
	private static String byteArrayToHexString(byte[] b) {  
	      StringBuilder hs = new StringBuilder();     
	      String stmp;      
	      for (int n = 0; b!=null && n < b.length; n++) {   
	         stmp = Integer.toHexString(b[n] & 0XFF);    
	         if (stmp.length() == 1)           
	         hs.append('0');      
	         hs.append(stmp);    
	    }      
	   return hs.toString().toLowerCase();   
	 }   
    
}
