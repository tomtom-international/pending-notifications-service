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

package com.tomtom.services.notifications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tomtom.speedtools.apivalidation.ApiDTO;
import com.tomtom.speedtools.objects.Immutables;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
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
@XmlRootElement(name = "values")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ValuesDTO extends ApiDTO {

    @JsonProperty("values")
    @JsonUnwrapped
    @Nonnull
    public List<String> values; // Mandatory field.

    @Override
    public void validate() {
        validator().start();
        validator().checkNotNull(true, "values", values);   // Mandatory.
        validator().done();
    }

    public ValuesDTO(@Nonnull final Collection<String> values) {
        super();
        this.values = Immutables.listOf(values);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    private ValuesDTO() {
        // Default constructor required by JAX-B.
        super();
    }

    @JsonValue
    @Nullable
    public List<String> getValues() {
        beforeGet();
        return values;
    }

    public void setValues(@Nonnull final Collection<String> values) {
        beforeSet();
        this.values = Immutables.listOf(values);
    }
}
