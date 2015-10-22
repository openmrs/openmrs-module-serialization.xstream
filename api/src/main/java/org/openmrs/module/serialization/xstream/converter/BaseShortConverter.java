/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.serialization.xstream.converter;

import net.sf.cglib.proxy.Enhancer;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.module.serialization.xstream.mapper.CGLibMapper;
import org.openmrs.module.serialization.xstream.mapper.JavassistMapper;
import org.openmrs.module.serialization.xstream.strategy.CustomReferenceByIdMarshaller;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CGLIBMapper;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * The base converter of all short converters
 */
public abstract class BaseShortConverter implements Converter {
	
	private Mapper mapper;
	
	private ConverterLookup converterLookup;
	
	/**
	 * Constructor
	 * 
	 * @param mapper - used to serialize CGLib proxy
	 * @param converterLookup - used to look up a converter for fully serializing the
	 *            short-serialized object
	 */
	public BaseShortConverter(Mapper mapper, ConverterLookup converterLookup) {
		super();
		this.mapper = mapper;
		this.converterLookup = converterLookup;
	}
	
	/**
	 * For Short Serialization, if a domain object which needs short serialization is the object
	 * passed into "XStreamShortSerializer#serialize(Object)" as a parameter, then xstream should
	 * fully serialize it, not only serialize its uuid. Because in that case, the object does not
	 * exist as a member of other object.<br />
	 * 
	 * @param context - xstrema can know how many objects are serialized with a "id" attribute
	 *            through this param
	 * @return whether xstream need full serialize the currentSerializedItem
	 * @see XStreamShortSerializer#serialize(Object)
	 * @see UserShortConverter#marshal(Object, HierarchicalStreamWriter, MarshallingContext)
	 * @see PatientShortConverter#marshal(Object, HierarchicalStreamWriter, MarshallingContext)
	 * @see PersonShortConverter#marshal(Object, HierarchicalStreamWriter, MarshallingContext)
	 */
	protected boolean needsFullSeralization(MarshallingContext context) {
		CustomReferenceByIdMarshaller marshaller = (CustomReferenceByIdMarshaller) context;
		return marshaller.getCount() == 1 ? true : false;
	}
	
	/**
	 * judge whether current type is a type of CGLib proxy
	 * 
	 * @param type - the type to be judged
	 * @return whether type is a type of CGLib proxy
	 */
	protected boolean isCGLibProxy(Class<?> type) {
		return (Enhancer.isEnhanced(type) && type.getName().indexOf(CGLibMapper.marker) > 0)
		        || type == CGLIBMapper.Marker.class;
	}
	
	/**
	 * judge whether current type is a type of Javassist proxy
	 * 
	 * @param type - the type to be judged
	 * @return whether type is a type of Javassist proxy
	 */
	protected boolean isJavassistProxy(Class<?> type) {
        return type.getName().indexOf(JavassistMapper.OLD_NAMING_MARKER) > 0
                || type.getName().indexOf(JavassistMapper.NEW_NAMING_MARKER) > 0;
	}
	
	/**
	 * For short deserialization, we can know whether need to shortly deserialize current node
	 * according to that whether current node has a "id" attribute and its value == "1" <br />
	 * Note: this method will be used in every short converter's
	 * "unmarshal(HierarchicalStreamReader, UnmarshallingContext)"
	 * 
	 * @param reader - the stream read from
	 * @return
	 * @see UserShortConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
	 * @see PatientShortConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
	 * @see PersonShortConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
	 */
	protected boolean needsFullDeserialization(HierarchicalStreamReader reader) {
		String uuid = reader.getAttribute("uuid");
		if (uuid == null) {
			return true;
		}
		else {
			String id = reader.getAttribute("id");
			return (id != null && "1".equals(id));
		}
	}
	
	protected Mapper getMapper() {
		return mapper;
	}
	
	protected ConverterLookup getConverterLookup() {
		return converterLookup;
	}
	
	/**
	 * If this object is an OpenMRS object, if it is not the base node in the serialization
	 * (eg. it is a property of another object that is being serialized), and if it has been previously
	 * saved to the database, this will serialize the object by uuid.  Otherwise, it will perform the
	 * default full serialization of all of it's underlying properties
	 * 
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
		
		String uuid = null;
		try {
			OpenmrsObject omrsObj = (OpenmrsObject) obj;
			if (omrsObj.getId() != null || needsFullSeralization(context)) {
				uuid = omrsObj.getUuid();	
			}
			else {
				omrsObj.setUuid(null);
			}
		} 
		catch (Exception ex) {
		}

		if (needsFullSeralization(context) || uuid == null) {
			if (isCGLibProxy(obj.getClass())) {
				CustomCGLIBEnhancedConverter converter = new CustomCGLIBEnhancedConverter(getMapper(), getConverterLookup());
				converter.marshal(obj, writer, context);
			} 
			else if (isJavassistProxy(obj.getClass())) {
				CustomJavassistEnhancedConverter converter = new CustomJavassistEnhancedConverter(getMapper(), getConverterLookup());
				converter.marshal(obj, writer, context);
			} 
			else {
				Converter defaultConverter = getConverterLookup().lookupConverterForType(Object.class);
				defaultConverter.marshal(obj, writer, context);
			}
		} 
		else {
			writer.addAttribute("uuid", uuid);
		}
	}
	
	/**
	 * If current node has a "id" attribute and its value == "1", it represents that we should fully
	 * deserialize current node, else just shortly deserialize current node.
	 * 
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
	 *      com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		if (needsFullDeserialization(reader)) {
			Converter defaultConverter = getConverterLookup().lookupConverterForType(Object.class);
			return defaultConverter.unmarshal(reader, context);
		} else {
			String uuid = reader.getAttribute("uuid");
			Object ret = context.get(uuid);
			if (ret == null) {
				ret = getByUUID(uuid);
				context.put(uuid, ret);
			}
			return ret;
		}
	}
	
	/**
	 * Every short converter should override this method, so that it can correctly get object from
	 * its uuid.
	 * 
	 * <pre>
	 *     User user = Context.getUserService().getUserByUuid(uuid);
	 * </pre>
	 */
	public abstract Object getByUUID(String uuid);
	
}
