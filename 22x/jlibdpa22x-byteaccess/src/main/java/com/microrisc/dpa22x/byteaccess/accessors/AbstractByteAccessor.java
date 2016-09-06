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

import com.microrisc.dpa22x.byteaccess.network.NetworkLayer;
import com.microrisc.dpa22x.timing.WaitingTimeCounter;

/**
 * Abstract base class for byte accessors.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractByteAccessor implements ByteAccessor {
    
    /** Network layer. */
    protected NetworkLayer networkLayer = null;
    
    /** Default waiting timeout. */
    protected long defaultWaitingTimeout = ByteAccessor.WAITING_TIMEOUT_NOT_LIMITED;
    
    /** Waiting time counter to use for calculating waiting times of messages. */
    protected WaitingTimeCounter waitingTimeCounter = null;
    
    
    private NetworkLayer checkNetworkLayer(NetworkLayer networkLayer) {
        if ( networkLayer == null ) {
            throw new IllegalArgumentException("Network layer cannot be null.");
        }
        return networkLayer;
    }
    
    private WaitingTimeCounter checkWaitingTimeCounter(WaitingTimeCounter waitingTimeCounter) {
        if ( waitingTimeCounter == null ) {
            throw new IllegalArgumentException("Waiting time counter cannot be null.");
        }
        return waitingTimeCounter;
    }
    
    protected long checkWaitingTimeout(long waitingTimeout) {
        if ( waitingTimeout == ByteAccessor.WAITING_TIMEOUT_NOT_LIMITED ) {
            return waitingTimeout;
        }
        
        if ( waitingTimeout < 0 ) {
            throw new IllegalArgumentException(
                    "Waiting timeout cannot be less then 0 and be different from "
                    + ByteAccessor.WAITING_TIMEOUT_NOT_LIMITED 
                    + " at the same time."
            );
        }
        
        return waitingTimeout;
    }
    
    /**
     * Protected constructor.
     * 
     * @param networkLayer network layer to use
     * @param waitingTimeCounter waiting time counter to use
     * @throws IllegalArgumentException if {@code waitingTimeCounter} is {@code null}
     */
    protected AbstractByteAccessor(
            NetworkLayer networkLayer, WaitingTimeCounter waitingTimeCounter
    ) {
        this.networkLayer = checkNetworkLayer(networkLayer);
        this.waitingTimeCounter = checkWaitingTimeCounter(waitingTimeCounter);
    }
    
    /**
     * Sets default waiting timeout.
     * 
     * @param timeout timeout value to set
     */
    @Override
    public void setDefaultWaitingTimeout(long timeout) {
        this.defaultWaitingTimeout = checkWaitingTimeout(timeout);
    }
}
