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

import com.microrisc.cdc.J_AsyncMsgListener;
import com.microrisc.cdc.J_CDCImpl;
import com.microrisc.cdc.J_CDCImplException;
import com.microrisc.cdc.J_DSResponse;
import com.microrisc.dpa22x.byteaccess.network.AbstractNetworkLayer;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements network layer using {@code J_CDCImpl} object.
 * <p>
 * This registers itself like an asynchronous listener of {@code J_CDCImpl}
 * object. All data comming from CDC interface is forwarder to user's registered
 * network listener. All data designated to underlaying network are forwarded to
 * J_CDCImpl's {@code J_CDCImpl} method.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public final class CdcNetworkLayer
extends AbstractNetworkLayer implements J_AsyncMsgListener {
    
    //** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(CdcNetworkLayer.class);

    private static String checkPortName(String portName) {
        if ( portName == null ) {
            throw new IllegalArgumentException("Port name cannot be null");
        }

        if ( portName.isEmpty() ) {
            throw new IllegalArgumentException("Port name cannot be empty string");
        }
        return portName;
    }


    /** Reference to CDC-object for communication with IQRF network. */
    private J_CDCImpl cdcImpl = null;

    
    /**
     * Creates CDC network layer object.
     *
     * @param portName COM-port name for communication
     * @throws com.microrisc.cdc.J_CDCImplException if some exception has
     * occurred during creating of CDC network layer
     */
    public CdcNetworkLayer(String portName) throws J_CDCImplException, Exception {
        checkPortName(portName);
        cdcImpl = new J_CDCImpl(portName);
        logger.debug("CDC Network layer created: portName=" + portName);
    }

    /**
     * Starts receiving data from CDC interface. 
     * This methods last at least for 5 seconds due to reseting of connected USB device.
     */
    @Override
    public void start() {
        logger.debug("start - start");

        cdcImpl.registerAsyncListener(this);

        /*
        final long RESET_DELAY = 5000;
        final long MARGIN_DELAY = 5000;
        
        try {
            // reseting GW 
            // 5 s after receiving of this command USB device is reset - program must deal with this !!!
            cdcImpl.resetUSBDevice();
            Thread.sleep(RESET_DELAY + MARGIN_DELAY);
        } catch (Exception ex) {
            logger.error("Cannot reset USB device: " + ex.getMessage());
            throw new NetworkLayerException(ex);
        }
         */
        logger.debug("startIQRFLayer - end");
    }

    @Override
    public void sendData(short[] data) throws NetworkLayerException {
        logger.debug("sendData - start: data={}", data);

        J_DSResponse dataSendResult = null;
        try {
            dataSendResult = cdcImpl.sendData(data);
        } catch ( Exception ex ) {
            throw new NetworkLayerException(ex);
        }
        
        if ( dataSendResult != J_DSResponse.OK ) {
            throw new NetworkLayerException("Response of DS-command not OK: " + dataSendResult);
        }
        
        logger.debug("sendData - end");
    }

    @Override
    public void terminateAndRelease() {
        logger.debug("terminateAndRelease - start: ");

        cdcImpl.unregisterAsyncListener();
        logger.info("CDC Listener unregistered");

        cdcImpl.destroy();
        cdcImpl = null;

        logger.info("CDC Network Layer terminated and released");
        logger.debug("terminateAndRelease - end");
    }

    @Override
    public void onGetMessage(short[] data) {
        logger.debug("onGetMessage - start: data={}", Arrays.toString(data));

        if ( listener != null ) {
            listener.onGetData(data);
        }

        logger.debug("onGetMessage - end");
    }

}
