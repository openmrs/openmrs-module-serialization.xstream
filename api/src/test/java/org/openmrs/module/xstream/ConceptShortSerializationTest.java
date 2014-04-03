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
import org.openmrs.ConceptDescription;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a concept
 */
public class ConceptShortSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldPatientShortSerialization() throws Exception {
		
		//prepare the necessary data
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/ConceptShortSerializationTest.xml");
		authenticate();
		
		ConceptDescription cd = (ConceptDescription) Context.getConceptService().getConceptDescriptionByUuid("79a3efa7-3a43-4b38-ac5d-9b68aee086c6");
		String xmlOutput = Context.getSerializationService().serialize(cd, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("0cbe2ed3-cd5f-4f46-9459-26127c9265ab", "/conceptDescription/concept/@uuid",
		    xmlOutput);
		//with short serialization, the "concept" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/conceptDescription/concept/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldPatientShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "ConceptShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/ConceptShortSerializationTest.xml");
		authenticate();
		
		//prepare the necessary data
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<conceptDescription id=\"1\" uuid=\"79a3efa7-3a43-4b38-ac5d-9b68aee086c6\">\n");
		xmlBuilder.append("  <conceptDescriptionId>9</conceptDescriptionId>\n");
		xmlBuilder.append("  <concept id=\"2\" uuid=\"0cbe2ed3-cd5f-4f46-9459-26127c9265ab\"/>\n");
		xmlBuilder.append("  <description>This is used for coughs</description>\n");
		xmlBuilder.append("  <locale id=\"3\">en</locale>\n");
		xmlBuilder.append("  <creator id=\"4\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"5\">2008-08-15 15:27:51 CST</dateCreated>\n");
		xmlBuilder.append("</conceptDescription>\n");
		
		ConceptDescription cd = Context.getSerializationService().deserialize(xmlBuilder.toString(), ConceptDescription.class,
		    XStreamShortSerializer.class);
		assertEquals("0cbe2ed3-cd5f-4f46-9459-26127c9265ab", cd.getConcept().getUuid());
	}
}
