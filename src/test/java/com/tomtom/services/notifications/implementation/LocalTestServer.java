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
import com.tomtom.services.notifications.dao.NotificationDao;
import com.tomtom.services.notifications.dao.memory.NotificationDaoMemoryImpl;
import com.tomtom.speedtools.maven.MavenProperties;
import com.tomtom.speedtools.rest.Reactor;
import com.tomtom.speedtools.rest.ResourceProcessor;
import com.tomtom.speedtools.testutils.SimpleExecutionContext;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import scala.concurrent.ExecutionContext;

import javax.annotation.Nonnull;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class LocalTestServer {
    private static final int PORT = 8081;
    private static final String HOST = "http://localhost:";

    final private TJWSEmbeddedJaxrsServer server;
    private final int port;

    public LocalTestServer() {
        this.port = PORT;
        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(port);
    }

    @Before
    public void startServer() {
        final DatabaseProperties databaseProperties = new DatabaseProperties(true, "", "", "", "");
        final MavenProperties mavenProperties = new MavenProperties("1.0.0-TEST");
        final NotificationDao notificationDao = new NotificationDaoMemoryImpl();

        // Create a simple ResourceProcessor, required for implementation of REST service using the SpeedTools framework.
        final Reactor reactor = new Reactor() {
            @Nonnull
            @Override
            public ExecutionContext getExecutionContext() {
                return SimpleExecutionContext.getInstance();
            }

            // This method is stubbed and never used.
            @Nonnull
            @Override
            public DateTime getSystemStartupTime() {
                return new DateTime();
            }
        };
        final ResourceProcessor resourceProcessor = new ResourceProcessor(reactor);

        // Add root resource.
        server.getDeployment().getResources().add(new HelperResourceImpl(
                mavenProperties
        ));

        // Add notifications resource.
        server.getDeployment().getResources().add(new PendingNotificationsResourceImpl(
                resourceProcessor,
                databaseProperties,
                notificationDao
        ));

        server.start();
    }

    @After
    public void stopServer() {
        server.stop();
    }

    @Nonnull
    public String getHost() {
        return HOST + port;
    }
}
