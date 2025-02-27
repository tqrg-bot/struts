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
package org.apache.struts2.dispatcher.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;
import org.apache.struts2.views.jsp.StrutsMockHttpServletResponse;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

import javax.servlet.http.HttpServletResponse;

/**
 * DefaultActionMapper test case.
 *
 */
public class DefaultActionMapperTest extends StrutsTestCase {

    private MockHttpServletRequest req;
    private ConfigurationManager configManager;
    private Configuration config;

    protected void setUp() throws Exception {
        super.setUp();
        req = new MockHttpServletRequest();
        req.setupGetParameterMap(new HashMap());
        req.setupGetContextPath("/my/namespace");

        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig("myns", "/my/namespace", false, null);
        PackageConfig pkg2 = new PackageConfig("my", "/my", false, null);
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        configManager = new ConfigurationManager() {
            public Configuration getConfiguration() {
                return config;
            }
        };
    }

    public void testGetMapping() throws Exception {
        setUp();
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithMethod() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName!add.action");
        req.setupGetServletPath("/my/namespace/actionName!add.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertEquals("add", mapping.getMethod());
    }

    public void testGetMappingWithSlashedName() throws Exception {

        req.setupGetRequestURI("/my/foo/actionName.action");
        req.setupGetServletPath("/my/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my", mapping.getNamespace());
        assertEquals("foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());      
    }
    
    public void testGetMappingWithNamespaceSlash() throws Exception {

        req.setupGetRequestURI("/my.hh/abc.action");
        req.setupGetServletPath("/my.hh/abc.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("abc", mapping.getName());
        
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("my.hh/abc", mapping.getName());
    }

    public void testGetMappingWithUnknownNamespace() throws Exception {
        setUp();
        req.setupGetRequestURI("/bo/foo/actionName.action");
        req.setupGetServletPath("/bo/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }
    
    public void testGetMappingWithUnknownNamespaceButFullNamespaceSelect() throws Exception {
        setUp();
        req.setupGetRequestURI("/bo/foo/actionName.action");
        req.setupGetServletPath("/bo/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAlwaysSelectFullNamespace("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/bo/foo", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetUri() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithMethod() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName!add.action");
        req.setupGetServletPath("/my/namespace/actionName!add.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace/actionName!add.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetMappingWithNoExtension() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName");
        req.setupGetServletPath("/my/namespace/actionName");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }
    
    public void testGetMappingWithNoExtensionButUriHasExtension() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.html");
        req.setupGetServletPath("/my/namespace/actionName.html");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName.html", mapping.getName());
        assertNull(mapping.getMethod());
    }

    // =============================
    // === test name & namespace ===
    // =============================

    public void testParseNameAndNamespace1() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }

    public void testParseNameAndNamespace2() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/");
    }

    public void testParseNameAndNamespace3() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/my/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/my");
    }

    public void testParseNameAndNamespace_NoSlashes() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setSlashesInActionNames("false");
        defaultActionMapper.parseNameAndNamespace("/foo/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }

    public void testParseNameAndNamespace_AllowSlashes() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setSlashesInActionNames("true");
        defaultActionMapper.parseNameAndNamespace("/foo/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "foo/someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }


    // ===========================
    // === test special prefix ===
    // ===========================

    public void testActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals(actionMapping.getName(), "myAction");
    }

    public void testActionPrefix_fromImageButton() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.x", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.y", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals(actionMapping.getName(), "myAction");
    }
    
    public void testActionPrefix_fromIEImageButton() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.x", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.y", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals(actionMapping.getName(), "myAction");
    }

    public void testRedirectPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.REDIRECT_PREFIX + "http://www.google.com", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        Result result = actionMapping.getResult();
        assertNotNull(result);
        assertTrue(result instanceof ServletRedirectResult);

        Mock invMock = new Mock(ActionInvocation.class);
        ActionInvocation inv = (ActionInvocation) invMock.proxy();
        ActionContext ctx = ActionContext.getContext();
        ctx.put(ServletActionContext.HTTP_REQUEST, request);
        StrutsMockHttpServletResponse response = new StrutsMockHttpServletResponse();
        ctx.put(ServletActionContext.HTTP_RESPONSE, response);
        invMock.expectAndReturn("getInvocationContext", ctx);
        invMock.expectAndReturn("getStack", ctx.getValueStack());
        result.execute(inv);
        assertEquals("http://www.google.com", response.getRedirectURL());
        //TODO: need to test location but there's noaccess to the property/method, unless we use reflection
    }

    public void testRedirectActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.REDIRECT_ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);
        

        Result result = actionMapping.getResult();
        assertNotNull(result);
        assertTrue(result instanceof ServletRedirectResult);

        // TODO: need to test location but there's noaccess to the property/method, unless we use reflection
    }

    public void testCustomActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put("foo:myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.addParameterAction("foo", new ParameterAction() {
            public void execute(String key, ActionMapping mapping) {
                mapping.setName("myAction");
            }
        });
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals(actionMapping.getName(), "myAction");
    }

    public void testDropExtension() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        String name = mapper.dropExtension("foo.action");
        assertTrue("Name not right: "+name, "foo".equals(name));

        name = mapper.dropExtension("foo.action.action");
        assertTrue("Name not right: "+name, "foo.action".equals(name));

    }

    public void testGetUriFromActionMapper1() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper2() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper3() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action", uri);
    }


    public void testGetUriFromActionMapper4() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper5() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    //
    public void testGetUriFromActionMapper6() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper7() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper8() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }


    public void testGetUriFromActionMapper9() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper10() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper11() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName.action");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper12() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName.action");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

}
