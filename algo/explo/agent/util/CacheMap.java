package explo.agent.util;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Implementation of a Least Recently Used cache map
 */
public class CacheMap extends LinkedHashMap<Object, Object>{
	private static final long serialVersionUID = 1L;
	private int maxCapacity;
	public CacheMap(int initialCapacity, int maxCapacity) {
		super(initialCapacity, 1.00f, true);
		this.maxCapacity = maxCapacity;
	}
	@Override
	protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
		return size()>maxCapacity;
	}
}