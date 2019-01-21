/**
 * Copyright (C) 2016, TomTom NV (http://www.tomtom.com)
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

package com.tomtom.services.notifications.dao;

import com.tomtom.speedtools.mongodb.EntityNotFoundException;
import com.tomtom.speedtools.mongodb.EntityRemoveException;
import com.tomtom.speedtools.mongodb.EntityStoreException;
import com.tomtom.speedtools.mongodb.InternalDaoException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * This class defines the data access layer. There may be different implementations of this layer, such
 * as an in-memory model, or a database backed implementation.
 */
public interface NotificationDao {

    /**
     * Return the total number of device IDs.
     *
     * @return Total number of device IDs, &gt;= 0.
     * @throws InternalDaoException Thrown if the data store throws an error.
     */
    public int getNrOfDeviceIds() throws InternalDaoException;

    /**
     * Return all device IDs.
     *
     * @return All device IDs.
     * @throws InternalDaoException Thrown if the data store throws an error.
     */
    @Nonnull
    public List<String> getAllDeviceIds() throws InternalDaoException;

    /**
     * Get all service IDs for which there is a notification pending for a specific device ID.
     *
     * @param deviceId Device ID to get service IDs for. The empty service ID "" denotes an ID-less service.
     * @return List of service IDs. May contain 1 empty service ID, "".
     * @throws EntityNotFoundException Thrown if the device ID was not found.
     * @throws InternalDaoException    Thrown if the data store throws an error.
     */
    @Nonnull
    public Set<String> getServiceIds(@Nonnull final String deviceId) throws EntityNotFoundException, InternalDaoException;

    /**
     * Remove all service IDs of pending notifications for a specific device ID.
     *
     * @param deviceId Device ID to clear all pending notifications for services for.
     * @throws EntityRemoveException Thrown if the record could not be removed.
     */
    public void removeServiceIds(@Nonnull final String deviceId) throws EntityRemoveException;

    /**
     * Create a pending notification for a service ID for a specific device ID
     *
     * @param deviceId   Device ID to create a pending notification for.
     * @param serviceIds A set of service IDs for which there are pending notifications.
     * @throws EntityStoreException Thrown if the record could not be stored.
     */
    public void putServiceIds(@Nonnull final String deviceId, @Nonnull final Set<String> serviceIds) throws EntityStoreException;
}
