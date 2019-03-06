package me.spajk.piyoutube;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

public class SSDPServer extends Thread
{
	private boolean shouldStop = false;
	
	private UUID deviceUUID;
	private int dialPort;
	
	private NetworkInterface iface;
	
	public SSDPServer(UUID deviceUUID, NetworkInterface iface, int dialPort)
	{
		this.deviceUUID = deviceUUID;
		this.dialPort = dialPort;
		this.iface = iface;
		
		this.setName("SSDP Server - " + iface.getName());
	}

	public void run()
	{
		try
		{
			InetAddress ifaceAddress = null;
			
			for(InetAddress address : Collections.list(this.iface.getInetAddresses()))
			{
				if(address instanceof Inet4Address)
				{
					ifaceAddress = address;
					break;
				}
			}
			
			if(ifaceAddress != null)
			{
				MulticastSocket serverSocket = new MulticastSocket(new InetSocketAddress(ifaceAddress, 1900));
				
				if(Util.isUnix())
				{
					serverSocket.close();
					serverSocket = new MulticastSocket(1900); // TODO receive won't work if address isn't 0.0.0.0
				}
				
				serverSocket.joinGroup(InetAddress.getByName("239.255.255.250"));
				
				byte receiveData[] = new byte[4096];
				
				while(! this.shouldStop)
				{
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					try
					{
						serverSocket.receive(receivePacket);
						
						String ssdpMessage = new String(receiveData, StandardCharsets.US_ASCII);
						
						// "sophisticated" SSDP parsing algorithm 
						// https://github.com/Netflix/dial-reference/blob/master/server/quick_ssdp.c#L199
						if(ssdpMessage.contains("urn:dial-multiscreen-org:service:dial:1"))
						{
							String ssdp_reply = "HTTP/1.1 200 OK\r\n" +
									"LOCATION: http://" + ifaceAddress.getHostAddress() + ":" + this.dialPort + "/dd.xml\r\n" +
									"CACHE-CONTROL: max-age=1800\r\n" +
									"EXT:\r\n" +
									"BOOTID.UPNP.ORG: 1\r\n" +
									"SERVER: Linux/2.6 UPnP/1.1 quick_ssdp/1.1\r\n" +
									"ST: urn:dial-multiscreen-org:service:dial:1\r\n" +
									"USN: uuid:" + this.deviceUUID.toString()  + "::" +
									"urn:dial-multiscreen-org:service:dial:1\r\n" +
									"\r\n";
							
							byte message[] = ssdp_reply.getBytes(StandardCharsets.US_ASCII);
							
							serverSocket.send(new DatagramPacket(message, message.length, receivePacket.getAddress(), receivePacket.getPort()));
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
		        }
				
				serverSocket.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public NetworkInterface getNetworkInterface()
	{
		return this.iface;
	}
	
	public void stopServer()
	{
		this.shouldStop = true;
	}
}
