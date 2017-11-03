package me.spajk.piyoutube;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

public class Util
{
	public static String readWholeInputStream(InputStream stream)
	{
		return new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
	}
	
	public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException 
	{
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) 
		{
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}
	
	public static String macAddressToString(byte[] mac)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
		}
		
		return sb.toString();
	}
	
	public static String getJSONFieldStringOrNull(JSONObject object, String field)
	{	
		try
		{
			return object.getString(field);
		}
		catch(JSONException e)
		{
			return null;
		}
	}
	
	public static boolean isUnix() {
		String os = System.getProperty("os.name");
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 );
    }
	
	public static String getDefaultDeviceName()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch(Exception e) 
		{ 
			return "PiYouTubePlayer";
		}
	}
}
