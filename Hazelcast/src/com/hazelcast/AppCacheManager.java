package com.hazelcast;

import java.io.FileNotFoundException;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.Hazelcast;

/**
 * Singleton class to manage the application cache. Hazelcast is used to provide
 * the caching implementation.
 * 
 * Nov 13, 2014
 * 
 * @author aman
 */

public class AppCacheManager {

	private final CacheManager cacheManager;

	private static volatile AppCacheManager appCacheManager = null;

	public static final String DEFAULT_CACHE_NAME = "AdeptiaConnectCacheMemory";

	private AppCacheManager() {
		try {
			Hazelcast
					.newHazelcastInstance(new FileSystemXmlConfig(
							"C:\\Users\\Aman Jain\\workspace\\AdeptiaConnectWeb\\resources\\hazelcast.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
		cacheManager = Caching.getCachingProvider().getCacheManager();
		addCache(DEFAULT_CACHE_NAME);
	}

	public static AppCacheManager getInstance() {
		if (appCacheManager == null) {
			synchronized (AppCacheManager.class) {
				// double check - to ensure thread safety while generating
				// single instance of this class
				if (appCacheManager == null) {
					appCacheManager = new AppCacheManager();
				}
			}
		}
		return appCacheManager;
	}

	public Cache getCache(String cacheName) throws Exception {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			throw new Exception("Cache \"" + cacheName + "\" doesnt exist!");
		}
		return cache;
	}

	public Cache getDefaultCache() throws Exception {
		return getCache(DEFAULT_CACHE_NAME);
	}

	/**
	 * Method to put an object in the cache
	 * 
	 */
	public void put(String cacheName, Object id, Object object)
			throws Exception {
		getCache(cacheName).put(id, object);
	}

	public void put(Object id, Object object) throws Exception {
		put(DEFAULT_CACHE_NAME, id, object);
	}

	/**
	 * Method to put collection of elements in the cache
	 * 
	 */
	public void putAll(String cacheName, Map<Object, Object> elements)
			throws Exception {
		getCache(cacheName).putAll(elements);
	}

	public void putAll(Map<Object, Object> elements) throws Exception {
		putAll(DEFAULT_CACHE_NAME, elements);
	}

	/**
	 * Method to return object from the cache for the corresponding identifier
	 * 
	 */
	public Object get(String cacheName, Object id) throws Exception {
		Cache cache = getCache(cacheName);
		Object objectValue = cache.get(id);
		return objectValue;
	}

	public Object get(Object id) throws Exception {
		return get(DEFAULT_CACHE_NAME, id);
	}

	/**
	 * Method to remove object from cache if present in cache.
	 * 
	 */
	public boolean remove(String cacheName, Object id) throws Exception {
		return getCache(cacheName).remove(id);
	}

	public boolean remove(Object id) throws Exception {
		String cacheName = DEFAULT_CACHE_NAME;
		if (isCached(cacheName, id)) {
			return getCache(cacheName).remove(id);
		}
		return false;
	}

	/**
	 * Method to check the presence of an object in the cache
	 * 
	 */
	public boolean isCached(String cacheName, Object id) throws Exception {
		return getCache(cacheName).containsKey(id);
	}

	/**
	 * Method to reset (empty) the specified cache
	 */
	public void reset(String cacheName) throws Exception {
		getCache(cacheName).removeAll();
	}

	public void reset() throws Exception {
		reset(DEFAULT_CACHE_NAME);
	}

	public void addCache(String cacheName) {
		cacheManager.createCache(cacheName,
				new MutableConfiguration<String, String>());
	}

	/**
	 * Remove a cache from the CacheManager.
	 */
	public void removeCache(String cacheName) throws Exception {
		if (DEFAULT_CACHE_NAME.equalsIgnoreCase(cacheName)) {
			throw new Exception(
					"Can't remove or delete default application cache!");
		}
		cacheManager.destroyCache(cacheName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	public void finalize() {
		// make sure the cache manager is shutdown
		shutdown();
	}

	public void shutdown() {
		if (cacheManager != null)
			cacheManager.close();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public static void main(String[] args) {
		System.out.println(AppCacheManager.getInstance().cacheManager);
	}

}
