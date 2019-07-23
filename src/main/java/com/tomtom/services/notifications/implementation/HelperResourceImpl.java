/**
 * Copyright (C) 2019, TomTom NV (http://www.tomtom.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomtom.services.notifications.implementation;

import com.tomtom.services.notifications.HelperResource;
import com.tomtom.services.notifications.dto.VersionDTO;
import com.tomtom.speedtools.maven.MavenProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class HelperResourceImpl implements HelperResource {
    private static final Logger LOG = LoggerFactory.getLogger(HelperResourceImpl.class);

    /**
     * These properties (actually only 1) gets injected by Google Guice from the properties file supplied
     * in the "resources" directory. It is used for the service version number, which equals the POM version.
     */
    @Nonnull
    private final MavenProperties mavenProperties;

    /**
     * The root resources gets the Maven POM property injected by Google Guice at startup.
     *
     * @param mavenProperties POM version.
     */
    @Inject
    public HelperResourceImpl(@Nonnull final MavenProperties mavenProperties) {
        assert mavenProperties != null;
        this.mavenProperties = mavenProperties;
    }

    @Override
    @Nonnull
    public String getHelpHTML() {
        LOG.info("getHelpHTML: show help page", mavenProperties.getPomVersion());
        return "<html><pre>\n" +
                "PENDING NOTIFICATIONS SERVICE (" + mavenProperties.getPomVersion() + ")\n" +
                "-----------------------------\n\n" +

                "The service exposes REST methods to manage pending notifications for identities.\n\n" +

                "Called by devices:\n" +
                "  GET    /notifications/{deviceId}                 -- get pending notifications for a device\n" +
                "                                                      (body contains service IDs, if available)\n" +
                "Called by back-end services:\n" +
                "  POST   /notifications/{deviceId}                 -- create a pending notification for a device\n" +
                "  POST   /notifications/{deviceId}[/{serviceId}]   -- create a pending notification for a device, for a service\n" +
                "  POST   /notifications/{deviceId}                 -- delete all pending notifications for a device\n" +
                "  DELETE /notifications/{deviceId}[/{serviceId}]   -- delete a specific pending notifications for a device\n" +
                "                                                      (removing the last one removes the entire entry))\n\n" +

                "Provided for deployment/monitoring:\n" +
                "  GET    /notifications[?offset={x}&count={y}]     -- get all IDs that have pending notifications\n" +
                "  GET    /                                         -- produces this help text\n" +
                "  GET    /version                                  -- returns the service version\n" +
                "  GET    /status                                   -- returns 204 if all OK\n" +

                "</pre></html>\n\n";
    }

    @Override
    public void getVersion(@Nonnull final AsyncResponse response) {
        assert response != null;

        // Just return version number.
        final String pomVersion = mavenProperties.getPomVersion();
        LOG.info("getVersion: POM version={}", pomVersion);

        // Create the data transfer object (JSON result).
        final VersionDTO result = new VersionDTO(pomVersion);

        // Make sure we return a valid object.
        result.validate();

        // And include it with a 200 return code.
        response.resume(Response.ok(result).build());
    }

    @Override
    public void getStatus(@Nonnull final AsyncResponse  response) {
        assert response != null;
        LOG.info("getStatus: all OK");

        // Simply return 204.
        response.resume(Response.status(Status.NO_CONTENT).build());
    }
}
