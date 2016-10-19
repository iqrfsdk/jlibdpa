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
package com.microrisc.dpa22x.byteaccess.network.serial;

import com.microrisc.dpa22x.byteaccess.network.NetworkLayer;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerFactory;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerFactoryException;
import org.apache.commons.configuration.Configuration;

/**
 * Serial network layer factory.
 * <p>
 * Configuration items: <br>
 * - <b>networkLayer.type.serial.portName</b>: serial port name. 
 *      If no such configuration key is found, exception is thrown.
 * - <b>networkLayer.type.serial.baudRate</b>: baud rate
 *      If no such configuration key is found, exception is thrown.
 * 
 * @author Michal Konopa
 */
public final class SerialNetworkLayerFactory implements NetworkLayerFactory {
    
    @Override
    public NetworkLayer getNetworkLayer(Configuration configuration) throws NetworkLayerFactoryException {
        // port
        String portName = configuration.getString("networkLayer.type.serial.portName", "");
        if ( portName.isEmpty() ) {
            throw new NetworkLayerFactoryException("Port name is missing.");
        }
        
        // baud rate
        String baudRateStr = configuration.getString("networkLayer.type.serial.baudRate", "");
        if ( baudRateStr.isEmpty() ) {
            throw new NetworkLayerFactoryException("Baud rate is missing.");
        }
        
        try {
            int baudRate = Integer.valueOf(baudRateStr);
            return new SerialNetworkLayer(portName, baudRate);
        } catch ( Exception ex ) {
            throw new NetworkLayerFactoryException(ex);
        }   
    }
}
