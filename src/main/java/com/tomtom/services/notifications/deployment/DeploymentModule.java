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

package com.tomtom.services.notifications.deployment;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Binder;
import com.tomtom.services.notifications.HelperResource;
import com.tomtom.services.notifications.PendingNotificationsResource;
import com.tomtom.services.notifications.implementation.HelperResourceImpl;
import com.tomtom.services.notifications.implementation.PendingNotificationsResourceImpl;
import com.tomtom.speedtools.guice.GuiceConfigurationModule;
import com.tomtom.speedtools.json.Json;
import com.tomtom.speedtools.rest.GeneralExceptionMapper;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;


/**
 * This class defines the deployment configuration for Google Guice.
 *
 * The deployment module "bootstraps" the whole Guice injection process.
 *
 * It bootstraps the Guice injection and specifies the property files to be read. It also needs to bind the tracer, so
 * they can be used early on in the app. Finally, it can bind a "startup check" (example provided) as an eager
 * singleton, so the system won't start unless a set of basic preconditions are fulfilled.
 *
 * The "speedtools.default.properties" is required, but its values may be overridden in other property files.
 */
public class DeploymentModule extends GuiceConfigurationModule {

    /**
     * The deployment module defines which property files must be read. The property files are
     * specified as 1 primary property file, which is included in the WAR file and which specifies
     * ALL properties that must be set, potentially with '{empty}' values for property values to be set in
     * secondary property files, which may be left out of the WAR and which are added during deployment
     * to the class path.
     *
     * This allows you to make sure the service ALWAYS has all properties that are required and expected
     * (because the system won't startup if properties are missing) and you can still specify environment-specific
     * properties outside of your WAR.
     */
    public DeploymentModule() {
        super("classpath:pending-notifications-service.properties",
                "classpath:pending-notifications-service-secret.properties");
    }

    @Override
    public void configure(@Nonnull final Binder binder) {
        assert binder != null;
        super.configure(binder);

        // Make sure incorrect JSON doesn't return a HTTP 500, but HTTP 400 code.
        GeneralExceptionMapper.addCustomException(JsonParseException.class, false, Status.BAD_REQUEST);

        // Bind APIs to their implementation.
        binder.bind(HelperResource.class).to(HelperResourceImpl.class).in(Singleton.class);
        binder.bind(PendingNotificationsResource.class).to(PendingNotificationsResourceImpl.class).in(Singleton.class);

        // Bind start-up checking class (example).
        binder.bind(StartupCheck.class).asEagerSingleton();

        final ObjectMapper jsonMapper = Json.getCurrentJsonObjectMapper();
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
}
