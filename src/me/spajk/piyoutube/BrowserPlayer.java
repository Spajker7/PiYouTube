package me.spajk.piyoutube;

import org.json.JSONObject;

import java.io.IOException;

public class BrowserPlayer extends Player
{
	private Process browserProcess;

	public BrowserPlayer(JSONObject config)
	{
		super(config);
	}

	@Override
	public void startPlayer(String body)
	{
		String url = "https://www.youtube.com/tv?" + body;

		String cmd = this.config.getString("command");
		cmd = cmd.replace("{url}", url);

		try
		{
			this.browserProcess = Runtime.getRuntime().exec(cmd);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void stopPlayer()
	{
		this.browserProcess.destroyForcibly();
	}
}
