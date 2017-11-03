package me.spajk.piyoutube;

import org.json.JSONArray;
import org.json.JSONObject;

public class Option
{
	public static Option[] defaultOptionsArray = {
		new Option("name", Util.getDefaultDeviceName(), "Name of your device"),
		new Option("iface", "all", "Network interface from which connections are expected"),
		new Option("driver", "", "Path to ChromeDriver"),
		new Option("chrome", "auto", "Custom path to Chrome binary"),
		new Option("preloadchrome", false, "Should Chrome be always on"),
		new Option("chromeParams", new JSONArray("[\"--fullscreen\", \"--kiosk\",\"--disable-infobars\",\"--no-sandbox\"]"), "Should Chrome be always on")
	};
	
	public static JSONObject getDefaultOptions()
	{
		JSONObject def = new JSONObject();
		
		for(Option option : defaultOptionsArray)
		{
			def.put(option.getName(), option.getDefaultValue());
		}
		
		return def;
	}
	
	public static JSONObject fillOptions(JSONObject options)
	{
		for(Option option : defaultOptionsArray)
		{
			String name = option.getName();
			
			if(! options.has(name))
			{
				options.put(name, processValue(name, option.getDefaultValue()));
			}
			else
			{
				Object value = options.get(name);
				options.remove(name);
				options.put(name, processValue(name, value));
			}
		}
		
		return options;
	}
	
	public static Object processValue(String name, Object value)
	{
		if("iface".equals(name) && "all".equals(value))
		{
			if(Util.isUnix())
			{
				System.out.println("Interface needs to be defined for Linux systems. Please edit config.json.");
				System.exit(0);
			}
			
			value = null;
		}
		else if("chrome".equals(name) && "auto".equals(value))
		{
			value = null;
		}
		
		return value;
	}
	
	private String name;
	private Object defaultValue;
	private String desc;
	
	private Option(String name, Object defaultValue, String desc)
	{
		this.name = name;
		this.defaultValue = defaultValue;
		this.desc = desc;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Object getDefaultValue()
	{
		return this.defaultValue;
	}
	
	public String getDescription()
	{
		return this.desc;
	}
}
