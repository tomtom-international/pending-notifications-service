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

package com.tomtom.services.notifications.dao.mappers;

import com.tomtom.services.notifications.Notification;
import com.tomtom.speedtools.mongodb.mappers.EntityMapper;
import com.tomtom.speedtools.mongodb.mappers.StringMapper;

import java.util.Collection;

/**
 * This class defines the mapping of database objects to domain objects. It is backed by the
 * SpeedTools database mapping framework, which includes the EntityMapper.
 */
public class NotificationMapper extends EntityMapper<Notification> {

    // Specify the entity type.
    public final EntityType entityType = entityType(Notification.class);

    // Specify the mappers for the class fields.
    public final Field<String> deviceId = stringField("deviceId", "getDeviceId", CONSTRUCTOR);
    public final Field<Collection<String>> serviceIds = collectionField("serviceIds", StringMapper.class, "getServiceIds", CONSTRUCTOR);
}
