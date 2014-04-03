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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.custommonkey.xmlunit.XMLAssert;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;



/**
 * Test class that test follow points<br />
 * (1) should fully serialize a short-serialized object while it exists sole, not as a member of other object<br />
 * (2) should shortly serialize a short-serialized object while it exists as a member of other object<br />
 * (3) should fully serialize a short-serialized object's CGLib proxy while it exists sole, not as a member of other object<br />
 * (4) should shortly serialize a short-serialized object's CGLib proxy while it exists as a member of other object<br />
 * (5) should fully deserialize a short-serialized object while it exists sole as the root node of serialized xml string<br />
 * (6) should shortly deserialize a short-serialized object while it exists as a child node of serialized xml string<br />
 * (7) should shortly serialize a short-serialized object while it exist as the element of a Collection<br />
 * (8) should shortly serialize a short-serialized object while it exist as the key/value of a Map
 */
public class XStreamShortSerializerTest extends BaseModuleContextSensitiveTest{
	/**
	 * 
	 * should fully serialize a short-serialized object while it exists sole, not as a member of other object.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void shouldFullySerializeObject() throws Exception{
		User user = Context.getUserService().getUser(501);
		assertTrue("current user shouldn't be a cglib proxy", User.class == user.getClass());
		String xmlOutput = Context.getSerializationService().serialize(user, XStreamShortSerializer.class);
		//test root node in "xmlOutput" should not contain only a uuid attribute.
		XMLAssert.assertXpathEvaluatesTo("501", "/user/userId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("bruno", "/user/username", xmlOutput);
	}
	
	/**
	 * 
     * should shortly serialize a short-serialized object while it exists as a member of other object.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldShortlySerializeObject() throws Exception{
		Patient patient = Context.getPatientService().getPatient(999);
		assertEquals("current patient should contain a creator whoes userId == 1", 1, patient.getCreator().getUserId().intValue());
		String xmlOutput = Context.getSerializationService().serialize(patient, XStreamShortSerializer.class);
		//test the node named as "creator" in "xmlOutput" should only contain a uuid attribute.
		XMLAssert.assertXpathEvaluatesTo("1010d442-e134-11de-babe-001e378eb67e", "/patient/creator/@uuid", xmlOutput);
		XMLAssert.assertXpathNotExists("/patient/creator/*", xmlOutput);
	}
	
	/**
	 * 
     * should fully serialize a short-serialized object's CGLib proxy while it exists sole, not as a member of other object.
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldFullySerializeCGLibProxy() throws Exception{
		Person person = Context.getPersonService().getPersonAttribute(1).getPerson();
		assertTrue("current person should be a cglib proxy", HibernateProxy.class.isAssignableFrom(person.getClass()));
		String xmlOutput = Context.getSerializationService().serialize(person, XStreamShortSerializer.class);
		//test root node in "xmlOutput" should not contain only a uuid attribute.
		XMLAssert.assertXpathEvaluatesTo("501", "/person/personId", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo("F", "/person/gender", xmlOutput);
	}
	
	/**
	 * 
	 * should shortly serialize a short-serialized object's CGLib proxy while it exists as a member of other object.
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldShortlySerializeCGLibProxy() throws Exception{
		PersonAddress pa = Context.getPersonService().getPersonAddressByUuid("3350d0b5-821c-4e5e-ad1d-a9bce331e118");
		assertTrue("current personAddress should contain a person which is a cglib proxy", HibernateProxy.class.isAssignableFrom(pa.getPerson().getClass()));
		String xmlOutput = Context.getSerializationService().serialize(pa, XStreamShortSerializer.class);
		XMLAssert.assertXpathEvaluatesTo("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", "/personAddress/person/@uuid", xmlOutput);
		XMLAssert.assertXpathNotExists("/personAddress/person/*", xmlOutput);
	}
	
	/**
	 * 
	 * should fully deserialize a short-serialized object while it exists sole as the root node of serialized xml string
	 * 
	 * @throws Exception 
	 * 
	 */
	@Test
	public void shouldFullyDeserializeObject() throws Exception{
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<personAttributeType id=\"1\" uuid=\"b3b6d540-a32e-44c7-91b3-292d97667518\" retired=\"true\">\n");
		xmlBuilder.append("  <name>Race</name>\n");
		xmlBuilder.append("  <description>Group of persons related by common descent or heredity</description>\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2007-05-04 09:59:23 CST</dateCreated>\n");
		xmlBuilder.append("  <dateRetired class=\"sql-timestamp\" id=\"4\">2008-08-15 00:00:00 CST</dateRetired>\n");
		xmlBuilder.append("  <retiredBy />\n");
		xmlBuilder.append("  <retireReason>test</retireReason>\n");
		xmlBuilder.append("  <personAttributeTypeId>1</personAttributeTypeId>\n");
		xmlBuilder.append("  <format>java.lang.String</format>\n");
		xmlBuilder.append("  <searchable>false</searchable>\n");
		xmlBuilder.append("</personAttributeType>\n");
		
