/**
 * Copyright (C) 2019, TomTom NV (http://www.tomtom.com)
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

import com.tomtom.services.notifications.dao.memory.NotificationDaoMemoryImpl;
import com.tomtom.services.notifications.dao.mongodb.NotificationDaoMongoDBImpl;
import com.tomtom.speedtools.mongodb.EntityNotFoundException;
import com.tomtom.speedtools.mongodb.EntityRemoveException;
import com.tomtom.speedtools.mongodb.EntityStoreException;
import com.tomtom.speedtools.mongodb.InternalDaoException;
import com.tomtom.speedtools.mongodb.mappers.MapperRegistry;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

public class NotificationDaoImpl implements NotificationDao {

    @Nonnull
    private final NotificationDao currentNotificationDao;

    @Inject
    public NotificationDaoImpl(
            @Nonnull final DatabaseProperties databaseProperties,
            @Nonnull final MapperRegistry mapperRegistry) {
        super();
        assert mapperRegistry != null;

        if (databaseProperties.getUseInMemory()) {
            this.currentNotificationDao = new NotificationDaoMemoryImpl();
        } else {
            this.currentNotificationDao = new NotificationDaoMongoDBImpl(
                    DaoModule.provideMongoDB(databaseProperties), mapperRegistry);
        }
    }

    @Override
    public int getNrOfDeviceIds() throws InternalDaoException {
        return currentNotificationDao.getNrOfDeviceIds();
    }

    @Nonnull
    @Override
    public List<String> getAllDeviceIds() throws InternalDaoException {
        return currentNotificationDao.getAllDeviceIds();
    }

    @Nonnull
    @Override
    public Set<String> getServiceIds(@Nonnull final String deviceId) throws EntityNotFoundException, InternalDaoException {
        return currentNotificationDao.getServiceIds(deviceId);
    }

    @Override
    public void removeServiceIds(@Nonnull final String deviceId) throws EntityRemoveException {
        currentNotificationDao.removeServiceIds(deviceId);
    }

    @Override
    public void putServiceIds(@Nonnull final String deviceId, @Nonnull final Set<String> serviceIds) throws EntityStoreException {
        currentNotificationDao.putServiceIds(deviceId, serviceIds);
    }
}
