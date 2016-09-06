/*
 * Copyright 2016 Microrisc s.r.o.
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
package com.microrisc.dpa22x.byteaccess.accessors;

import com.microrisc.dpa22x.byteaccess.RequestResult;

/**
 * Byte accessor interface.
 * 
 * @author Michal Konopa
 */
public interface ByteAccessor {
    
    /**
     * Indicates, that waiting timeout is not limited. 
     */
    static final long WAITING_TIMEOUT_NOT_LIMITED = -1;
    
    /**
     * Sends specified DPA request into connected IQRF network and returns result.
     * It will wait at maximum {@code waitingTimeout} ms for result to come in. 
     * If no result cames in until specified timeout elapses, the return value will
     * contain appropriate error description.
     * 
     * @param request request to send
     * @param waitingTimeout maximum time to wait for result
     * @return result of the request's processing
     */
    RequestResult sendRequest(short[] request, long waitingTimeout);
    
    /**
     * Same as {@link ByteAccessor#sendRequest(short[], long) sendRequest} method
     * with the difference, that the default waiting timeout will be used.
     * 
     * @param request request to send
     * @return result of the request's processing
     */
    RequestResult sendRequest(short[] request);
    
    /**
     * Sets default timeout to wait to a result. 
     * It will last until new value of waiting timeout will be set.
     * 
     * @param timeout new value of waiting timeout to set
     */
    void setDefaultWaitingTimeout(long timeout);
}
