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
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a relationshipType
 */
public class RelationshipTypeShortSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldRelationshipTypeShortSerialization() throws Exception {
		
		//prepare the necessary data
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/RelationshipTypeShortSerializationTest.xml");
		authenticate();
		
		Relationship r = Context.getPersonService().getRelationship(1);
		String xmlOutput = Context.getSerializationService().serialize(r, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("6d9002ea-a96b-4889-af78-82d48c57a110", "/relationship/relationshipType/@uuid",
		    xmlOutput);
		//with short serialization, the "relationship" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/relationship/relationshipType/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldRelationshipTypeShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "RelationshipTypeShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/RelationshipTypeShortSerializationTest.xml");
		authenticate();
		
		//prepare the necessary data
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<relationship id=\"1\" uuid=\"c18717dd-5d78-4a0e-84fc-ee62c5f0676a\" voided=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2008-08-18 11:50:15 CST</dateCreated>\n");
		xmlBuilder.append("  <relationshipId>1</relationshipId>\n");
		xmlBuilder.append("  <personA id=\"4\" uuid=\"341b4e41-790c-484f-b6ed-71dc8da222de\"/>\n");
		xmlBuilder.append("  <relationshipType id=\"5\" uuid=\"6d9002ea-a96b-4889-af78-82d48c57a110\"/>\n");
		xmlBuilder.append("  <personB id=\"6\" uuid=\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\"/>\n");
		xmlBuilder.append("</relationship>\n");
		
		Relationship r = Context.getSerializationService().deserialize(xmlBuilder.toString(), Relationship.class,
		    XStreamShortSerializer.class);
		assertEquals("6d9002ea-a96b-4889-af78-82d48c57a110", r.getRelationshipType().getUuid());
	}
}
