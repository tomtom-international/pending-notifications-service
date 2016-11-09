/**
 * Copyright (C) 2016, TomTom International BV (http://www.tomtom.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void checkAllPendingNotiticationsEmpty() throws Exception {
        LOG.info("checkAllPendingNotiticationsEmpty");
        startServer();
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":0,\"ids\":[]}", response.readEntity(String.class));
    }

    @Test
    public void checkAllPendingNotiticationsAll1() throws Exception {
        LOG.info("checkAllPendingNotiticationsAll1");
        startServer();
        add("x", "");
        Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":1,\"ids\":[\"x\"]}", response.readEntity(String.class));

        add("y", "");
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
    public void checkAllPendingNotiticationsAll2() throws Exception {
        LOG.info("checkAllPendingNotiticationsAll2");
        startServer();
        add("x", "1");
        Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"total\":1,\"ids\":[\"x\"]}", response.readEntity(String.class));

        add("y", "2");
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
    public void checkAllPendingNotiticationsForDevice1() throws Exception {
        LOG.info("checkAllPendingNotiticationsForDevice1");
        startServer();
        Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("", response.readEntity(String.class));

        add("x", "");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/x").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[]", response.readEntity(String.class));

        add("y", "");
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
    public void checkAllPendingNotiticationsForDevice2() throws Exception {
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

        add("x", "1");
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

        add("y", "2");
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

        add("y", "3");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"2\",\"3\"]", response.readEntity(String.class));

        remove("y", "4");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"2\",\"3\"]", response.readEntity(String.class));

        remove("y", "3");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[\"2\"]", response.readEntity(String.class));

        remove("y", "2");
        response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("", response.readEntity(String.class));
    }

    private void add(@Nonnull final String device, @Nonnull final String service) {
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/" + device + (service.isEmpty() ? "" : ('/' + service))).
                request().
                accept(APPLICATION_JSON_TYPE).post(null);
        assertEquals(201, response.getStatus());
    }

    private void remove(@Nonnull final String device, @Nonnull final String service) {
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/notifications/" + device + (service.isEmpty() ? "" : ('/' + service))).
                request().
                accept(APPLICATION_JSON_TYPE).delete();
        assertEquals(204, response.getStatus());
    }
}
