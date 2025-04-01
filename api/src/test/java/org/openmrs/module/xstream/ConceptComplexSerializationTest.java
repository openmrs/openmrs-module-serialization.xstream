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

import org.junit.Test;
import org.openmrs.ConceptComplex;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.xmlunit.assertj.XmlAssert.assertThat;


/**
 * Test class that tests the serialization and deserialization of a conceptComplex
 */
public class ConceptComplexSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * create a conceptComplex and make sure it can be serialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerializeConceptComplex() throws Exception {
		//instantiate object
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/ConceptComplexSerializationTest.xml");
		authenticate();
		
		ConceptComplex cc = Context.getConceptService().getConceptComplex(3);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(cc, XStreamSerializer.class);
		assertThat(xmlOutput).valueByXPath("/conceptComplex/@uuid").isEqualTo("0cbe2ed3-cd5f-4f46-9459-26127c9265ab");
		assertThat(xmlOutput).valueByXPath("/conceptComplex/conceptId").isEqualTo("3");
		assertThat(xmlOutput).valueByXPath("/conceptComplex/@retired").isEqualTo("false");
		assertThat(xmlOutput).valueByXPath("/conceptComplex/datatype/@id").isEqualTo("2");
		assertThat(xmlOutput).valueByXPath("/conceptComplex/conceptClass/@id").isEqualTo("3");
		assertThat(xmlOutput).nodesByXPath("/conceptComplex/creator").exist();
		assertThat(xmlOutput).valueByXPath("/conceptComplex/set").isEqualTo("false");
		assertThat(xmlOutput).valueByXPath("/conceptComplex/dateCreated").isEqualTo(sdf.format(cc.getDateCreated()));
		assertThat(xmlOutput).nodesByXPath("/conceptComplex/names/conceptName[conceptNameId='2456']").exist();
		assertThat(xmlOutput).nodesByXPath("/conceptComplex/answers/conceptAnswer[conceptAnswerId='762']").exist();
		assertThat(xmlOutput).nodesByXPath("/conceptComplex/conceptSets/conceptSet[conceptSetId='1']").exist();
		assertThat(xmlOutput).nodesByXPath("/conceptComplex/descriptions/conceptDescription[conceptDescriptionId='9']").exist();
		assertThat(xmlOutput).nodesByXPath("/conceptComplex/conceptMappings/conceptMap[conceptMapId='1']").exist();
		assertThat(xmlOutput).valueByXPath("/conceptComplex/handler").isEqualTo("test purpose");
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializeConceptComplex() throws Exception {
		String xml = TestUtil.getFileContents("org/openmrs/module/xstream/include/TestConceptComplex.xml");
		ConceptComplex cc = Context.getSerializationService()
		        .deserialize(xml, ConceptComplex.class, XStreamSerializer.class);
		assertEquals("0cbe2ed3-cd5f-4f46-9459-26127c9265ab", cc.getUuid());
		assertEquals(3, cc.getConceptId().intValue());
		assertFalse("The retired shouldn't be " + cc.isRetired(), cc.isRetired());
		assertEquals(4, cc.getDatatype().getConceptDatatypeId().intValue());
		assertEquals(3, cc.getConceptClass().getConceptClassId().intValue());
		assertEquals(1, cc.getCreator().getUserId().intValue());
		assertFalse("The set shouldn't be " + cc.getSet(), cc.getSet());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		assertEquals(sdf.parse("2008-08-15 15:27:51 CST"), cc.getDateCreated());
		assertEquals(1, cc.getNames().size());
		assertEquals(1, cc.getAnswers().size());
		assertEquals(1, cc.getDescriptions().size());
		assertEquals(1, cc.getConceptSets().size());
		assertEquals(1, cc.getConceptMappings().size());
		assertEquals("test purpose", cc.getHandler());
	}
}
