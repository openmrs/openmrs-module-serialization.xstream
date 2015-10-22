package org.openmrs.module.serialization.xstream.converter;

import java.util.List;

import net.sf.cglib.proxy.Enhancer;

import org.openmrs.User;
import org.openmrs.module.serialization.xstream.mapper.CGLibMapper;
import org.openmrs.module.serialization.xstream.mapper.JavassistMapper;
import org.openmrs.module.serialization.xstream.strategy.CustomReferenceByIdMarshaller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CGLIBMapper;

/**
 * This class is related to ticket#1701. <br/>
 * It will serialize User.changedBy only as its uuid.<br/>
 * It will return a new user object only with its uuid filled in after
 * deserialization.<br/>
 */
public class UserConverter implements Converter {
	
	private Converter defaultConverter;
	
	private XStream xstream;
	
	/**
	 * @param converterLookup
	 */
	public UserConverter(XStream xstream) {
		this.xstream = xstream;
		defaultConverter = xstream.getConverterLookup().lookupConverterForType(Object.class);
	}
	
	/**
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	public boolean canConvert(Class clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		CustomReferenceByIdMarshaller mashaller = (CustomReferenceByIdMarshaller)context;
		List<Class> serializedClasses = mashaller.getSerializedClasses();
		int size = serializedClasses.size();
		if(size > 1 && User.class.isAssignableFrom(serializedClasses.get(size - 2))){
			User user = (User) obj;
			writer.addAttribute("uuid", user.getUuid());
		}else{
			if(isCGLibProxy(obj.getClass())){
				CustomCGLIBEnhancedConverter converter = new CustomCGLIBEnhancedConverter(xstream.getMapper(), xstream.getConverterLookup());
				converter.marshal(obj, writer, context);
			}
			else if (isJavassistProxy(obj.getClass())) {
				CustomJavassistEnhancedConverter converter = new CustomJavassistEnhancedConverter(xstream.getMapper(), xstream.getConverterLookup());
				converter.marshal(obj, writer, context);
			}
			else
				defaultConverter.marshal(obj, writer, context);
		}
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
	 *      com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		User user = null;
		if(reader.getAttributeCount() == 1 && "uuid".equals(reader.getAttributeName(0))){
			user.setUuid(reader.getAttribute("uuid"));
		}else{
			user = (User)defaultConverter.unmarshal(reader, context);
		}
		return user;
	}
	
	/**
	 * judge whether current type is a type of CGLib proxy
	 * 
	 * @param type - the type to be judged
	 * @return whether type is a type of CGLib proxy
	 */
	protected boolean isCGLibProxy(Class type) {
		return (Enhancer.isEnhanced(type) && type.getName().indexOf(CGLibMapper.marker) > 0)
		        || type == CGLIBMapper.Marker.class;
	}
	
	/**
	 * judge whether current type is a type of Javassist proxy
	 * 
	 * @param type - the type to be judged
	 * @return whether type is a type of Javassist proxy
	 */
	protected boolean isJavassistProxy(Class type) {
        return type.getName().indexOf(JavassistMapper.OLD_NAMING_MARKER) > 0
                || type.getName().indexOf(JavassistMapper.NEW_NAMING_MARKER) > 0;
	}
}
