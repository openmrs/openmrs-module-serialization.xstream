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

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
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
	
	private static String DEFAULT_NAMING_MARKER = "$$EnhancerByCGLIB$$";
	
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
	protected boolean isCGLibProxy(Class type) {
		return (Enhancer.isEnhanced(type) && type.getName().indexOf(DEFAULT_NAMING_MARKER) > 0)
		        || type == CGLIBMapper.Marker.class;
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
		String id = reader.getAttribute("id");
		if (id != null && "1".equals(id))
			return true;
		else
			return false;
	}
	
	protected Mapper getMapper() {
		return mapper;
	}
	
	protected ConverterLookup getConverterLookup() {
		return converterLookup;
	}
	
	/**
	 * While obj is a sole object, xstream will serialize all attributes of it.<br />
	 * While obj is a property of other classes, xstream will just serialize uuid.
	 * 
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (needsFullSeralization(context)) {
			if (isCGLibProxy(obj.getClass())) {
				CustomCGLIBEnhancedConverter converter = new CustomCGLIBEnhancedConverter(getMapper(), getConverterLookup());
				converter.marshal(obj, writer, context);
			} else {
				Converter defaultConverter = getConverterLookup().lookupConverterForType(Object.class);
				defaultConverter.marshal(obj, writer, context);
			}
		} else {
			/*
			 * Here cast "obj" to "BaseOpenmrsObject", so that any short converter extending form BaseShortConverter
			 * can directly use this marshal method to serialize "obj".	
			 */
			writer.addAttribute("uuid", ((BaseOpenmrsObject) obj).getUuid());
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
			return getByUUID(uuid);
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
