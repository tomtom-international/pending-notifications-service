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

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.Assert.*;

public class ApiPendingNotificationsTest {
    private static final Logger LOG = LoggerFactory.getLogger(ApiPendingNotificationsTest.class);

    private LocalTestServer server = null;

    public void startServer() {
        server = new LocalTestServer();
        server.startServer();
    }

    @After
    public void stopServer() {
        server.stopServer();
    }

    @Test
    public void checkAllPendingNotiticationsEmpty() {
        LOG.info("checkAllPendingNotiticationsEmpty");
        startServer();
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        String actual = response.readEntity(String.class);
        assertTrue("{\"total\":0}".equals(actual) || "{\"total\":0,\"ids\":[]}".equals(actual));
    }

    @Test
    public void checkAllPendingNotiticationsAll1() {
        LOG.info("checkAllPendingNotiticationsAll1");
        startServer();
        create("x", "");
        Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":1,\"ids\":[\"x\"]}", response.readEntity(String.class));

        create("y", "");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":2,\"ids\":[\"x\",\"y\"]}", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications?count=1").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":2,\"ids\":[\"x\"]}", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications?count=1&offset=-1").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":2,\"ids\":[\"y\"]}", response.readEntity(String.class));
    }

    @Test
    public void checkAllPendingNotiticationsAll2() {
        LOG.info("checkAllPendingNotiticationsAll2");
        startServer();
        create("x", "1");
        Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":1,\"ids\":[\"x\"]}", response.readEntity(String.class));

        create("y", "2");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":2,\"ids\":[\"x\",\"y\"]}", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications?count=1").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":2,\"ids\":[\"x\"]}", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications?count=0").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        String actual = response.readEntity(String.class);
        assertTrue("{\"total\":2}".equals(actual) || "{\"total\":2,\"ids\":[]}".equals(actual));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications?count=1&offset=-1").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":2,\"ids\":[\"y\"]}", response.readEntity(String.class));
    }

    @Test
    public void checkAllPendingNotiticationsForDevice1() {
        LOG.info("checkAllPendingNotiticationsForDevice1");
        startServer();
        Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("", response.readEntity(String.class));

        create("x", "");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[]", response.readEntity(String.class));

        create("y", "");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[]", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[]", response.readEntity(String.class));
    }

    @Test
    public void checkAllPendingNotiticationsForDevice2() {
        LOG.info("checkAllPendingNotiticationsForDevice2");
        startServer();
        Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x/1").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("", response.readEntity(String.class));

        create("x", "1");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"1\"]", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x/1").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("", response.readEntity(String.class));

        create("y", "2");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"1\"]", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"2\"]", response.readEntity(String.class));

        delete("x", "");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":1,\"ids\":[\"y\"]}", response.readEntity(String.class));

        create("y", "3");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"2\",\"3\"]", response.readEntity(String.class));

        delete("y", "4");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"2\",\"3\"]", response.readEntity(String.class));

        delete("y", "3");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"2\"]", response.readEntity(String.class));

        delete("y", "2");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("", response.readEntity(String.class));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        String actual = response.readEntity(String.class);
        assertTrue("{\"total\":0}".equals(actual) || "{\"total\":0,\"ids\":[]}".equals(actual));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications?count=1").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        actual = response.readEntity(String.class);
        assertTrue("{\"total\":0}".equals(actual) || "{\"total\":0,\"ids\":[]}".equals(actual));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications?count=1&offset=-11").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        actual = response.readEntity(String.class);
        assertTrue("{\"total\":0}".equals(actual) || "{\"total\":0,\"ids\":[]}".equals(actual));

        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications?count=0").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        actual = response.readEntity(String.class);
        assertTrue("{\"total\":0}".equals(actual) || "{\"total\":0,\"ids\":[]}".equals(actual));
    }

    private void create(@Nonnull final String device, @Nonnull final String service) {
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/" + device + (service.isEmpty() ? "" : ('/' + service))).
                request().
                accept(APPLICATION_JSON_TYPE).post(null);
        assertEquals(201, response.getStatus());
    }

    private void delete(@Nonnull final String device, @Nonnull final String service) {
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/" + device + (service.isEmpty() ? "" : ('/' + service))).
                request().
                accept(APPLICATION_JSON_TYPE).delete();
        assertEquals(204, response.getStatus());
    }
}
