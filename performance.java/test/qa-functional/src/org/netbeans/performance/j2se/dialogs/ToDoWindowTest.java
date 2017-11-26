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
package org.netbeans.performance.j2se.dialogs;

import junit.framework.Test;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.modules.performance.guitracker.LoggingRepaintManager;
import org.netbeans.modules.performance.utilities.PerformanceTestCase;
import org.netbeans.performance.j2se.setup.J2SESetup;


/**
 * Test of ToDo Window
 *
 * @author anebuzelsky@netbeans.org, mmirilovic@netbeans.org
 */
public class ToDoWindowTest extends PerformanceTestCase {

    private String MENU, TITLE;

    /**
     * Creates a new instance of ToDoWindow
     *
     * @param testName test name
     */
    public ToDoWindowTest(String testName) {
        super(testName);
        expectedTime = WINDOW_OPEN;
    }

    /**
     * Creates a new instance of ToDoWindow
     *
     * @param testName test name
     * @param performanceDataName data name
     */
    public ToDoWindowTest(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        expectedTime = WINDOW_OPEN;
    }

    public static Test suite() {
        return emptyConfiguration()
                .addTest(J2SESetup.class, "testCloseMemoryToolbar")
                .addTest(ToDoWindowTest.class)
                .suite();
    }

    public void testToDoWindow() {
        doMeasurement();
    }

    @Override
    public void initialize() {
        MENU = Bundle.getStringTrimmed("org.netbeans.core.windows.resources.Bundle", "Menu/Window") + "|Action Items";
        TITLE = "Action Items";
        repaintManager().addRegionFilter(LoggingRepaintManager.IGNORE_STATUS_LINE_FILTER);
    }

    @Override
    public void prepare() {
    }

    @Override
    public ComponentOperator open() {
        new JMenuBarOperator(MainWindowOperator.getDefault().getJMenuBar()).pushMenu(MENU, "|");
        return new TopComponentOperator(TITLE);
    }

    @Override
    public void close() {
        if (testedComponentOperator != null && testedComponentOperator.isShowing()) {
            ((TopComponentOperator) testedComponentOperator).close();
        }
    }

    @Override
    public void shutdown() {
        repaintManager().resetRegionFilters();
    }
}
