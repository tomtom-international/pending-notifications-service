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

package com.tomtom.services.notifications.dao;

import com.tomtom.speedtools.guice.HasProperties;
import com.tomtom.speedtools.guice.InvalidPropertyValueException;
import com.tomtom.speedtools.mongodb.MongoConnectionCache;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;

public final class DatabaseProperties implements HasProperties {

    private final boolean useInMemory;
    @Nonnull
    private final String servers;
    @Nonnull
    private final String database;
    @Nonnull
    private final String userName;
    @Nonnull
    private final String password;

    /**
     * MongoDB properties.
     *
     * @param useInMemory True if we should use the in-memory database, rather than an external database.
     * @param servers     Host names and port numbers of MongoDB engine, formatted as "hostname:port[,hostname:port]*".
     *                    This can be DNS name or IP number. The port number must be in range: [1, 65535]. Cannot be
     *                    empty.
     * @param database    Database name. Cannot be empty.
     * @param userName    Database userName. Cannot be empty.
     * @param password    Database password. Cannot be empty.
     */
    @Inject
    public DatabaseProperties(
            @Named("Database.useInMemory") final boolean useInMemory,
            @Named("Database.servers") @Nonnull final String servers,
            @Named("Database.database") @Nonnull final String database,
            @Named("Database.userName") @Nonnull final String userName,
            @Named("Database.password") @Nonnull final String password)
            throws InvalidPropertyValueException {
        assert servers != null;
        assert database != null;
        assert userName != null;
        assert password != null;

        this.useInMemory = useInMemory;
        if (!useInMemory) {

            // Check property values.
            if (servers.isEmpty()) {
                throw new InvalidPropertyValueException("Database.servers cannot be empty.");
            }
            try {
                MongoConnectionCache.getMongoDBServerAddresses(servers);
            } catch (final IllegalArgumentException e) {
                throw new InvalidPropertyValueException("Database.servers has an invalid value: " + e.getMessage());
            } catch (final UnknownHostException ignored) {
                throw new InvalidPropertyValueException("Database.servers has an invalid value (unknown host)");
            }

            if (database.isEmpty()) {
                throw new InvalidPropertyValueException("Database.database cannot be empty.");
            }

            if (userName.isEmpty()) {
                throw new InvalidPropertyValueException("Database.userName cannot be empty.");
            }

            if (password.isEmpty()) {
                throw new InvalidPropertyValueException("Database.password cannot be empty.");
            }
        }

        this.servers = servers;
        this.database = database;
        this.userName = userName;
        this.password = password;
    }

    public boolean getUseInMemory() {
        return useInMemory;
    }

    @Nonnull
    public String getServers() {
        return servers;
    }

    @Nonnull
    public String getDatabase() {
        return database;
    }

    @Nonnull
    public String getUserName() {
        return userName;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }
}
