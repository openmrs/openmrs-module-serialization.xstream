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
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a patientIdentifierType
 */
public class PatientIdentifierTypeShortSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldPatientIdentifierTypeShortSerialization() throws Exception {
		
		//prepare the necessary data
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/PatientIdentifierTypeShortSerializationTest.xml");
		authenticate();
		
		PatientIdentifier pi = Context.getPatientService()
		        .getPatientIdentifierByUuid("8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614");
		String xmlOutput = Context.getSerializationService().serialize(pi, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("1a339fe9-38bc-4ab3-b180-320988c0b968", "/patientIdentifier/identifierType/@uuid",
		    xmlOutput);
		//with short serialization, the "identifierType" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/patientIdentifier/identifierType/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldPatientIdentifierTypeShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "PatientIdentifierTypeShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/PatientIdentifierTypeShortSerializationTest.xml");
		authenticate();
		
		//prepare the necessary data
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<patientIdentifier id=\"1\" uuid=\"8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614\" voided=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2005-09-22 00:00:00 CST</dateCreated>\n");
		xmlBuilder.append("  <voidReason></voidReason>\n");
		xmlBuilder.append("  <patientIdentifierId>2</patientIdentifierId>\n");
		xmlBuilder.append("  <patient id=\"4\" uuid=\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\"/>\n");
		xmlBuilder.append("  <identifier>101-6</identifier>\n");
		xmlBuilder.append("  <identifierType id=\"5\" uuid=\"1a339fe9-38bc-4ab3-b180-320988c0b968\"/>\n");
		xmlBuilder.append("  <location id=\"6\" uuid=\"dc5c1fcc-0459-4201-bf70-0b90535ba362\" retired=\"false\">\n");
		xmlBuilder.append("    <name>Unknown Location</name>\n");
		xmlBuilder.append("    <description>The default location used when limited information is known</description>\n");
		xmlBuilder.append("    <creator reference=\"2\"/>\n");
		xmlBuilder.append("    <dateCreated class=\"sql-timestamp\" id=\"7\">2005-09-22 00:00:00 CST</dateCreated>\n");
		xmlBuilder.append("    <locationId>1</locationId>\n");
		xmlBuilder.append("    <childLocations id=\"8\"/>\n");
		xmlBuilder.append("    <tags id=\"9\"/>\n");
		xmlBuilder.append("  </location>\n");
		xmlBuilder.append("  <preferred>true</preferred>\n");
		xmlBuilder.append("</patientIdentifier>\n");
		
		PatientIdentifier pi = Context.getSerializationService().deserialize(xmlBuilder.toString(), PatientIdentifier.class,
		    XStreamShortSerializer.class);
		assertEquals("1a339fe9-38bc-4ab3-b180-320988c0b968", pi.getIdentifierType().getUuid());
	}
}
