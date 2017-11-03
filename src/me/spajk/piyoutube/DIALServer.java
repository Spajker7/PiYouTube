package me.spajk.piyoutube;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class DIALServer implements HttpHandler
{
	public static final int DIAL_DATA_SIZE = 8*1024;
	
	private enum State {
		HIDDEN("hidden"), 
		RUNNING("running"), 
		STOPPED("stopped");
	
		private String str;
		
		private State(String str)
		{
			this.str = str;
		}
		
		public String toString()
		{
			return this.str;
		}
	};
	
	private int port;
	
	private State youtubeState;
	private boolean youtubeAllowStop;
	
	private HashMap<String, String> youtubeData = new HashMap<String, String>();
	
	private PiYouTube piy;
	private HttpServer server;
	
	public DIALServer(PiYouTube piy, int port)
	{
		this.piy = piy;
		this.port = port;
		this.youtubeState = State.STOPPED;
		this.youtubeAllowStop = true;
	}
	
	public void start() throws IOException
	{
		this.server = HttpServer.create(new InetSocketAddress(this.port), 0);
		this.server.createContext("/", new DIALServer(this.piy, this.port));
		this.server.setExecutor(null);
		this.server.start();
	}
	
	public void stop()
	{
		this.server.stop(0);
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException
	{
		String path = t.getRequestURI().getPath();
		String method = t.getRequestMethod();
		
		if(path.equals("/dd.xml") && method.equals("GET"))
		{
			t.getResponseHeaders().add("Content-Type", "text/xml");
			t.getResponseHeaders().add("Application-URL", "http://"+ t.getLocalAddress().getAddress().getHostAddress() + ":" + this.port + "/apps/");
			
			t.sendResponseHeaders(200, 0);
			
			String name =  this.piy.getDeviceName();
			String manufacturer = PiYouTube.DEVICE_MANUFACTURER;
			String model =  PiYouTube.DEVICE_MODEL;
			
			String response = 	"<?xml version=\"1.0\"?>" + 
								"<root" + 
								" xmlns=\"urn:schemas-upnp-org:device-1-0\"" + 
								" xmlns:r=\"urn:restful-tv-org:schemas:upnp-dd\">" + 
								" <specVersion>" + 
								" <major>1</major>" + 
								" <minor>0</minor>" + 
								" </specVersion>" + 
								" <device>" + 
								" <deviceType>urn:schemas-upnp-org:device:tvdevice:1</deviceType>" + 
								" <friendlyName> " + name + "</friendlyName>" + 
								" <manufacturer>" + manufacturer + "</manufacturer>" + 
								" <modelName>" + model + "</modelName>" + 
								" <UDN>uuid:" + this.piy.getDeviceUUID().toString() + "</UDN>" + 
								" </device>" + 
								"</root>";
			
			OutputStream os = t.getResponseBody();
			
			os.write(response.getBytes(StandardCharsets.US_ASCII));
			os.close();
		}
		else if(path.startsWith("/apps/"))
		{
			String appname = path.substring(6);
			String command = null;
			
			int slash = appname.indexOf('/');
			if(slash > -1)
			{
				command = appname.substring(slash + 1);
				appname = appname.substring(0, slash);
			}
			
			if(appname.equals("YouTube"))
			{
				System.out.println("YouTube METHOD: " + method + " COMMAND: " + command);
				
				if(method.equals("GET"))
				{
					t.getResponseHeaders().add("Content-Type", "text/xml");
					t.sendResponseHeaders(200, 0);
					
					String response = 	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
					
					response += "<service xmlns=\"urn:dial-multiscreen-org:schemas:dial\">\r\n";
					response += "  <name>YouTube</name>\r\n";
					response += "  <options allowStop=\"" + youtubeAllowStop + "\"/>\r\n";
					response += "  <state>" + this.youtubeState.toString() + "</state>\r\n";
					
					if(! this.youtubeData.isEmpty())
					{
						response += "  <additionalData xmlns=\"http://www.youtube.com/dial\">\r\n";
						
						for(Map.Entry<String, String> entry : this.youtubeData.entrySet())
						{
							response += "    <" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">\r\n";
						}
						
						response += "  </additionalData>\r\n";
					}
					
					response += "</service>";
					
					OutputStream os = t.getResponseBody();
					
					os.write(response.getBytes(StandardCharsets.US_ASCII));
					os.close();
				}
				else if(method.equals("POST"))
				{
					String body = Util.readWholeInputStream(t.getRequestBody());
					t.getResponseHeaders().add("Content-Type", "text/plain");
					t.getResponseHeaders().add("Location", "http://" + t.getLocalAddress().getAddress().getHostAddress() + ":" + this.port + "/apps/YouTube/run");
					
					t.sendResponseHeaders(201, 0);
					
					t.getResponseBody().close();
					
					body = java.net.URLDecoder.decode(body, "UTF-8");
					
					String url = "https://www.youtube.com/tv?" + body;
					
			        this.piy.getChromeDriver().get(url);
					
			        this.youtubeState = State.RUNNING;
				}
				else if(method.equals("DELETE"))
				{
					if(command.equals("run"))
					{
						this.piy.getChromeDriver().get("");
						this.youtubeState = State.STOPPED;
						t.sendResponseHeaders(200, 0);
					}
				}
			}
		}
	}
}
