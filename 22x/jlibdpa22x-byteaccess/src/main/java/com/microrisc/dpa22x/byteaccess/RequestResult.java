/*
 * Copyright 2016 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microrisc.dpa22x.byteaccess;

import com.microrisc.dpa22x.DPA_Response;


/**
 * Complete result of processing of a request sent to connected IQRF network.
 * 
 * @author Michal Konopa
 */
public final class RequestResult {
    
    /** Status of processing of sent request. */
    public static enum Status {
        
        /** Successfully completed. */
        SUCCESSFULLY_COMPLETED,

        /** Some error occured. */
        ERROR
    }
    
    // status
    private final Status status;
    
    // DPA response
    private final DPA_Response response;
    
    // processing info
    private final ProcessingInfo procInfo;
    
    
    /**
     * Creates new object of request's result.
     * @param status status
     * @param response DPA response
     * @param procInfo information about request's processing
     */
    public RequestResult(Status status, DPA_Response response, ProcessingInfo procInfo) {
        this.status = status;
        this.response = response;
        this.procInfo = procInfo;
    }
    
    /**
     * Returns status.
     * @return status
     */
    public Status getStatus() {
        return status;
    }
    
    /**
     * Returns response.
     * @return response
     */
    public DPA_Response getResponse() {
        return response;
    }
    
    /**
     * Returns detailed information about processing of sent request.
     * @return detailed information about request's processing
     */
    public ProcessingInfo getProcessingInfo() {
        return procInfo;
    }
}
