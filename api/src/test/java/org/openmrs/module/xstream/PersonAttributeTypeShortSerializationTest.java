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

import static org.junit.Assert.assertEquals;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a personAttributeType
 */
public class PersonAttributeTypeShortSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldPersonShortSerialization() throws Exception {
		//prepare the necessary data
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/PersonAttributeTypeShortSerializationTest.xml");
		authenticate();
		
		PersonAttribute pa = Context.getPersonService().getPersonAttributeByUuid("0768f3da-b692-44b7-a33f-abf2c450474e");
		String xmlOutput = Context.getSerializationService().serialize(pa, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("b3b6d540-a32e-44c7-91b3-292d97667518", "/personAttribute/attributeType/@uuid",
		    xmlOutput);
		//with short serialization, the "attributeType" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/personAttribute/attributeType/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldPersonShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "PersonAttributeTypeShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/PersonAttributeTypeShortSerializationTest.xml");
		authenticate();
		
		//prepare the necessary data
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<personAttribute id=\"1\" uuid=\"0768f3da-b692-44b7-a33f-abf2c450474e\" voided=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2008-08-15 15:46:47 CST</dateCreated>\n");
		xmlBuilder.append("  <personAttributeId>1</personAttributeId>\n");
		xmlBuilder.append("  <person id=\"4\" uuid=\"df8ae447-6745-45be-b859-403241d9913c\"/>\n");
		xmlBuilder.append("  <attributeType id=\"5\" uuid=\"b3b6d540-a32e-44c7-91b3-292d97667518\"/>\n");
		xmlBuilder.append("  <value></value>\n");
		xmlBuilder.append("</personAttribute>\n");
		
		PersonAttribute pa = Context.getSerializationService().deserialize(xmlBuilder.toString(), PersonAttribute.class,
		    XStreamShortSerializer.class);
		assertEquals("b3b6d540-a32e-44c7-91b3-292d97667518", pa.getAttributeType().getUuid());
	}
}
