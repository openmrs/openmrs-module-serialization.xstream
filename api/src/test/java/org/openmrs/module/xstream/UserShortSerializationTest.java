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
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a user
 */
public class UserShortSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldUserShortSerialization() throws Exception {
		//prepare the necessary data
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/UserShortSerializationTest.xml");
		authenticate();
		
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByUuid(
		    "1a339fe9-38bc-4ab3-b180-320988c0b968");
		String xmlOutput = Context.getSerializationService().serialize(pit, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("1010d442-e134-11de-babe-001e378eb67e", "/patientIdentifierType/creator/@uuid",
		    xmlOutput);
		//with short serialization, the "user" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/patientIdentifierType/creator/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldUserShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "UserShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/UserShortSerializationTest.xml");
		authenticate();
		
		//prepare the necessary data
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder
		        .append("<patientIdentifierType id=\"1\" uuid=\"1a339fe9-38bc-4ab3-b180-320988c0b968\" retired=\"false\">\n");
		xmlBuilder.append("  <name>OpenMRS Identification Number</name>\n");
		xmlBuilder.append("  <description>Unique number used in OpenMRS</description>\n");
		xmlBuilder.append("  <creator uuid=\"" + Context.getAuthenticatedUser().getUuid() + "\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2005-09-22 00:00:00 CST</dateCreated>\n");
		xmlBuilder.append("  <patientIdentifierTypeId>1</patientIdentifierTypeId>\n");
		xmlBuilder.append("  <required>false</required>\n");
		xmlBuilder.append("  <checkDigit>true</checkDigit>\n");
		xmlBuilder.append("  <validator>org.openmrs.patient.impl.LuhnIdentifierValidator</validator>\n");
		xmlBuilder.append("</patientIdentifierType>\n");
		
		PatientIdentifierType pit = Context.getSerializationService().deserialize(xmlBuilder.toString(),
		    PatientIdentifierType.class, XStreamShortSerializer.class);
		assertEquals(Context.getAuthenticatedUser().getUuid(), pit.getCreator().getUuid());
	}
}
