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

/**
 * This utility class contains constants used in the Web services API.
 */
public final class ApiConstants {

    // Prevent instantiation.
    private ApiConstants() {
        super();
        assert false;
    }

    /**
     * General HTTP timeout for @Suspend() annotations.
     * The service will timeout after 30 seconds.
     */
    public static final int SUSPEND_TIMEOUT = 30000;

    /**
     * Define the min and max length of the version string.
     */
    public static final int API_VERSION_MAX_LENGTH = 25;
    public static final int API_VERSION_MIN_LENGTH = 0;
}
