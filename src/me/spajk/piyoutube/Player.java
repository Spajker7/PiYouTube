package me.spajk.piyoutube;

import org.json.JSONObject;

public abstract class Player
{
	protected JSONObject config;

	public Player(JSONObject config)
	{
		this.config = config;
	}

	public abstract void startPlayer(String body);
	public abstract void stopPlayer();
}
