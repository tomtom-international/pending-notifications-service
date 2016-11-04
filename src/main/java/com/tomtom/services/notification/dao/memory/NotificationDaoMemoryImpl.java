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

package com.tomtom.services.notification.dao.memory;

import com.tomtom.services.notification.dao.NotificationDao;
import com.tomtom.speedtools.mongodb.EntityNotFoundException;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationDaoMemoryImpl implements NotificationDao {

    /**
     * This simple implementation uses a local (thread-safe) set to store the pending notifications.
     * The list contains the device IDs for which a notification is pending only, together with a
     * set of service IDs, for which the notification is valid.
     *
     * To allow fail-over, you would back this set up by persisting to a data store, or replicating
     * it to a slave node. We are leaving that as a pretty trivial exercise to the reader.
     *
     * Persisting to a data store can probably be done asynchronously. Also, the store would ONLY ever
     * be read at start-up; never affecting performance.
     */
    @Nonnull
    private final Map<String, Set<String>> notifications = new ConcurrentHashMap<>();

    @Override
    public final int getNrOfDeviceIds() {
        return notifications.size();
    }

    @Override
    @Nonnull
    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public List<String> getAllDeviceIds() {
        final String[] deviceIds = notifications.keySet().toArray(new String[0]);
        Arrays.sort(deviceIds);
        return Arrays.asList(deviceIds);
    }

    @Override
    @Nonnull
    public Set<String> getServiceIds(@Nonnull final String deviceId) throws EntityNotFoundException {
        final Set<String> serviceIds = notifications.get(deviceId);
        if (serviceIds == null) {
            throw new EntityNotFoundException("No entry found for: " + deviceId);
        }
        return serviceIds;
    }

    @Override
    public void removeServiceIds(@Nonnull final String deviceId) {
        notifications.remove(deviceId);
    }

    @Override
    public void putServiceIds(@Nonnull final String deviceId, @Nonnull final Set<String> serviceIds) {
        notifications.put(deviceId, serviceIds);
    }
}
