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

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import com.thoughtworks.xstream.converters.ConversionException;

/**
 * Test class that tests how serialization handles unknown elements
 */
public class UnknownElementTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * This test is meant to ensure that if a serialized object contains unknown elements (for example, if
	 * it's class implementation has changed since serialization), that an exception will be thrown when trying
	 * to deserialize, rather than silently ignoring unknown elements
	 * @throws Exception
	 */
	@Test(expected=ConversionException.class)
	public void shouldThrowExceptionIfUnknownElementExists() throws Exception {
		OpenmrsSerializer serializer = Context.getSerializationService().getSerializer(XStreamShortSerializer.class);
		
		EncounterType original = Context.getEncounterService().getEncounterType(1);
		String xml = serializer.serialize(original);
		Assert.assertTrue(xml.contains("<description>"));
		
		// Test unknown element names
		xml = xml.replace("description>", "descr>");
		serializer.deserialize(xml, EncounterType.class);
	}
}
