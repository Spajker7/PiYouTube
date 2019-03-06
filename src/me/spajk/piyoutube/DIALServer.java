package me.spajk.piyoutube;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
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

				switch (method)
				{
					case "GET":
						t.getResponseHeaders().add("Content-Type", "text/xml");
						t.sendResponseHeaders(200, 0);

						StringBuilder stringBuilder = new StringBuilder();

						stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");

						stringBuilder.append("<service xmlns=\"urn:dial-multiscreen-org:schemas:dial\">\r\n");
						stringBuilder.append("  <name>YouTube</name>\r\n");
						stringBuilder.append("  <options allowStop=\"").append(youtubeAllowStop).append("\"/>\r\n");
						stringBuilder.append("  <state>").append(this.youtubeState.toString()).append("</state>\r\n");

						if (!this.youtubeData.isEmpty())
						{
							stringBuilder.append("  <additionalData xmlns=\"http://www.youtube.com/dial\">\r\n");

							for (Map.Entry<String, String> entry : this.youtubeData.entrySet())
							{
								stringBuilder.append("    <").append(entry.getKey()).append(">").append(entry.getValue()).append("</").append(entry.getKey()).append(">\r\n");
							}

							stringBuilder.append("  </additionalData>\r\n");
						}

						stringBuilder.append("</service>");

						OutputStream os = t.getResponseBody();

						os.write(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
						os.close();
						break;
					case "POST":
						String body = Util.readWholeInputStream(t.getRequestBody());
						t.getResponseHeaders().add("Content-Type", "text/plain");
						t.getResponseHeaders().add("Location", "http://" + t.getLocalAddress().getAddress().getHostAddress() + ":" + this.port + "/apps/YouTube/run");

						t.sendResponseHeaders(201, 0);

						t.getResponseBody().close();

						String params = URLDecoder.decode(body, StandardCharsets.UTF_8.name());

						this.piy.getPlayer().startPlayer(params);

						this.youtubeState = State.RUNNING;
						break;
					case "DELETE":
						if (command != null && command.equals("run"))
						{
							this.piy.getPlayer().stopPlayer();

							this.youtubeState = State.STOPPED;
							t.sendResponseHeaders(200, 0);
						}
						break;
				}
			}
		}
	}
}
