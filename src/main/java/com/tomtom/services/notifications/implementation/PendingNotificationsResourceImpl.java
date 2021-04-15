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

import akka.dispatch.Futures;
import com.tomtom.services.notifications.PendingNotificationsResource;
import com.tomtom.services.notifications.dao.DatabaseProperties;
import com.tomtom.services.notifications.dao.NotificationDao;
import com.tomtom.services.notifications.dto.AllPendingNotificationsDTO;
import com.tomtom.services.notifications.dto.ValuesDTO;
import com.tomtom.speedtools.apivalidation.exceptions.ApiIntegerOutOfRangeException;
import com.tomtom.speedtools.mongodb.EntityNotFoundException;
import com.tomtom.speedtools.mongodb.EntityRemoveException;
import com.tomtom.speedtools.rest.ResourceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PendingNotificationsResourceImpl implements PendingNotificationsResource {
    private static final Logger LOG = LoggerFactory.getLogger(PendingNotificationsResourceImpl.class);

    @Nonnull
    private final NotificationDao notificationDao;

    /**
     * This is the (Akka-backed) web service resource processor to execute the (lambda) service
     * implementations.
     */
    @Nonnull
    private final ResourceProcessor processor;

    /**
     * The constructor gets the (Akka-backed) resource processor injected from Guice. The instance
     * of the processor is defined in the "DeploymentModule" elsewhere in the project. It is a
     * singleton.
     *
     * @param processor          The (Akka-backed) resource processor.
     * @param databaseProperties Data base properties.
     * @param notificationDao    the DAO.
     */
    @Inject
    public PendingNotificationsResourceImpl(
            @Nonnull final ResourceProcessor processor,
            @Nonnull final DatabaseProperties databaseProperties,
            @Nonnull final NotificationDao notificationDao) {
        assert processor != null;
        assert databaseProperties != null;
        assert notificationDao != null;

        // Remember the injected processor.
        this.processor = processor;
        this.notificationDao = notificationDao;
    }

    @Override
    public void getAllPendingNotifications(
            final int offset,
            final int count,
            @Nonnull final AsyncResponse response) {
        assert response != null;

        /*
         * Schedule actual execution of the service call on a processor (Akka) thread.
         * The processor want to get the method name for logging purposes, as well as a reference
         * to the local logger, so log messages can be related to this class, rather than the
         * processor class (which would be confusing in reading the logs).
         */
        processor.process("getAllPendingNotifications", LOG, response, () -> {
            LOG.info("getAllPendingNotifications: total={}", notificationDao.getNrOfDeviceIds());

            // Check value of count.
            if (count < 0) {
                throw new ApiIntegerOutOfRangeException(PARAM_COUNT, count, 0, Integer.MAX_VALUE);
            }
            assert count >= 0;

            // Get all device IDs.
            //noinspection ToArrayCallWithZeroLengthArrayArgument
            final List<String> deviceIds = notificationDao.getAllDeviceIds();

            // Calculate the sub-list to return.
            final int fromIndex = (offset < 0) ? Math.max(0, deviceIds.size() + offset) : Math.min(deviceIds.size(), offset);
            final int toIndex = Math.min(deviceIds.size(), fromIndex + count);

            // Create a result object (data transfer object) containing all IDs.
            final List<String> subList = deviceIds.subList(fromIndex, toIndex);
            final AllPendingNotificationsDTO result = new AllPendingNotificationsDTO(notificationDao.getNrOfDeviceIds(), subList);

            // Validate the result, to make sure we sent out valid stuff.
            result.validate();

            // Make sure the response contains the right HTTP code and body.
            response.resume(Response.status(Status.OK).entity(result).build());

            /**
             * Return successfully from the processor thread. Note the 'response' MUST be set before
             * returning or the processor will timeout, waiting for the async result to be set "somewhere",
             */
            return Futures.successful(null);
        });
    }

    @Override
    public void getPendingNotificationsForDeviceAndService(
            @Nonnull final String deviceId,
            @Nonnull final String serviceId,
            @Nonnull final AsyncResponse response) {
        assert deviceId != null;
        assert serviceId != null;
        assert response != null;

        processor.process("getPendingNotificationsForDeviceAndService", LOG, response, () -> {

            // Check if a pending notification exists.
            try {

                // Get all service IDs for a specific device ID.
                final Set<String> serviceIds = notificationDao.getServiceIds(deviceId);
                final boolean contains = serviceIds.contains(serviceId);
                LOG.info("getPendingNotificationsForDeviceAndService: deviceId={}, serviceId={}, contains={}", deviceId, serviceId, contains);
                if (contains) {

                    // Return 200 if it exists.
                    response.resume(Response.status(Status.OK).build());
                } else {

                    // Return 404 if no notifications exist for this device for this specific service.
                    response.resume(Response.status(Status.NOT_FOUND).build());
                }
            } catch (final EntityNotFoundException ignored) {

                // Return 404 if the device doesn't exist.
                response.resume(Response.status(Status.NOT_FOUND).build());
            }
            return Futures.successful(null);
        });
    }

    @Override
    public void getPendingNotificationsForDevice(
            @Nonnull final String deviceId,
            @Nonnull final AsyncResponse response) {
        assert deviceId != null;
        assert response != null;

        processor.process("getPendingNotificationsForDevice", LOG, response, () -> {

            // Check if a pending notification exists.
            try {

                // Get all service IDs for this device ID.
                final Set<String> serviceIds = notificationDao.getServiceIds(deviceId);
                LOG.info("getPendingNotificationsForDevice: deviceId={}, serviceIds={}", deviceId, serviceIds);
                final ValuesDTO result = new ValuesDTO(serviceIds);

                // Validate the result, to make sure we sent out valid stuff.
                result.validate();

                // Return 200 if it exists.
                response.resume(Response.status(Status.OK).entity(result).build());
            } catch (final EntityNotFoundException ignored) {

                // Return 404 if the device doesn't exist.
                response.resume(Response.status(Status.NOT_FOUND).build());
            }
            return Futures.successful(null);
        });
    }

    @Override
    public void createPendingNotificationForDeviceAndService(
            @Nonnull final String deviceId,
            @Nullable final String serviceId,
            @Nonnull final AsyncResponse response) {
        assert deviceId != null;
        assert response != null;

        processor.process("createPendingNotificationForDeviceAndService", LOG, response, () -> {
            LOG.info("createPendingNotificationForDeviceAndService: deviceId={}, serviceId={}", deviceId, serviceId);

            // Create a notification for a specific service (for a specific device).
            Set<String> serviceIds;
            try {
                serviceIds = notificationDao.getServiceIds(deviceId);
            } catch (final EntityNotFoundException ignored) {
                serviceIds = new HashSet<>();
            }

            // The ID-less or nameless service is represented by an empty set.
            if (serviceId != null) {
                serviceIds.add(serviceId);
            }
            notificationDao.putServiceIds(deviceId, serviceIds);

            response.resume(Response.status(Status.CREATED).build());
            return Futures.successful(null);
        });
    }

    @Override
    public void createPendingNotificationForDevice(
            @Nonnull final String deviceId,
            @Nonnull final AsyncResponse response) {
        assert deviceId != null;
        assert response != null;

        // Create a nameless service notification.
        createPendingNotificationForDeviceAndService(deviceId, null, response);
    }

    @Override
    public void deletePendingNotificationsForDeviceAndService(
            @Nonnull final String deviceId,
            @Nonnull final String serviceId,
            @Nonnull final AsyncResponse response) {
        assert deviceId != null;
        assert response != null;

        processor.process("deletePendingNotificationsForDeviceAndService", LOG, response, () -> {
            LOG.info("deletePendingNotificationsForDeviceAndService: deviceId={}, serviceId", deviceId, serviceId);

            // Delete the notification for a specific service (for a specific device).
            try {
                final Set<String> serviceIds = notificationDao.getServiceIds(deviceId);

                // Remove service from list; it's OK if the serviceId isn't present.
                serviceIds.remove(serviceId);

                // Remove device if list has become empty.
                if (serviceIds.isEmpty()) {
                    notificationDao.removeServiceIds(deviceId);
                }
            } catch (final EntityNotFoundException | EntityRemoveException ignored) {
                // Ignore.
            }

            response.resume(Response.status(Status.NO_CONTENT).build());
            return Futures.successful(null);
        });
    }

    @Override
    public void deletePendingNotificationsForDevice(
            @Nonnull final String deviceId,
            @Nonnull final AsyncResponse response) {
        assert deviceId != null;
        assert response != null;

        processor.process("deletePendingNotificationsForDevice", LOG, response, () -> {
            LOG.info("deletePendingNotificationsForDevice: deviceId={}", deviceId);

            // Delete all notifications for a specific device.
            try {
                notificationDao.removeServiceIds(deviceId);
            } catch (final EntityRemoveException ignored) {
                // Ignored.
            }
            response.resume(Response.status(Status.NO_CONTENT).build());
            return Futures.successful(null);
        });
    }
}
