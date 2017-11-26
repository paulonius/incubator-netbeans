/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.netbeans.modules.maven.format.checkstyle;

import javax.swing.JComponent;
import org.netbeans.modules.maven.api.customizer.ModelHandle2;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
@ProjectCustomizer.CompositeCategoryProvider.Registration(projectType="org-netbeans-modules-maven", category="Formatting", position=100)
public class CheckstyleCustomizerPanel implements ProjectCustomizer.CompositeCategoryProvider {

    @Override
    public ProjectCustomizer.Category createCategory(Lookup look) {
        return ProjectCustomizer.Category.create(
                "checkstyle",
                NbBundle.getMessage(CheckstyleCustomizerPanel.class, "TIT_CheckStyle"),
                null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category cat, Lookup look) {
        ModelHandle2 handle = look.lookup(ModelHandle2.class);
        return new CheckstylePanel(handle, cat);
    }


}
