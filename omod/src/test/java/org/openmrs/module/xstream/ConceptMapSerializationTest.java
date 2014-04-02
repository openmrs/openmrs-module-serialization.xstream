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

import java.text.SimpleDateFormat;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.ConceptMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.Version;
import org.openmrs.module.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsConstants;

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
		executeDataSet(TestUtil.resolveTestDatasetFilename("org/openmrs/module/xstream/include/ConceptMapSerializationTest"
		        + TestUtil.VERSION_PLACE_HOLDER + ".xml"));
		authenticate();
		
		ConceptMap cm = Context.getConceptService().getConcept(3).getConceptMappings().iterator().next();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		//serialize and compare with a give string
		String xmlOutput = Context.getSerializationService().serialize(cm, XStreamSerializer.class);
		XMLAssert.assertXpathEvaluatesTo("6c36f786-957d-4a14-a6ed-e66ced057066", "/conceptMap/@uuid", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("1", "/conceptMap/conceptMapId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("3", "/conceptMap/concept/conceptId", xmlOutput);
		XMLAssert.assertXpathExists("/conceptMap/creator", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(sdf.format(cm.getDateCreated()), "/conceptMap/dateCreated", xmlOutput);
		if (TestUtil.VERSION_ONE_NINE.compareTo(new Version(OpenmrsConstants.OPENMRS_VERSION_SHORT)) <= 0) {
			XMLAssert.assertXpathExists("/conceptMap/conceptMapType[conceptMapTypeId=1]", xmlOutput);
			XMLAssert.assertXpathExists("/conceptMap/conceptReferenceTerm[conceptReferenceTermId=1]", xmlOutput);
			XMLAssert.assertXpathEvaluatesTo("1", "/conceptMap/conceptReferenceTerm/conceptSource/conceptSourceId[1]",
			    xmlOutput);
		} else {
			XMLAssert.assertXpathEvaluatesTo("1", "/conceptMap/source/conceptSourceId", xmlOutput);
			XMLAssert.assertXpathEvaluatesTo("test", "/conceptMap/sourceCode", xmlOutput);
			XMLAssert.assertXpathEvaluatesTo("test", "/conceptMap/comment", xmlOutput);
		}
	}
	
	/**
	 * Construct a serialized xml string and make sure it can be deserialized correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldDeserializeConceptMap() throws Exception {
		String serializedText = TestUtil
		        .getSerializedContents("org/openmrs/module/xstream/include/ConceptMapDeserializationTest"
		                + TestUtil.VERSION_PLACE_HOLDER + ".xml");
		//deserialize and make sure everything has been put into object
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		ConceptMap cm = Context.getSerializationService().deserialize(serializedText, ConceptMap.class,
		    XStreamSerializer.class);
		assertEquals("6c36f786-957d-4a14-a6ed-e66ced057066", cm.getUuid());
		assertEquals(1, cm.getConceptMapId().intValue());
		assertEquals(3, cm.getConcept().getConceptId().intValue());
		assertEquals(1, cm.getSource().getConceptSourceId().intValue());
		assertEquals("test", cm.getSourceCode());
		if (TestUtil.VERSION_ONE_NINE.compareTo(new Version(OpenmrsConstants.OPENMRS_VERSION_SHORT)) > 0) {
			assertEquals("test", cm.getComment());
		}
		assertEquals(1, cm.getCreator().getUserId().intValue());
		assertEquals(sdf.parse("2006-02-20 00:00:00 CST"), cm.getDateCreated());
	}
}
