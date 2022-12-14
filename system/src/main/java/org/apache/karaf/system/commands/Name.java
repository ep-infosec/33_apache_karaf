/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.system.commands;


import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.system.SystemService;

/**
 * Command to shut down Karaf container.
 */
@Command(scope = "system", name = "name", description = "Show or change Karaf instance name.")
@Service
public class Name implements Action {

    @Argument(name = "name", index = 0, description = "New name for the instance", required = false, multiValued = false)
    String name;

    @Reference
    SystemService systemService;

    @Override
    public Object execute() throws Exception {
        if (name == null) {
            System.out.println(systemService.getName());
        } else {
            systemService.setName(name);
            System.out.println("Instance name changed to " + name + ". Restart needed for this to take effect.");
        }
        return null;
    }

}
