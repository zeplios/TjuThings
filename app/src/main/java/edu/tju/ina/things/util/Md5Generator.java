package edu.tju.ina.things.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Generator{
	
	public static void main(String [] args){
		System.out.println(Md5Generator.encodeByMd5("203@ina"));
	}
	
	public static String encodeByMd5(String str){
        MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
	        md5.reset();
	        md5.update(str.getBytes("utf-8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte [] digest = md5.digest();
		StringBuffer sb = new StringBuffer();
		for(byte b : digest){
			String text = Integer.toHexString(b & 0xFF);
			if(text.length() < 2){
				sb.append("0");
				sb.append(text);
			}
			else{
				sb.append(text);
			}
		}
        return sb.toString();
	}
}
