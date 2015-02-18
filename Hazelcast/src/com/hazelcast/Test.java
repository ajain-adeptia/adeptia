package com.hazelcast;


public class Test {

	public static void main(String[] args) throws Exception {
		// CachingProvider cacheManager = Caching.getCachingProvider();
		// CacheManager cm = cacheManager.getCacheManager();
		//
		// HazelcastInstance hz = Hazelcast.newHazelcastInstance();
		// Map<String, String> map = hz.getMap("map");
		// map.put("1", "Tokyo");
		// map.put("2", "Paris");
		// map.put("3", "New York");
		// System.out.println("Finished loading map");

		System.out.println(AppCacheManager.getInstance().get("x"));
	}

}
