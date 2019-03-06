package me.spajk.piyoutube;

import org.json.JSONObject;

public class Option
{
	public static Option[] defaultOptionsArray = {
		new Option("name", Util.getDefaultDeviceName(), "Name of your device"),
		new Option("iface", "all", "Network interface from which connections are expected"),
		new Option("command", "", "Command to execute to open browser"),
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
			value = null;
		}
		else if("chrome".equals(name) && "auto".equals(value))
		{
			value = null;
		}
		
		if(value == null)
		{
			value = JSONObject.NULL;
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
