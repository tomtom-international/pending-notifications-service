/**
 * Copyright (C) 2016, TomTom NV (http://www.tomtom.com)
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

package com.tomtom.services.notifications.dao;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.tomtom.services.notifications.dao.mappers.NotificationsMapperRegistry;
import com.tomtom.speedtools.mongodb.MongoConnectionCache;
import com.tomtom.speedtools.mongodb.MongoDB;
import com.tomtom.speedtools.mongodb.MongoDBConnectionException;
import com.tomtom.speedtools.mongodb.mappers.MapperRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.UnknownHostException;

/**
 * Google Guice Module that contains all DAO's using a MongoDB implementation.
 */
public class DaoModule implements Module {
    private static final Logger LOG = LoggerFactory.getLogger(DaoModule.class);

    private static final int MONGODB_TIMEOUT_SECS = 30;

    @Override
    public void configure(@Nonnull final Binder binder) {
        assert binder != null;

        // Bind singletons.
        binder.bind(DatabaseProperties.class).in(Singleton.class);
        binder.bind(MapperRegistry.class).to(NotificationsMapperRegistry.class).in(Singleton.class);
        binder.bind(NotificationDao.class).to(NotificationDaoImpl.class).in(Singleton.class);
    }

    @Nonnull
    @Provides
    @Singleton
    public static MongoDB provideMongoDB(@Nonnull final DatabaseProperties properties) {
        assert properties != null;

        try {
            LOG.info("provideMongoDB: Creating MongoDB connection: {}", properties.getServers());
            final Mongo mongo = MongoConnectionCache.getMongoDB(properties.getServers(), MONGODB_TIMEOUT_SECS, properties.getUserName(), properties.getDatabase(), properties.getPassword());

            // The address may not be available yet, if we connect like this.
            final ServerAddress address = mongo.getAddress();
            LOG.info("provideMongoDB: MongoDB connection established: {}",
                    properties.getDatabase() + " at " +
                            properties.getServers() + " [master:" +
                            ((address == null) ? "(unknown)" : (address.getHost() + ':' + mongo.getAddress().getPort())) +
                            "] (MongoDB version " + ')');
            final MongoDB db = getDB(mongo, properties.getDatabase(), "", properties.getUserName(), properties.getPassword());
            return db;
        } catch (final UnknownHostException e) {
            throw new MongoDBConnectionException("Cannot find any of MongoDB servers: " + properties.getServers(), e);
        }
    }

    /**
     * Get the MongoDB database instance.
     *
     * @param mongo           MongoDB handle.
     * @param databaseName    Database name.
     * @param subDatabaseName Sub-database name (for unit tests). If the subdatabase name is empty, the database name is
     *                        used, otherwise the subdatabase name is appended to. Example, database="TEST", subdatabase
     *                        name="xyz" would produce "TEST_xyz".
     * @param userName        Username for authentication.
     * @param password        Password for authentication.
     * @return MongoDB instance.
     * @throws MongoDBConnectionException If something went wrong.
     */
    @Nonnull
    protected static MongoDB getDB(@Nonnull final Mongo mongo, @Nonnull final String databaseName,
                                   @Nonnull final String subDatabaseName,
                                   @Nonnull final String userName, @Nonnull final String password) {
        assert mongo != null;
        assert databaseName != null;
        assert subDatabaseName != null;
        assert userName != null;
        assert password != null;

        final MongoDB mongoDb = new MongoDB(mongo.getDB(databaseName), subDatabaseName);
        return mongoDb;
    }
}
