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
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a person
 */
public class PersonShortSerializationTest extends BaseModuleContextSensitiveTest {
	
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
		executeDataSet("org/openmrs/module/xstream/include/PersonShortSerializationTest.xml");
		authenticate();
		
		PersonAddress pa = Context.getPersonService().getPersonAddressByUuid("3350d0b5-821c-4e5e-ad1d-a9bce331e118");
		String xmlOutput = Context.getSerializationService().serialize(pa, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", "/personAddress/person/@uuid", xmlOutput);
		//with short serialization, the "person" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/personAddress/person/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldPersonShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "PersonShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/PersonShortSerializationTest.xml");
		authenticate();
		
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<personAddress id=\"1\" uuid=\"3350d0b5-821c-4e5e-ad1d-a9bce331e118\" voided=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2005-09-22 00:00:00 CST</dateCreated>\n");
		xmlBuilder.append("  <personAddressId>2</personAddressId>\n");
		xmlBuilder.append("  <person id=\"4\" uuid=\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\"/>\n");
		xmlBuilder.append("  <preferred>false</preferred>\n");
		xmlBuilder.append("  <address1>1050 Wishard Blvd.</address1>\n");
		xmlBuilder.append("  <address2>RG5</address2>\n");
		xmlBuilder.append("  <cityVillage>Indianapolis</cityVillage>\n");
		xmlBuilder.append("  <stateProvince>IN</stateProvince>\n");
		xmlBuilder.append("  <country>USA</country>\n");
		xmlBuilder.append("  <postalCode>46202</postalCode>\n");
		xmlBuilder.append("</personAddress>\n");
		
		PersonAddress pa = Context.getSerializationService().deserialize(xmlBuilder.toString(), PersonAddress.class,
		    XStreamShortSerializer.class);
		assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", pa.getPerson().getUuid());
	}
}
