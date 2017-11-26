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

package org.netbeans.installer.downloader.dispatcher.impl;

import org.netbeans.installer.downloader.dispatcher.Process;
import org.netbeans.installer.utils.ErrorManager;
import org.netbeans.installer.utils.helper.NbiThread;

/**
 * @author Danila_Dugurov
 */
public class Worker extends NbiThread {
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    Process current;
    
    public Worker() {
        setDaemon(true);
    }
    
    // if worker busy return false
    public synchronized boolean setCurrent(Process newCurrent) {
        if (!isFree()) {
            return false;
        }
        this.current = newCurrent;
        notifyAll();
        return true;
    }
    
    public synchronized boolean isFree() {
        return current == null;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.interrupted();
                synchronized (this) {
                    if (current == null) {
                        wait();
                        if (isFree()) {
                            continue;
                        }
                    }
                }
                current.init();
                current.run();
            } catch (InterruptedException e) {
                ErrorManager.notifyDebug("Interrupted", e); // NOI18N
            } finally {
                synchronized (this) {
                    current = null;
                }
            }
        }
    }
}
