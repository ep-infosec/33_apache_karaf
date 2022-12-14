/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.diagnostic.core.providers;

import java.io.OutputStreamWriter;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.apache.karaf.diagnostic.core.common.TextDumpProvider;

/**
 * Provider which dumps thread info to file named threads.txt.
 */
public class ThreadDumpProvider extends TextDumpProvider {

    /**
     * Creates new dump entry which contains information about threads.
     */
    public ThreadDumpProvider() {
        super("threads.txt");
    }

    @Override
    protected void writeDump(OutputStreamWriter outputStream) throws Exception {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        outputStream.write("Number of threads: " + threadMXBean.getThreadCount() + "\n");

        for (ThreadInfo threadInfo : threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE)) {
            outputStream.write(getDumpThreadString(threadInfo) + "\n\n");
        }

    }

    protected String getDumpThreadString(ThreadInfo threadInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(threadInfo.getThreadName()).append("\"").append(" Id=").append(
            threadInfo.getThreadId()).append(" ").append(threadInfo.getThreadState());
        if (threadInfo.getLockName() != null) {
            sb.append(" on ").append(threadInfo.getLockName());
        }
        if (threadInfo.getLockOwnerName() != null) {
            sb.append(" owned by \"").append(threadInfo.getLockOwnerName()).append("\" Id=").append(
                threadInfo.getLockOwnerId());
        }
        if (threadInfo.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (threadInfo.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');
        int i = 0;
        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        for (; i < stackTrace.length; i++) {
            StackTraceElement ste = stackTrace[i];
            sb.append("\tat ").append(ste.toString());
            sb.append('\n');
            if (i == 0 && threadInfo.getLockInfo() != null) {
                Thread.State ts = threadInfo.getThreadState();
                switch (ts) {
                case BLOCKED:
                    sb.append("\t-  blocked on ").append(threadInfo.getLockInfo());
                    sb.append('\n');
                    break;
                case WAITING:
                    sb.append("\t-  waiting on ").append(threadInfo.getLockInfo());
                    sb.append('\n');
                    break;
                case TIMED_WAITING:
                    sb.append("\t-  waiting on ").append(threadInfo.getLockInfo());
                    sb.append('\n');
                    break;
                default:
                }
            }

            for (MonitorInfo mi : threadInfo.getLockedMonitors()) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked ").append(mi);
                    sb.append('\n');
                }
            }
        }
        if (i < stackTrace.length) {
            sb.append("\t...");
            sb.append('\n');
        }

        LockInfo[] locks = threadInfo.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = ").append(locks.length);
            sb.append('\n');
            for (LockInfo li : locks) {
                sb.append("\t- ").append(li);
                sb.append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }


}
