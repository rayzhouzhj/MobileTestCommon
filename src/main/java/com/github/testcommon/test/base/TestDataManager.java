package com.github.testcommon.test.base;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.testcommon.test.base.data.UserData;
import com.github.testcommon.test.base.data.Users;
import com.github.testcommon.test.utils.ResourceReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rayzhou.framework.context.TestingDevice;

public class TestDataManager 
{
	private static AtomicInteger index = new AtomicInteger();
	private static List<UserData> userDataList;
	private static ConcurrentMap<String, UserData> deviceDataMap = new ConcurrentHashMap<>();

	private TestDataManager(){}

	static
	{
		String dataStr = ResourceReader.readResource("resources" + File.separator + "userdata.json");
		Gson gson = new GsonBuilder().create();
		userDataList = gson.fromJson(dataStr, Users.class).getUserdata();
		
		System.out.println("[INFO] Test Data Initialization successfully.");
	}

	public static synchronized UserData getUserData()
	{
		String device = TestingDevice.getDeviceUDID();
		
		if(!deviceDataMap.keySet().contains(device))
		{
			if(index.get() == userDataList.size())
			{
				System.err.println("No enough user data for testing.");
				throw new IllegalArgumentException("No enough user data for testing.");
			}

			deviceDataMap.put(device, userDataList.get(index.get()));

			index.getAndIncrement();
		}

		return deviceDataMap.get(device);
	}
	
	public static synchronized UserData getUserData(String device)
	{
		if(!deviceDataMap.keySet().contains(device))
		{
			if(index.get() == userDataList.size())
			{
				System.err.println("No enough user data for testing.");
				throw new IllegalArgumentException("No enough user data for testing.");
			}

			deviceDataMap.put(device, userDataList.get(index.get()));

			index.getAndIncrement();
		}

		return deviceDataMap.get(device);
	}
}
