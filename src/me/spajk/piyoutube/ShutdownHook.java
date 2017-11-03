package me.spajk.piyoutube;

public class ShutdownHook extends Thread
{
	private PiYouTube piy;
	
	public ShutdownHook(PiYouTube piy)
	{
		this.piy = piy;
	}
	
	public void run()
	{
		if(this.piy.getChromeDriver() != null)
		{
			this.piy.getChromeDriver().quit();
		}
		
		if(this.piy.getDialServer() != null)
		{
			this.piy.getDialServer().stop();
		}
		
		if(this.piy.getSsdpServers() != null)
		{
			for(SSDPServer server : this.piy.getSsdpServers())
			{
				server.stopServer();
			}
		}
		
		System.out.println("Pi YouTube shutdown.");
	}
}
