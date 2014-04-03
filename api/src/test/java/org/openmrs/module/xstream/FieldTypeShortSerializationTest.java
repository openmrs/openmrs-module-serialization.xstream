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
import org.openmrs.Field;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a fieldType
 */
public class FieldTypeShortSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldFieldTypeShortSerialization() throws Exception {
		
		//prepare the necessary data
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/FieldTypeShortSerializationTest.xml");
		authenticate();
		
		Field f = Context.getFormService().getField(1);
		
		String xmlOutput = Context.getSerializationService().serialize(f, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("abf16b7d-39a5-4911-89da-0eefbfef7cb4", "/field/fieldType/@uuid", xmlOutput);
		//with short serialization, the "fieldType" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/field/fieldType/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldFieldTypeShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "FieldTypeShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/FieldTypeShortSerializationTest.xml");
		authenticate();
		
		//prepare the necessary data
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<field id=\"1\" uuid=\"db016b7d-39a5-4911-89da-0eefbfef7cb2\" retired=\"false\">\n");
		xmlBuilder.append("  <name>Some concept</name>\n");
		xmlBuilder.append("  <description>This is a test field</description>\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2006-07-18 11:03:31 CST</dateCreated>\n");
		xmlBuilder.append("  <fieldId>1</fieldId>\n");
		xmlBuilder.append("  <fieldType id=\"4\" uuid=\"abf16b7d-39a5-4911-89da-0eefbfef7cb4\"/>\n");
		xmlBuilder.append("  <concept id=\"5\" uuid=\"0cbe2ed3-cd5f-4f46-9459-26127c9265ab\"/>\n");
		xmlBuilder.append("  <tableName></tableName>\n");
		xmlBuilder.append("  <defaultValue></defaultValue>\n");
		xmlBuilder.append("  <selectMultiple>false</selectMultiple>\n");
		xmlBuilder.append("  <answers id=\"6\"/>\n");
		xmlBuilder.append("</field>\n");
		
		Field f = Context.getSerializationService().deserialize(xmlBuilder.toString(), Field.class,
		    XStreamShortSerializer.class);
		assertEquals("abf16b7d-39a5-4911-89da-0eefbfef7cb4", f.getFieldType().getUuid());
	}
}
