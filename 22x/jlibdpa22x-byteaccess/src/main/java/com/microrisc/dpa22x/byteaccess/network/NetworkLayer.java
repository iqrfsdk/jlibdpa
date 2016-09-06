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

/**
 * Bottom network layer for communication with IQRF network. 
 * 
 * @author Michal Konopa
 */
public interface NetworkLayer {
    
    /**
     * Starts the network layer run.
     * @throws com.microrisc.dpa22x.byteaccess.network.NetworkLayerException if
     *         some error occured during start
     */
    void start() throws NetworkLayerException;
    
    /**
     * Registers specified network layer listener, which send the data from 
     * network layer to.
     * 
     * @param listener listener to register
     */
    void registerListener(NetworkLayerListener listener);
    
    /**
     * Sends specified data into network.
     * @param data data to send into network
     * @throws NetworkLayerException if an error has occured during sending of specified data
     */
    void sendData(short[] data) throws NetworkLayerException;
    
    /**
     * Unregisters currently registered network listener.
     */
    void unregisterListener();
    
    /**
     * Terminates the network layer run and release used resources.
     */
    void terminateAndRelease();
}
