package com.hazelcast;

import java.util.Iterator;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class TestClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientConfig clientConfig = new ClientConfig();
		HazelcastInstance client = HazelcastClient
				.newHazelcastClient(clientConfig);
		IMap map = client.getMap("map");
		System.out.println("Map Size:" + map.size());
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			String value = (String) map.get(type);
			System.out.println(type + " --> " + value);

		}
		// map.put("1", "Test CCCC");
		//
		// map.put("12", "Test CCCC");
	}
}
