/*
 * $Id: TreeNodeTag.java 471756 2006-11-06 15:01:43Z husted $
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.dojo.components.TreeNode;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see TreeNode
 */
public class TreeNodeTag extends AbstractClosingTag {

    private static final long serialVersionUID = 7340746943017900803L;


    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TreeNode(stack,req,res);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // NOTE: not necessary, label property is inherited, will be populated
    // by super-class
    /*protected void populateParams() {
        if (label != null) {
            TreeNode treeNode = (TreeNode)component;
            treeNode.setLabel(label);
        }
        super.populateParams();
    }*/
}
