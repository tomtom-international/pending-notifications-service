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
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tomtom.speedtools.apivalidation.ApiDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class defines the message body for a 'get all pending notifications' call.
 *
 * It is essentially a list of IDs and an integer that contains the size of the
 * list. In its current form, the 'total' field (the size of the list) is not
 * that useful. If you'd implement paging, the total count becomes important in
 * order to define proper 'offset' and 'count' parameters in a request (this has
 * not been implemented in this example).
 */
@SuppressWarnings({"EqualsWhichDoesntCheckParameterClass", "NullableProblems"})
@JsonInclude(Include.NON_EMPTY)
public final class AllPendingNotificationsDTO extends ApiDTO {

    @Nonnull
    public Integer total;    // Mandatory field.

    @JsonUnwrapped
    @Nullable
    public ValuesDTO ids;    // Optional field.

    /**
     * The method 'validate' is SpeedTools proprietary way of dealing with
     * message body validation. Every property in this class is explicitly
     * validated in this method.
     *
     * The SpeedTools API validation framework may seem a bit awkward at first
     * (and it is a bit, to be frank), but it does have some advantages over
     * annotations, such as that it accumulates all errors in a single response
     * body, which is easily parseable by the client, and it allows for (almost)
     * immutable data transfer objects. Any properties of a DTO using this framework
     * can ONLY be used after ALL properties have been set; and reversely, no
     * property can be set after one of them has ever been read.
     */
    @Override
    public void validate() {
        validator().start();
        validator().checkInteger(true, "total", total, 0, Integer.MAX_VALUE);   // Mandatory.
        validator().checkNotNullAndValidate(false, "values", ids);           // Optional.
        validator().done();
    }

    public AllPendingNotificationsDTO(
            final int total,
            @Nullable final List<String> ids) {
        this.total = total;
        this.ids = (ids == null) ? null : new ValuesDTO(ids);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    private AllPendingNotificationsDTO() {
        // Default constructor required by JAX-B.
        super();
    }

    @Nonnull
    public Integer getTotal() {
        return total;
    }

    @Nullable
    public ValuesDTO getIds() {
        return ids;
    }

    public void setTotal(@Nonnull final Integer total) {
        this.total = total;
    }

    public void setIds(@Nullable final List<String> ids) {
        this.ids = (ids == null) ? null : new ValuesDTO(ids);
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
