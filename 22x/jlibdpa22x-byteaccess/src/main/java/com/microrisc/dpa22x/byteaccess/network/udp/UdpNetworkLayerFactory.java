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
package com.microrisc.dpa22x.byteaccess.network.udp;

import com.microrisc.dpa22x.byteaccess.network.NetworkLayer;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerFactory;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerFactoryException;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UDP network layer factory.
 * <p>
 * Configuration items: <br>
 * - <b>networkLayer.type.udp.localHostName</b>: host name. If no such configuration
 * key is found, loopback is used. <br>
 * 
 * - <b>networkLayer.type.udp.localPort</b>: local port. If no such configuration
 * key is found, exception is thrown. <br>
 * 
 * - <b>networkLayer.type.udp.remoteHostName</b>: remote host name. If no such configuration
 * key is found, exception is thrown. <br>
 * 
 * - <b>networkLayer.type.udp.remotePort</b>: remote port. If no such configuration
 * key is found, exception is thrown. <br>
 * 
 * - <b>networkLayer.type.udp.maxRecvPacketSize</b>: maximum received packet size. 
 * If no such configuration key is found, {@link UdpNetworkLayer#MAX_RECEIVED_PACKET_SIZE_DEFAULT}
 * is used. <br>
 * 
 * - <b>networkLayer.type.udp.receptionTimeout</b>: reception timeout. 
 * If no such configuration key is found, {@link UdpNetworkLayer#RECEPTION_TIMEOUT_DEFAULT}
 * is used.
 * 
 * @author Michal Konopa
 */
public final class UdpNetworkLayerFactory implements NetworkLayerFactory {
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(UdpNetworkLayer.class);
    
    
    @Override
    public NetworkLayer getNetworkLayer(Configuration configuration) throws NetworkLayerFactoryException {
        
        // local host name
        String localHostName = configuration.getString("networkLayer.type.udp.localHostName", "");
        if ( localHostName.isEmpty() ) {
            // loopback
            localHostName = null;
        }
        
        // local port
        int localPort = configuration.getInt("networkLayer.type.udp.localPort", -1);
        if ( localPort == -1 ) {
            throw new NetworkLayerFactoryException("Local port is missing.");
        }
        
        // local host name
        String remoteHostName = configuration.getString("networkLayer.type.udp.remoteHostName", "");
        if ( remoteHostName.isEmpty() ) {
            throw new NetworkLayerFactoryException("Remote host name is missing.");
        }
        
        // remote port
        int remotePort = configuration.getInt("networkLayer.type.udp.remotePort", -1);
        if ( remotePort == -1 ) {
            throw new NetworkLayerFactoryException("Remote port is missing.");
        }
        
        // max recived packet size
        int maxRecvPacketSize = configuration.getInt("networkLayer.type.udp.maxRecvPacketSize", -1);
        if ( maxRecvPacketSize == -1 ) {
            maxRecvPacketSize = UdpNetworkLayer.MAX_RECEIVED_PACKET_SIZE_DEFAULT;
            logger.info("Maximum received packet size is missing, {} used", 
                    UdpNetworkLayer.MAX_RECEIVED_PACKET_SIZE_DEFAULT
            );
        }
        
        // reception timeout
        int receptionTimeout = configuration.getInt("networkLayer.type.udp.receptionTimeout", -1);
        if ( receptionTimeout == -1 ) {
            receptionTimeout = UdpNetworkLayer.RECEPTION_TIMEOUT_DEFAULT;
            logger.info("Reception timeout is missing, {} used", 
                    UdpNetworkLayer.RECEPTION_TIMEOUT_DEFAULT
            );
        }
        
        try {
            return new UdpNetworkLayer(
                    localHostName, localPort, remoteHostName, remotePort, 
                    maxRecvPacketSize, receptionTimeout
            );
        } catch ( Exception ex ) {
            throw new NetworkLayerFactoryException(ex);
        }
    }
}
