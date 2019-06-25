/*******************************************************************************
 * Copyright (c) 2019 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.jackson.dataobject.enumeration;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.eclipse.scout.rt.dataobject.IDataObjectMapper;
import org.eclipse.scout.rt.dataobject.IPrettyPrintDataObjectMapper;
import org.eclipse.scout.rt.dataobject.fixture.FixtureEnum;
import org.eclipse.scout.rt.jackson.dataobject.fixture.TestEntityWithEnumDo;
import org.eclipse.scout.rt.jackson.testing.DataObjectSerializationTestHelper;
import org.eclipse.scout.rt.platform.BEANS;
import org.junit.Before;
import org.junit.Test;

public class EnumSerializationTest {

  protected DataObjectSerializationTestHelper m_testHelper;
  protected IDataObjectMapper m_dataObjectMapper;

  @Before
  public void before() {
    m_dataObjectMapper = BEANS.get(IPrettyPrintDataObjectMapper.class);
    m_testHelper = BEANS.get(DataObjectSerializationTestHelper.class);
  }

  @Test
  public void testSerializeDeserialize_EntityWithEnum() throws Exception {
    TestEntityWithEnumDo entity = BEANS.get(TestEntityWithEnumDo.class);
    entity.withValue(FixtureEnum.ONE);
    String json = m_dataObjectMapper.writeValue(entity);

    String expectedJson = m_testHelper.readResourceAsString(toURL("TestEntityWithEnumDo.json"));
    m_testHelper.assertJsonEquals(expectedJson, json);

    TestEntityWithEnumDo marshalled = m_dataObjectMapper.readValue(expectedJson, TestEntityWithEnumDo.class);
    assertEquals(FixtureEnum.ONE, marshalled.getValue());
  }

  protected URL toURL(String resourceName) {
    return EnumSerializationTest.class.getResource(resourceName);
  }
}