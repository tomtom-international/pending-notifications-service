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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Notification {

    /**
     * The deviceId indicates the device for which there are 1 or more
     * pending notifications.
     */
    @Nonnull
    private final String deviceId;

    /**
     * The set serviceIds contains all the IDs of other services, with IDs, which
     * have pending notifications.
     */
    @Nonnull
    private final Set<String> serviceIds;

    public Notification(
            @Nonnull final String deviceId,
            @Nonnull final Collection<String> serviceIds) {
        this.deviceId = deviceId;
        this.serviceIds = new HashSet<>();
        for (final String serviceId : serviceIds) {
            this.serviceIds.add(serviceId);
        }
    }

    @Nonnull
    public String getDeviceId() {
        return deviceId;
    }

    @Nonnull
    public Set<String> getServiceIds() {
        return serviceIds;
    }
}
