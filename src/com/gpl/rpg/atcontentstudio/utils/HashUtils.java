package com.gpl.rpg.atcontentstudio.utils;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.zackehh.siphash.SipHash;
import com.zackehh.siphash.SipHashResult;

public class HashUtils {

	private static final String WEBLATE_SIPASH_KEY = "Weblate Sip Hash";
	
	private static final Map<String, SipHash> HASHER_CACHE =  new LinkedHashMap<String, SipHash>();
	
	public static String weblateHash(String str, String ctx) {
		
		byte[] data = null;
		
		if (str != null) {
			byte[] strBytes;
			try {
				strBytes = str.getBytes("UTF-8");
				byte[] ctxBytes = ctx.getBytes("UTF-8");
				data = new byte[strBytes.length + ctxBytes.length];
				System.arraycopy(strBytes, 0, data, 0, strBytes.length);
				System.arraycopy(ctxBytes, 0, data, strBytes.length, ctxBytes.length);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		} else {
			try {
				data = ctx.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return siphash(WEBLATE_SIPASH_KEY, data);
	}
	
	private static String siphash(String key, byte[] data) {
		SipHash hasher = HASHER_CACHE.get(key); 
		if (hasher == null) {
			hasher= new SipHash("Weblate Sip Hash".getBytes());
			HASHER_CACHE.put(key, hasher);
		}
		
		if (data != null) {
			SipHashResult result = hasher.hash(data);
			return result.getHex();
		}
		
		
		return null;
		
	}
	
}
