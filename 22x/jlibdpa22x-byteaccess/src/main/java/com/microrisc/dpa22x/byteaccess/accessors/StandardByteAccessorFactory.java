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

import com.microrisc.dpa22x.DCTR;
import com.microrisc.dpa22x.RF_Mode;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayer;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerFactory;
import com.microrisc.dpa22x.timing.WaitingTimeCounter;
import java.lang.reflect.Constructor;
import org.apache.commons.configuration.Configuration;

/**
 * Standard Byte Accessor factory.
 * 
 * @author Michal Konopa
 */
public final class StandardByteAccessorFactory implements ByteAccessorFactory {
    
    // creates and returns network layer
    private NetworkLayer createNetworkLayer(Configuration configuration) throws ByteAccessorFactoryException {
        
        // getting factory class
        String networkLayerFactoryClassName = configuration.getString("networkLayer.factory.class", "");
        if ( networkLayerFactoryClassName.isEmpty() ) {
            throw new ByteAccessorFactoryException("Network layer factory class not found.");
        }
        
        try {
            Class networkLayerFactoryClass = Class.forName(networkLayerFactoryClassName);
            Constructor constructor = networkLayerFactoryClass.getConstructor();
            Object networkLayerFactory = constructor.newInstance();
            if ( !(networkLayerFactory instanceof NetworkLayerFactory) ) {
                throw new ByteAccessorFactoryException(
                        "Network layer factory MUST implement the: " + NetworkLayerFactory.class
                        + "interface."
                );
            }
            
            // returning network layer
            return ((NetworkLayerFactory)networkLayerFactory).getNetworkLayer(configuration);
        } catch ( Exception ex ) {
            throw new ByteAccessorFactoryException(ex);
        }
    }
    
    /**
     * Waiting time counter factory.
     * <p>
     * Configuration items: <br>
     * - <b>waitingTimeCounter.rfMode</b>: RF mode. If no such configuration
     * key is found, {@link WaitingTimeCounter#RF_MODE_DEEFAULT default} mode is used.<br>
     * 
     * - <b>waitingTimeCounter.dctr</b>: DCTR HW type. If no such configuration
     * key is found, {@link WaitingTimeCounter#DCTR_DEFAULT default} DCTR is used.<br>
     * 
     * - <b>waitingTimeCounter.timeToWaitForConfirmation</b>: time to wait for confirmation. 
     * If no such configuration key is found, 
     * {@link WaitingTimeCounter#TIME_TO_WAIT_FOR_CONFIRMATION_DEFAULT default} 
     * waiting timeout is used.
     */
    public static class WaitingTimeCounterFactory {
        
        private static RF_Mode parseRF_Mode(String rfModeStr) throws ByteAccessorFactoryException {
            switch ( rfModeStr ) {
                case "STD":
                    return RF_Mode.STD;
                case "LP":
                    return RF_Mode.LP;
                default:
                    throw new ByteAccessorFactoryException("Unknown value of RF mode: " + rfModeStr);
            }
        }
        
        private static DCTR parseDctr(String dctrStr) throws ByteAccessorFactoryException {
            switch ( dctrStr ) {
                case "5xD":
                    return DCTR.DCTR_5xD;
                case "7xD":
                    return DCTR.DCTR_7xD;
                default:
                    throw new ByteAccessorFactoryException("Unknown value of DCTR HW: " + dctrStr);
            }
        }
        
        /** 
         * Creates and returns waiting time counter according to specified configuration.
         * @param configuration configuration for waiting time counter
         * @return waiting time counter
         * @throws com.microrisc.dpa22x.byteaccess.accessors.ByteAccessorFactoryException
         *         if some error occured during creation of waiting time counter    
         */
        public static WaitingTimeCounter getWaitingTimeCounter(Configuration configuration) 
                throws ByteAccessorFactoryException 
        {
            RF_Mode rfMode = WaitingTimeCounter.RF_MODE_DEEFAULT;
            String rfModeStr = configuration.getString("waitingTimeCounter.rfMode", "");
            if ( !rfModeStr.isEmpty() ) {
                rfMode = parseRF_Mode(rfModeStr);
            }
            
            DCTR dctr = WaitingTimeCounter.DCTR_DEFAULT;
            String dctrStr = configuration.getString("waitingTimeCounter.dctr", "");
            if ( !dctrStr.isEmpty() ) {
                dctr = parseDctr(dctrStr);
            }
            
            long timeToWaitForConfirmation 
                = configuration.getLong("waitingTimeCounter.timeToWaitForConfirmation", -1);
            if ( timeToWaitForConfirmation == -1 ) {
                timeToWaitForConfirmation = WaitingTimeCounter.TIME_TO_WAIT_FOR_CONFIRMATION_DEFAULT;
            }
            
            return new WaitingTimeCounter(rfMode, dctr, timeToWaitForConfirmation);
        }
    }
   

    @Override
    public ByteAccessor getByteAccessor(Configuration configuration) 
            throws ByteAccessorFactoryException 
    {
        // creation of Network Layer
        NetworkLayer networkLayer = createNetworkLayer(configuration);
        
        // creation of Waiting Counter
        WaitingTimeCounter waitingTimeCounter = WaitingTimeCounterFactory.getWaitingTimeCounter(configuration);
        
        try {
            return new StandardByteAccessor(networkLayer, waitingTimeCounter);
        } catch ( ByteAccessorException ex ) {
            throw new ByteAccessorFactoryException(ex); 
        }
    }
    
}
