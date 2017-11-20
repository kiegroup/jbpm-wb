/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workbench.es.client.editors.util;

import java.util.Date;

import org.jbpm.workbench.es.model.ErrorSummary;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.util.RequestStatus;

public class JobUtils {

    private static final Long JOB_ID = 1L;
    private static final Long PROCESS_INSTANCE_ID = 2L;

    public static RequestSummary createRequestSummary() {
        return createRequestSummary(JOB_ID,
                                    "businessKey",
                                    RequestStatus.QUEUED);
    }

    public static RequestSummary createRequestSummary(RequestStatus status) {
        return createRequestSummary(JOB_ID,
                                    "businessKey",
                                    status);
    }

    public static RequestSummary createRequestSummary(Long jobId,
                                                      String businessKey,
                                                      RequestStatus status) {
        return new RequestSummary(jobId,
                                  new Date(),
                                  status,
                                  "commandName",
                                  "Message",
                                  businessKey,
                                  1,
                                  0,
                                  "testProcessName",
                                  PROCESS_INSTANCE_ID,
                                  "testProcessInstanceDescription",
                                  "evaluation.1.0.1");
    }

    public static ErrorSummary createErrorSummary() {
        return new ErrorSummary(3L,
                                new Date(),
                                "errorMessage",
                                "errorStacktrace",
                                JOB_ID);
    }

    public static RequestParameterSummary createRequestParameterSummary() {
        return new RequestParameterSummary("processInstanceId",
                                           Long.toString(PROCESS_INSTANCE_ID));
    }
}
