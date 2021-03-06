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


package com.tomtom.services.notifications;

import com.tomtom.services.notifications.dto.AllPendingNotificationsDTO;
import com.tomtom.services.notifications.dto.ValuesDTO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

/**
 * This class defines the main REST API methods for the service.
 */
@Path("/notifications")
public interface PendingNotificationsResource {

    String PARAM_DEVICE_ID = "deviceId";
    String PARAM_SERVICE_ID = "serviceId";
    String PARAM_COUNT = "count";
    String PARAM_OFFSET = "offset";

    String DEFAULT_OFFSET = "0";
    String DEFAULT_COUNT = "1000";

    /**
     * Get all pending notifications, for all IDs. The returned body contains a list of all IDs with pending notifications
     * calls (and an indication of the total number of notifications).
     *
     * This would normally be an authorized call; not available externally. Perhaps it would not even be needed.
     * It was added here for REST consistency of the '/notifications' collection handling.
     *
     * @param offset   Return values from 'offset'. Range: &gt;= 0 counts from start, &lt; 0 counts from end.
     * @param count    Return 'count' values at most. Range: &gt;= 0.
     * @param response List of IDs that have pending notifications, with a total count of devices,
     *                 see {@link AllPendingNotificationsDTO}.
     */
    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    void getAllPendingNotifications(
            @QueryParam(PARAM_OFFSET) @DefaultValue(DEFAULT_OFFSET) final int offset,
            @QueryParam(PARAM_COUNT) @DefaultValue(DEFAULT_COUNT) final int count,
            @Suspended @Nonnull AsyncResponse response);

    /**
     * Get the pending notifications for a specific device. The result of this service call is just an HTTP result code.
     * possibly with a body which contains a list of service IDs.
     *
     * The returned list may be empty, which means that a ID-less (or nameless) service notification was POST-ed
     * before. Note that notifications with and without an ID should not normally not be mixed, as notifications
     * for ID-less services get lost when deleting the last notification for a service with an ID.
     *
     * This method would be available unauthenticated, as an HTTP service (!), externally.
     *
     * The reason for exposing this as HTTP, rather than HTTPS, is that it would allow a client to request the
     * pending notifications state with as few TCP packets as possible, which is much less than with HTTPS.
     *
     * Return codes:
     * 200 - Notifications are pending.
     * 404 - No pending notifications.
     *
     * @param deviceId  Device ID.
     * @param serviceId Service ID. This parameter is optional and normally never used. A device would normally not
     *                  check for notifications of individual services, as that would not be data usage efficient.
     * @param response  Response format {@link ValuesDTO}.
     */
    @GET
    @Path('{' + PARAM_DEVICE_ID + "}/{" + PARAM_SERVICE_ID + '}')
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    void getPendingNotificationsForDeviceAndService(
            @Nonnull @PathParam(PARAM_DEVICE_ID) String deviceId,
            @Nonnull @PathParam(PARAM_SERVICE_ID) String serviceId,
            @Suspended @Nonnull AsyncResponse response);

    @GET
    @Path('{' + PARAM_DEVICE_ID + '}')
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    void getPendingNotificationsForDevice(
            @Nonnull @PathParam(PARAM_DEVICE_ID) String deviceId,
            @Suspended @Nonnull AsyncResponse response);

    /**
     * Create a pending notification for a device.
     *
     * This method would normally be an authorized, internal call only. Systems that need to notify devices, call this.
     *
     * Return codes:
     * 201 - Created pending notification (also if it already existed).
     *
     * @param deviceId  Device ID.
     * @param serviceId Service ID. If not used, a serviceId-less notification is created, which must be deleted with the
     *                  corresponding serviceId-less DELETE method.
     * @param response  Empty response.
     */
    @POST
    @Path('{' + PARAM_DEVICE_ID + "}/{" + PARAM_SERVICE_ID + '}')
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    void createPendingNotificationForDeviceAndService(
            @Nonnull @PathParam(PARAM_DEVICE_ID) String deviceId,
            @Nullable @PathParam(PARAM_SERVICE_ID) String serviceId,
            @Suspended @Nonnull AsyncResponse response);

    @POST
    @Path('{' + PARAM_DEVICE_ID + '}')
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    void createPendingNotificationForDevice(
            @Nonnull @PathParam(PARAM_DEVICE_ID) String deviceId,
            @Suspended @Nonnull AsyncResponse response);

    /**
     * Delete pending notifications for an ID.
     *
     * See 'create pending notification'. This method deletes the pending notification. Note that if there are no
     * more notifications left for any service, the entire entry is removed (even if originally an empty set
     * of service IDs was created, by adding a 'ID-less' service notification).
     *
     * Return codes:
     * 204 - Pending notification removed (if it existed).
     *
     * @param deviceId  Device ID.
     * @param serviceId Service ID. If this is omitted, all notifications for the device are deleted.
     * @param response  Empty response.
     */
    @DELETE
    @Path('{' + PARAM_DEVICE_ID + "}/{" + PARAM_SERVICE_ID + '}')
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    void deletePendingNotificationsForDeviceAndService(
            @Nonnull @PathParam(PARAM_DEVICE_ID) String deviceId,
            @Nonnull @PathParam(PARAM_SERVICE_ID) String serviceId,
            @Suspended @Nonnull AsyncResponse response);

    @DELETE
    @Path('{' + PARAM_DEVICE_ID + '}')
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    void deletePendingNotificationsForDevice(
            @Nonnull @PathParam(PARAM_DEVICE_ID) String deviceId,
            @Suspended @Nonnull AsyncResponse response);
}
