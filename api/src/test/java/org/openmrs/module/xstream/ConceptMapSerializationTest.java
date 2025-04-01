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
import org.openmrs.ConceptMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.xmlunit.assertj.XmlAssert.assertThat;

/**
 * Test class that tests the serialization and deserialization of a conceptMap
 */
public class ConceptMapSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * create a conceptMap and make sure it can be serialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerializeConceptMap() throws Exception {
		//instantiate object
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/ConceptMapSerializationTest.xml");
		authenticate();
		
		ConceptMap cm = Context.getConceptService().getConcept(3).getConceptMappings().iterator().next();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(cm, XStreamSerializer.class);
		assertThat(xmlOutput).valueByXPath("/conceptMap/@uuid").isEqualTo("6c36f786-957d-4a14-a6ed-e66ced057066");
		assertThat(xmlOutput).valueByXPath("/conceptMap/@id").isEqualTo("1");
		assertThat(xmlOutput).valueByXPath("/conceptMap/concept/conceptId").isEqualTo("3");
		assertThat(xmlOutput).valueByXPath("/conceptMap/conceptMapType/@id").isEqualTo("2");
		assertThat(xmlOutput).valueByXPath("/conceptMap/conceptReferenceTerm/@id").isEqualTo("28");
		assertThat(xmlOutput).nodesByXPath("/conceptMap/creator").exist();
		assertThat(xmlOutput).valueByXPath("/conceptMap/dateCreated").isEqualTo(sdf.format(cm.getDateCreated()));
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializeConceptMap() throws Exception {
		String xml = TestUtil.getFileContents("org/openmrs/module/xstream/include/TestConceptMap.xml");
		ConceptMap cm = Context.getSerializationService().deserialize(xml, ConceptMap.class, XStreamSerializer.class);
		assertEquals("6c36f786-957d-4a14-a6ed-e66ced057066", cm.getUuid());
		assertEquals(1, cm.getConceptMapId().intValue());
		assertEquals(3, cm.getConcept().getConceptId().intValue());
		assertEquals(1, cm.getConceptReferenceTerm().getConceptSource().getConceptSourceId().intValue());
		assertEquals("test", cm.getConceptReferenceTerm().getCode());
		assertEquals(1, cm.getConceptMapType().getConceptMapTypeId().intValue());
		assertEquals("is a", cm.getConceptMapType().getName());
		assertEquals(1, cm.getCreator().getUserId().intValue());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		assertEquals(sdf.parse("2006-02-20 00:00:00 CST"), cm.getDateCreated());
	}
	
}
