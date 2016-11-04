/**
 * Copyright (C) 2016, TomTom International BV (http://www.tomtom.com)
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

package com.tomtom.services.notifications;

import javax.annotation.Nonnull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

/**
 * This class defines additional methods for the pending notifications service, such as providing a developer help page
 * ("human readable service discovery"), the service version and a service status call, to be used for
 * service monitoring.
 */
@Path("/pending")
public interface HelperResource {

    /**
     * This method provides help info as HTML.
     *
     * @return Returns help text as HTML.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Nonnull
    String getHelpHTML();

    /**
     * This method returns the current version of the application. This is primarily intended for service
     * management, to know which version is running on which service node.
     *
     * Return HTTP status 200.
     *
     * @param response Version, {@link com.tomtom.services.notifications.dto.VersionDTO}.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("version")
    void getVersion(@Suspended @Nonnull AsyncResponse response);

    /**
     * This method returns whether the service is operational or not (status code 204 is OK).
     *
     * @param response Returns a version number as JSON.
     */
    @GET
    @Path("status")
    void getStatus(@Suspended @Nonnull AsyncResponse response);
}
