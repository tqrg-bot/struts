/*
 * $Id$
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.interceptor.validation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.TestUtils;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;
import org.apache.struts2.views.jsp.StrutsMockHttpServletResponse;
import org.apache.struts2.views.jsp.StrutsMockServletContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;

public class JSONValidationInterceptorTest extends StrutsTestCase {
    private MockActionInvocation invocation;
    private StringWriter stringWriter;
    private TestAction action;
    private StrutsMockHttpServletResponse response;
    private JSONValidationInterceptor interceptor;
    private StrutsMockHttpServletRequest request;

    public void testValidationFails() throws Exception {

        action.addActionError("General error");
        interceptor.setValidationFailedStatus(HttpServletResponse.SC_BAD_REQUEST);
        interceptor.intercept(invocation);

        String json = stringWriter.toString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils
            .normalize(JSONValidationInterceptorTest.class.getResource("json-1.txt"));
        //json
        assertEquals(normalizedExpected, normalizedActual);
        //execution
        assertFalse(invocation.isExecuted());
        //http status
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    public void testValidationSucceeds() throws Exception {
        JSONValidationInterceptor interceptor = new JSONValidationInterceptor();

        action.setText("abcd@ggg.com");
        action.setValue(10);

        interceptor.intercept(invocation);

        String json = stringWriter.toString();

        String normalizedActual = TestUtils.normalize(json, true);
        assertEquals("", normalizedActual);
        assertTrue(invocation.isExecuted());
    }
    
    public void testValidationSucceedsValidateOnly() throws Exception {
        JSONValidationInterceptor interceptor = new JSONValidationInterceptor();

        action.setText("abcd@ggg.com");
        action.setValue(10);

        //just validate
        Map parameters = new HashMap();
        parameters.put("struts.validateOnly", "true");
        request.setParameterMap(parameters);
        
        interceptor.intercept(invocation);

        String json = stringWriter.toString();

        String normalizedActual = TestUtils.normalize(json, true);
        assertEquals("/*{}*/", normalizedActual);
        assertFalse(invocation.isExecuted());
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.action = new TestAction();
        this.interceptor = new JSONValidationInterceptor();
        
        this.request = new StrutsMockHttpServletRequest();
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        this.response = new StrutsMockHttpServletResponse();
        response.setWriter(writer);

        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        ActionContext context = new ActionContext(stack.getContext());

        ActionContext.setContext(context);
        context.put(StrutsStatics.HTTP_REQUEST, request);
        context.put(StrutsStatics.HTTP_RESPONSE, response);

        StrutsMockServletContext servletContext = new StrutsMockServletContext();

        context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
        invocation = new MockActionInvocation() {
            private boolean executed;

            public String invoke() throws Exception {
                executed = true;
                return "success";
            }

            public boolean isExecuted() {
                return executed;
            }
        };
        invocation.setAction(action);
        invocation.setInvocationContext(context);
        MockActionProxy proxy = new MockActionProxy();
        proxy.setAction(action);
        invocation.setProxy(proxy);
    }

    @Validation
    public static class TestAction extends ActionSupport {
        private String text = "x";
        private int value = -10;

        public String execute() {
            return Action.SUCCESS;
        }

        @SkipValidation
        public String skipMe() {
            return "skipme";
        }

        public String getText() {
            return text;
        }

        @StringLengthFieldValidator(minLength = "2", message = "Too short")
        @EmailValidator(message = "This is no email")
        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        @IntRangeFieldValidator(min = "-1", message = "Min value is -1")
        public void setValue(int value) {
            this.value = value;
        }
    }
}
