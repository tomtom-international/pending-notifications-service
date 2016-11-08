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

package com.tomtom.services.notifications.dao.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.tomtom.services.notifications.Notification;
import com.tomtom.services.notifications.dao.NotificationDao;
import com.tomtom.services.notifications.dao.mappers.NotificationMapper;
import com.tomtom.speedtools.json.Json;
import com.tomtom.speedtools.mongodb.*;
import com.tomtom.speedtools.mongodb.mappers.MapperException;
import com.tomtom.speedtools.mongodb.mappers.MapperRegistry;
import com.tomtom.speedtools.mongodb.mappers.SchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.tomtom.speedtools.mongodb.MongoDBUtils.mongoPath;

public class NotificationDaoMongoDBImpl implements NotificationDao {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationDaoMongoDBImpl.class);

    @Nullable
    private final DBCollection collection;
    @Nullable
    private final NotificationMapper notificationMapper;
    @Nonnull
    private final MapperRegistry mapperRegistry;

    @Inject
    public NotificationDaoMongoDBImpl(
            @Nullable final MongoDB db,
            @Nonnull final MapperRegistry mapperRegistry) {
        super();
        assert mapperRegistry != null;

        this.mapperRegistry = mapperRegistry;
        if (db != null) {
            collection = db.getCollection("notifications");
            notificationMapper = mapperRegistry.findMapper(NotificationMapper.class);
            collection.createIndex(new BasicDBObject(mongoPath(notificationMapper.deviceId), 1));
        }
        else {
            collection = null;
            notificationMapper = null;
        }
        LOG.debug("NotificationDaoMongoDBImpl: Using database collection: {}", collection.getName());
    }

    @Override
    public int getNrOfDeviceIds() throws InternalDaoException {
        assert (collection != null) && (notificationMapper != null);
        final MongoDBQuery query = new MongoDBQuery().exists(notificationMapper.deviceId);
        final long count = DaoUtils.count(collection, query);
        LOG.debug("getNrOfDeviceIds: count={}", count);
        return (int) count;
    }

    @Nonnull
    @Override
    public List<String> getAllDeviceIds() throws InternalDaoException {
        assert (collection != null) && (notificationMapper != null);
        final MongoDBQuery query = new MongoDBQuery().exists(notificationMapper.deviceId);
        final List<Notification> notifications = DaoUtils.find(collection, notificationMapper, query);
        final List<String> deviceIds = new ArrayList<>(notifications.size());
        for (final Notification notification : notifications) {
            deviceIds.add(notification.getDeviceId());
        }
        LOG.debug("getAllDeviceIds: size={}", deviceIds.size());
        return deviceIds;
    }

    @Nonnull
    @Override
    public Set<String> getServiceIds(@Nonnull final String deviceId) throws EntityNotFoundException, InternalDaoException {
        assert (collection != null) && (notificationMapper != null);
        LOG.debug("getServiceIds: deviceId={}", deviceId);
        final MongoDBQuery query = new MongoDBQuery().eq(notificationMapper.deviceId, deviceId);
        final Notification notification = DaoUtils.findOne(collection, notificationMapper, query);
        return notification.getServiceIds();
    }

    @Override
    public void removeServiceIds(@Nonnull final String deviceId) throws EntityRemoveException {
        assert (collection != null) && (notificationMapper != null);
        LOG.debug("removeServiceIds: deviceId={}", deviceId);
        DaoUtils.removeEntityByField(collection, notificationMapper.deviceId, deviceId);
    }

    @Override
    public void putServiceIds(@Nonnull final String deviceId, @Nonnull final Set<String> serviceIds) throws EntityStoreException {
        assert (collection != null) && (notificationMapper != null);
        LOG.debug("putServiceIds: deviceId={}, serviceIds={}", deviceId, Json.toStringJson(serviceIds));
        final MongoDBQuery query = new MongoDBQuery().eq(notificationMapper.deviceId, deviceId);
        final Notification notification = new Notification(deviceId, serviceIds);
        final DBObject dbObject;
        try {
            final NotificationMapper mapper = mapperRegistry.getMapper(NotificationMapper.class);
            dbObject = mapper.toDb(notification);
            assert dbObject != null;
        } catch (final SchemaException | MapperException e) {
            throw new EntityStoreException("Mapper exception found", e);
        }
        DaoUtils.upsert(collection, query, dbObject);
    }
}
