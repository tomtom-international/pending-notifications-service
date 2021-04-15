/*
 * Copyright (C) 2012-2021, TomTom (http://tomtom.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomtom.services.notifications.implementation;

import com.tomtom.services.notifications.dao.DatabaseProperties;
import com.tomtom.speedtools.guice.InvalidPropertyValueException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabasePropertiesTest {
    private static final Logger LOG = LoggerFactory.getLogger(DatabasePropertiesTest.class);

    @Test
    public void testDatabaseProperties() {
        LOG.info("testDatabaseProperties");
        DatabaseProperties databaseProperties = new DatabaseProperties(false, "1:1", "2", "3", "4");
        Assert.assertEquals(false, databaseProperties.getUseInMemory());
        Assert.assertEquals("1:1", databaseProperties.getServers());
        Assert.assertEquals("2", databaseProperties.getDatabase());
        Assert.assertEquals("3", databaseProperties.getUserName());
        Assert.assertEquals("4", databaseProperties.getPassword());

        databaseProperties = new DatabaseProperties(true, "", "", "", "");
        Assert.assertEquals(true, databaseProperties.getUseInMemory());
        Assert.assertEquals("", databaseProperties.getServers());
        Assert.assertEquals("", databaseProperties.getDatabase());
        Assert.assertEquals("", databaseProperties.getUserName());
        Assert.assertEquals("", databaseProperties.getPassword());
    }

    @Test(expected = InvalidPropertyValueException.class)
    public void testDatabasePropertiesError1() {
        LOG.info("testDatabasePropertiesError1");
        final DatabaseProperties databaseProperties = new DatabaseProperties(false, "", "2", "3", "4");
        Assert.assertNull(databaseProperties);
    }

    @Test(expected = InvalidPropertyValueException.class)
    public void testDatabasePropertiesError2() {
        LOG.info("testDatabasePropertiesError2");
        final DatabaseProperties databaseProperties = new DatabaseProperties(false, "1:1", "", "3", "4");
        Assert.assertNull(databaseProperties);
    }

    @Test(expected = InvalidPropertyValueException.class)
    public void testDatabasePropertiesError3() {
        LOG.info("testDatabasePropertiesError3");
        final DatabaseProperties databaseProperties = new DatabaseProperties(false, "1:1", "2", "", "4");
        Assert.assertNull(databaseProperties);
    }

    @Test(expected = InvalidPropertyValueException.class)
    public void testDatabasePropertiesError4() {
        LOG.info("testDatabasePropertiesError4");
        final DatabaseProperties databaseProperties = new DatabaseProperties(false, "1:1", "2", "3", "");
        Assert.assertNull(databaseProperties);
    }
}
