/*
 * $Id: StrutsMockHttpServletResponse.java 490622 2006-12-28 00:55:27Z mrdon $
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
package org.apache.struts2.dojo.views.jsp.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import com.mockobjects.servlet.MockHttpServletResponse;


/**
 * StrutsMockHttpServletResponse
 *
 */
public class StrutsMockHttpServletResponse extends MockHttpServletResponse {
    private Locale locale;
    private PrintWriter writer;
    private int status;
    private String redirectURL;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getContentType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PrintWriter getWriter() throws IOException {
        if (writer == null)
            return new PrintWriter(new ByteArrayOutputStream());
        else
            return writer;
    }

    public void setCharacterEncoding(String string) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public String encodeURL(String s) {
        return s;
    }

    public String encodeRedirectURL(String s) {
        return s;
    }

    public String encodeUrl(String s) {
        return s;
    }

    public void setStatus(int i) {
        this.status = i;
        super.setStatus(i);
    }

    public int getStatus() {
        return status;
    }


    public String getRedirectURL() {
        return redirectURL;
    }

    public void sendRedirect(String redirectURL) throws IOException {
        this.redirectURL = redirectURL;
        super.sendRedirect(redirectURL);
    }
}
