/*
 * $Id: DateTimePickerDirective.java 493867 2007-01-07 22:26:23Z mrdon $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.dojo.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.dojo.components.DateTimePicker;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see DateTimePicker
 */
public class DateTimePickerDirective extends DojoAbstractDirective {

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DateTimePicker(stack, req, res);
    }

    public String getBeanName() {
        return "datetimepicker";
    }
}
