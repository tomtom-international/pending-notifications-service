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

package com.tomtom.services.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tomtom.services.notification.ApiConstants;
import com.tomtom.speedtools.apivalidation.ApiDTO;
import com.tomtom.speedtools.utils.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class defines the data transfer object for the "/pending/version" method.
 * It contains only the version string, which is the POM version of the service.
 */
@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
@JsonInclude(Include.NON_EMPTY)
public final class VersionDTO extends ApiDTO {

    /**
     * Version string of service. No assumptions can be made on its format.
     */
    @Nullable
    private String version;

    @Override
    public void validate() {
        validator().start();
        validator().checkString(true, "version", version,
                ApiConstants.API_VERSION_MIN_LENGTH,
                ApiConstants.API_VERSION_MAX_LENGTH);
        validator().done();
    }

    public VersionDTO(@Nonnull final String version) {
        super();
        setVersion(version);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    private VersionDTO() {
        // Default constructor required by JAX-B.
        super();
    }

    @Nonnull
    public String getVersion() {
        assert version != null;
        return version;
    }

    public void setVersion(@Nonnull final String version) {
        this.version = StringUtils.trim(version);
    }

    @Override
    @Nonnull
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }
}
