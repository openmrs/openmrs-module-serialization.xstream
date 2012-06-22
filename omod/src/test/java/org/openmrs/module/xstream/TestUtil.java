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
package org.openmrs.module.xstream;

import java.lang.reflect.Field;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.util.Reflect;

/**
 * Utility methods used during unit testing
 *
 */
@Ignore
public class TestUtil {

	/**
	 * Tests deserialization of a given object.
	 * @param object the object to test
	 * @throws Exception
	 */
	public static void testDeserialize(Object object) throws Exception {
		String xml = Context.getSerializationService().serialize(object, XStreamSerializer.class);
		Object deserializedObject = Context.getSerializationService().deserialize(xml, object.getClass(),
			    XStreamSerializer.class);
		
		List<Field> fields = Reflect.getAllFields(object.getClass());
		for (Field field : fields) {
			Assert.assertEquals(field.get(object), field.get(deserializedObject));
		}
	}
}
