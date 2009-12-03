/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. May 2004 by Joe Walnes
 */
package org.openmrs.module.serialization.xstream.strategy;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class implement the feature to determinate whether a single object or collection need to add
 * reference for it while serializing.<br/>
 */
public class CustomObjectIdDictionary {
	
	/* 
	 * About the info of map
	 * Key: the object match one element in serialized xml string
	 * Value: the id of that match element
	 */
	@SuppressWarnings("unchecked")
	private final Map map = new LinkedHashMap();
	
	private static final String marker = new String("EnhancerByCGLIB");
	
	private volatile int counter;
	
	/**
	 * Auto generated method comment
	 * 
	 * @param obj
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	public void associateId(Object obj, Object id) {
		map.put(obj, id);
		++counter;
		cleanup();
	}
	
	/**
	 * Iterate each entry of map to find whether it has a key equals with "obj"
	 * <br/>
	 * 
	 * @param obj - the object need to be compared with keys of map
	 * @return if map has a key equal with "obj", then return the value matching with that key
	 * 		   otherwise, return null.
	 */
	@SuppressWarnings("unchecked")
	public Object lookupId(Object obj) {
		++counter;
		if(isCollectionAndNoItems(obj)){
			Object res = null;
			Iterator it = map.keySet().iterator();
			while(it.hasNext()){
				Object key = it.next();
				if(isCollectionAndNoItems(key))
					/*
					 * For such a case which satisfy follow two conditions: 
					 *     (1) Suppose, A is Collection<PersonName> and B is a Collection<PersonAddress>,
					 *     (2) Both A and B's size is zero.
					 * Then if we use A.equals(B), the result will be true.(you can refer to the jdk1.6 doc)
					 * So for this case i use the "==" to do comparison.
					 */
					if(obj == key){
						res = map.get(key);
						break;
					}
			}
			return res;
		}else{
			return map.get(obj);
		}
	}
	
	/**
	 * Judge whether map contains a key equals with "obj". <br/>
	 * <br/>
	 * Note: Here, the mechanism of comparison is equals with {@link CustomObjectIdDictionary#lookupId(Object)}
	 * 
	 * @param obj - the object need to be compared with keys of map
	 * @return whether "obj" has already been in map
	 */
	@SuppressWarnings("unchecked")
	public boolean containsId(Object obj) {	
		++counter;
		if(isCollectionAndNoItems(obj)){
			boolean contain = false;
			Iterator it = map.keySet().iterator();
			while(it.hasNext()){
				Object key = it.next();
				if(isCollectionAndNoItems(key))
					/*
					 * For such a case which satisfy follow two conditions: 
					 *     (1) Suppose, A is Collection<PersonName> and B is a Collection<PersonAddress>,
					 *     (2) Both A and B's size is zero.
					 * Then if we use A.equals(B), the result will be true.(you can refer to the jdk1.6 doc)
					 * So for this case i use the "==" to do comparison.
					 */
					if(obj == key){
						contain = true;
						break;
					}
			}
			return contain;
		}else{
			return map.containsKey(obj);
		}
	}
	
	public void removeId(Object item) {
		map.remove(item);
		++counter;
		cleanup();
	}
	
	public int size() {
		return map.size();
	}
	
	private void cleanup() {
		if (counter > 10000) {
			counter = 0;
			// much more efficient to remove any orphaned wrappers at once
			for (final Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
				if (iterator.next() == null) {
					iterator.remove();
				}
			}
		}
	}
	
	/**
	 * Judge whether obj is a Collection or Map And its size is zero
	 * <br/>
	 * 
	 * @param obj - the obj to be judge
	 * @return whether obj is a Collection or Map and its size is zero
	 */
	private boolean isCollectionAndNoItems(Object obj){
		Class cls = obj.getClass();
		if(Collection.class.isAssignableFrom(cls) && ((Collection)obj).size() == 0)
			return true;
		else if(Map.class.isAssignableFrom(cls) && ((Map)obj).size() == 0)
			return true;
		else
			return false;
	}	
}