		PersonAttributeType pat = Context.getSerializationService().deserialize(xmlBuilder.toString(), PersonAttributeType.class, XStreamShortSerializer.class);
		assertEquals(1, pat.getPersonAttributeTypeId().intValue());
		assertEquals("b3b6d540-a32e-44c7-91b3-292d97667518", pat.getUuid());
	}
	
	/**
	 * 
	 * should shortly deserialize a short-serialized object while it exists as a child node of serialized xml string
	 * 
	 * @throws Exception 
	 * 
	 */
	@Test
	public void shouldShortlyDeserializeObject() throws Exception{
		StringBuilder xmlBuilder = new StringBuilder();
		xmlBuilder.append("<personAttribute id=\"1\" uuid=\"0768f3da-b692-44b7-a33f-abf2c450474e\" voided=\"false\">\n");
		xmlBuilder.append("  <creator id=\"2\" uuid=\"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\"/>\n");
		xmlBuilder.append("  <dateCreated class=\"sql-timestamp\" id=\"3\">2008-08-15 15:46:47 CST</dateCreated>\n");
		xmlBuilder.append("  <personAttributeId>1</personAttributeId>\n");
		xmlBuilder.append("  <person id=\"4\" uuid=\"df8ae447-6745-45be-b859-403241d9913c\"/>\n");
		xmlBuilder.append("  <attributeType id=\"5\" uuid=\"b3b6d540-a32e-44c7-91b3-292d97667518\"/>\n");
		xmlBuilder.append("  <value></value>\n");
		xmlBuilder.append("</personAttribute>\n");
		PersonAttribute pa = Context.getSerializationService().deserialize(xmlBuilder.toString(), PersonAttribute.class, XStreamShortSerializer.class);
		assertEquals(1, pa.getAttributeType().getPersonAttributeTypeId().intValue());
		assertEquals("b3b6d540-a32e-44c7-91b3-292d97667518", pa.getAttributeType().getUuid());
	}
	
	/**
	 * 
	 * should shortly serialize a short-serialized object while it exist as the element of a Collection
	 *
	 * @throws Exception 
	 */
	@Test
	public void shouldShortlySerializeElementInCollection() throws Exception{
		/*
		 * Because for current domain objects, there are a few classes which contain a collection attribute composed by short-serialized class,
		 * here i just serialize a collection composed by short-serialized class for testing
		 */
		Collection<User> col = new ArrayList<User>();
		User user1 = Context.getUserService().getUser(501);
		User user2 = Context.getUserService().getUser(502);
		col.add(user1);
		col.add(user2);
		String xmlOutput = Context.getSerializationService().serialize(col, XStreamShortSerializer.class);
		XMLAssert.assertXpathEvaluatesTo(user1.getUuid(), "/list/user[1]/@uuid", xmlOutput);
		XMLAssert.assertXpathNotExists("/list/user[1]/*", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(user2.getUuid(), "/list/user[2]/@uuid", xmlOutput);
		XMLAssert.assertXpathNotExists("/list/user[2]/*", xmlOutput);
	}
	
	/**
	 * 
	 * should shortly serialize a short-serialized object while it exist as the key/value of a Map
	 *
	 * @throws Exception 
	 */
	@Test
	public void shouldShortlySerializeKeyInMap() throws Exception{
		/*
		 * Because for key and value's short serialization is equal, here just test for while a short-serialized class exist as key in a map
		 */
		Map<User,String> map = new LinkedHashMap<User, String>();
		User user1 = Context.getUserService().getUser(501);
		User user2 = Context.getUserService().getUser(502);
		map.put(user1, user1.getUsername());
		map.put(user2, user2.getUsername());
		String xmlOutput = Context.getSerializationService().serialize(map, XStreamShortSerializer.class);
		XMLAssert.assertXpathEvaluatesTo(user1.getUuid(), "/linked-hash-map/entry[1]/user/@uuid", xmlOutput);
		XMLAssert.assertXpathNotExists("/linked-hash-map/entry[1]/user/*", xmlOutput);
		XMLAssert.assertXpathEvaluatesTo(user2.getUuid(), "/linked-hash-map/entry[2]/user/@uuid", xmlOutput);
		XMLAssert.assertXpathNotExists("/linked-hash-map/entry[2]/user/*", xmlOutput);
	}
}
