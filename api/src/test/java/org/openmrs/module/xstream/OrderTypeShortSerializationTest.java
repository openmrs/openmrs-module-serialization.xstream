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
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that test the short serialization and short deserialization of a orderType
 */
public class OrderTypeShortSerializationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * generate the relative objects and make sure the short serialization can work
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldOrderTypeShortSerialization() throws Exception {
		
		//prepare the necessary data
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/OrderTypeShortSerializationTest.xml");
		authenticate();
		
		Order o = Context.getOrderService().getOrderByUuid("921de0a3-05c4-444a-be03-e01b4c4b9142");
		
		String xmlOutput = Context.getSerializationService().serialize(o, XStreamShortSerializer.class);
		//should only serialize "uuid"
		XMLAssert.assertXpathEvaluatesTo("f149b5e1-4314-4d0d-a95f-1c4f8031161d", "/order/orderType/@uuid", xmlOutput);
		//with short serialization, the "orderType" element shouldn't contain any child element in the serialized xml
		XMLAssert.assertXpathNotExists("/order/orderType/*", xmlOutput);
	}
	
	/**
	 * give a expected xml string and make sure it can be shortly deserialized
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldOrderTypeShortDeserialization() throws Exception {
		//prepare the necessary data
		
		/*
		 * Because "XXXShortConverter.unmarshal(HierarchicalStreamReader, UnmarshallingContext)" has operations accessing data in database,
		 * We also need to use the "OrderTypeShortSerializationTest.xml" here 
		 */
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/xstream/include/OrderTypeShortSerializationTest.xml");
		authenticate();
		
		//prepare the necessary data
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<order id=\"1\" uuid=\"921de0a3-05c4-444a-be03-e01b4c4b9142\" voided=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"6adb7c42-cfd2-4301-b53b-ff17c5654ff7\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2008-08-19 12:20:22 CST</dateCreated>\n");
		xmlBuilder.append("  <orderId>1</orderId>\n");
		xmlBuilder.append("  <patient id=\"4\" uuid=\"5946f880-b197-400b-9caa-a3c661d23041\"/>\n");
		xmlBuilder.append("  <orderType id=\"5\" uuid=\"f149b5e1-4314-4d0d-a95f-1c4f8031161d\"/>\n");
		xmlBuilder.append("  <concept id=\"6\" uuid=\"15f83cd6-64e9-4e06-a5f9-364d3b14a43d\"/>\n");
		xmlBuilder.append("  <instructions></instructions>\n");
		xmlBuilder.append("  <startDate class=\"sql-timestamp\" id=\"7\">2008-08-08 00:00:00 CST</startDate>\n");
		xmlBuilder.append("  <orderer />\n");
		xmlBuilder.append("  <discontinued>true</discontinued>\n");
		xmlBuilder.append("  <discontinuedBy />\n");
		xmlBuilder
		        .append("  <discontinuedDate class=\"sql-timestamp\" id=\"8\">2008-08-15 00:00:00 CST</discontinuedDate>\n");
		xmlBuilder.append("</order>\n");
		
		Order o = Context.getSerializationService().deserialize(xmlBuilder.toString(), Order.class,
		    XStreamShortSerializer.class);
		assertEquals("f149b5e1-4314-4d0d-a95f-1c4f8031161d", o.getOrderType().getUuid());
	}
}
