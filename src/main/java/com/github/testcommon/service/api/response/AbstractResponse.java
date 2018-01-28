package com.github.testcommon.service.api.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractResponse
{
	public AbstractResponse()
	{
	}

	public void print()
	{
		System.out.println("Response:\n "+ this.toString());
	}
	
	public String toString()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
}
