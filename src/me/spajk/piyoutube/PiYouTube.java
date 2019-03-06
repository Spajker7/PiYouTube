package me.spajk.piyoutube;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class PiYouTube
{
	/*
	 * Static stuff
	 */
	
	public static final String UUID_FILE = "device.dat";
	public static final String CONFIG_FILE = "config.json";
	
	public static final String DEVICE_MANUFACTURER = "Spajk";
	public static final String DEVICE_MODEL = "PiYouTubePlayer v1.0.0";
	
	public static final int DIAL_PORT = 56789;
	
	private static PiYouTube instance;
	
	public static void main(String[] args)
	{
		if(instance == null)
		{
			instance = new PiYouTube();
			instance.run(args);
		}

	}
	
	public static PiYouTube getInstance()
	{
		return instance;
	}
	
	/*
	 * Actual program :)
	 */
	
	private UUID deviceUUID;
	private JSONObject config;
	
	private DIALServer dialServer;
	private ArrayList<SSDPServer> ssdpServers;

	private Player player;
	
	public void run(String[] args)
	{
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
		
		File uuidFile = new File(UUID_FILE);
		File configFile = new File(CONFIG_FILE);
		
		System.out.println("Starting Pi YouTube...");
		
		this.deviceUUID = this.getThisDeviceUUID(uuidFile);
		System.out.println("Loaded device UUID. UUID: " + this.deviceUUID);
		
		this.loadConfig(configFile);

		this.player = new BrowserPlayer(this.config);

		try
		{
			this.ssdpServers = new ArrayList<SSDPServer>();
			NetworkInterface iface = null;
			
			if(this.config.optString("iface", null) != null && (iface = NetworkInterface.getByName(this.config.getString("iface"))) != null)
			{
				this.ssdpServers.add(new SSDPServer(this.deviceUUID, iface, DIAL_PORT));
			}
			else
			{
				if(Util.isUnix())
				{
					System.out.println("Interface needs to be defined for Linux systems. Please edit config.json.");
					System.exit(0);
				}
				
				for(NetworkInterface iface2 : Collections.list(NetworkInterface.getNetworkInterfaces()))
				{
					if(iface2.isUp())
					{
						this.ssdpServers.add(new SSDPServer(this.deviceUUID, iface2, DIAL_PORT));
					}
				}
			}
			
			this.dialServer = new DIALServer(this, DIAL_PORT);
			this.dialServer.start();
			
			System.out.println("DIAL server started!");
			
			for(SSDPServer server : this.ssdpServers)
			{
				server.start();
			}
			
			System.out.println("SSDP server(s) started!");
			
			System.out.println("Pi YouTube ready to accept connections!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public Player getPlayer()
	{
		return this.player;
	}

	/*
	public void launchChromeDriver()
	{
		System.setProperty("webdriver.chrome.driver", this.config.getString("driver"));
		
		ChromeOptions chromeParams = new ChromeOptions();
		
		JSONArray options = this.config.getJSONArray("chromeParams");
		if(options != null)
		{
			for(int i = 0; i < options.length(); i++)
			{
				chromeParams.addArguments(options.getString(i));
			}
		}
		
		if(this.config.optString("chrome", null) != null)
		{
			chromeParams.setBinary(this.config.getString("chrome"));
		}
		
		try
		{
			if(! Util.isUnix())
			{
				this.chromeDriver = new ChromeDriver(chromeParams);
			}
			else
			{
				this.chromeDriver = new ChromeDriver(new ChromeDriverService.Builder().withEnvironment(ImmutableMap.of("DISPLAY",":0.0")).build(), chromeParams);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error loading chromedriver. Are you sure the correct path is set in config.json?");
			e.printStackTrace();
			System.exit(0);
		}
	}
	*/
	
	private UUID getThisDeviceUUID(File uuidFile)
	{
		UUID uuid = null;
		
		try
		{
			FileInputStream input = new FileInputStream(uuidFile);
			
			byte[] bytes = new byte[16];
			
			input.read(bytes);
			
			ByteBuffer bb = ByteBuffer.wrap(bytes);
			
			long msb = bb.getLong();
			long lsb = bb.getLong();
			
			uuid = new UUID(msb, lsb);
			
			input.close();
			
		}
		catch (IOException e)
		{
			uuid  = UUID.randomUUID();
			
			FileOutputStream output = null;

			try
			{
				output = new FileOutputStream(uuidFile);
				
				ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
				bb.putLong(uuid.getMostSignificantBits());
				bb.putLong(uuid.getLeastSignificantBits());
				
				output.write(bb.array());
				
				output.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
		return uuid;
	}
	
	
	public String getDeviceName()
	{
		return this.config.optString("name");
	}
	
	private void loadConfig(File configFile)
	{
		try
		{
			FileInputStream input = new FileInputStream(configFile);
			String configStr = Util.readWholeInputStream(input);
			input.close();
			
			this.config = Option.fillOptions(new JSONObject(configStr));
			
			System.out.println("Config loaded.");
		}
		catch (FileNotFoundException e)
		{
			this.config = Option.fillOptions(new JSONObject());
			
			System.out.println("Loading default config. Writing it to disk.");
			
			try
			{
				FileOutputStream output = new FileOutputStream(configFile);
				output.write(Option.getDefaultOptions().toString(4).getBytes());
				output.close();
				
			}
			catch (JSONException | IOException e1)
			{
				System.out.println("Error writing default config to disk.");
			}
		}
		catch (JSONException | IOException e)
		{
			this.config = Option.fillOptions(new JSONObject());
			System.out.println("Error while reading config, using default config.");
		}
	}
	
	public DIALServer getDialServer()
	{
		return this.dialServer;
	}
	public ArrayList<SSDPServer> getSsdpServers()
	{
		return this.ssdpServers;
	}
	
	public UUID getDeviceUUID()
	{
		return this.deviceUUID;
	}
	
	public JSONObject getConfig()
	{
		return this.config;
	}
}
