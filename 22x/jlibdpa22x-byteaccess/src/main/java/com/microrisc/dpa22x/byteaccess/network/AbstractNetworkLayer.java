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
package com.microrisc.dpa22x.byteaccess.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for network layer implementations.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractNetworkLayer implements NetworkLayer {
    
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(AbstractNetworkLayer.class);

    /** Registered listener. */
    protected NetworkLayerListener listener = null;
    
    
    /**
     * Register specified listener. 
     * If there already is some listener registered, then the old one is unregistered
     * and {@code listener} is registered.
     *
     * @param listener to register
     */
    @Override
    public void registerListener(NetworkLayerListener listener) {
        if ( this.listener != null ) {
            unregisterListener();
            logger.info("Previously registered listener unregistered.");
        }
        
        this.listener = listener;
        logger.info("Listener registered: {}", listener);
    }

    /**
     * Unregister already registered listener. 
     * If there isn't registered listener, this operation has no effect.
     */
    @Override
    public void unregisterListener() {
        if ( listener == null ) {
            return;
        }
        
        listener = null;
        logger.info("Listener unregistered.");
    }
    
}
