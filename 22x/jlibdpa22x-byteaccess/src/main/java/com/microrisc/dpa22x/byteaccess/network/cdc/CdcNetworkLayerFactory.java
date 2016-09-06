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
package com.microrisc.dpa22x.byteaccess.network.cdc;

import com.microrisc.dpa22x.byteaccess.network.NetworkLayer;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerFactory;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerFactoryException;
import org.apache.commons.configuration.Configuration;

/**
 * CDC network layer factory.
 * <p>
 * Configuration items: <br>
 * - <b>networkLayer.type.cdc.port</b>: COM-port to use. If no such configuration
 * key is found, exception is thrown.
 * 
 * @author Michal Konopa
 */
public final class CdcNetworkLayerFactory implements NetworkLayerFactory {

    @Override
    public NetworkLayer getNetworkLayer(Configuration configuration) throws NetworkLayerFactoryException {
        
        // port
        String portName = configuration.getString("networkLayer.type.cdc.portName", "");
        if ( portName.isEmpty() ) {
            throw new NetworkLayerFactoryException("Port name is missing.");
        }
        
        try {
            return new CdcNetworkLayer(portName);
        } catch ( Exception ex ) {
            throw new NetworkLayerFactoryException(ex);
        }
    }
    
}
