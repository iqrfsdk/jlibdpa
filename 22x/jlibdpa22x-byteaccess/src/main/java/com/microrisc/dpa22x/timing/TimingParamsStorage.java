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
package com.microrisc.dpa22x.timing;

import com.microrisc.dpa22x.DPA_Response;
import com.microrisc.dpa22x.ProtocolProperties;
import com.microrisc.dpa22x.RF_Mode;
import com.microrisc.dpa22x.peripherals.Coordinator;
import com.microrisc.dpa22x.peripherals.FRC;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage of timing parameters.
 * 
 * @author Michal Konopa
 */
public final class TimingParamsStorage {
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(TimingParamsStorage.class);
    
    
    // base abstract clas for mutable versions of "Timing Params" classes
    private static abstract class MutableTimingParams {
        
        // returns timing params (usually an immutable version of this)
        public abstract TimingParams getTimingParams();
    }
    
    // mutable version of FRC_TimingParams class
    private static final class MutableFRC_TimingParams extends MutableTimingParams {
        
        // number of bonded nodes
        private int bondedNodesNum;

        // RF mode
        private RF_Mode rfMode;

        // response time on coordinator
        private FRC_ResponseTime responseTime;
        
        
        // CACHING
        // indicates, whether last timing params has changed since the last return
        private boolean changedFromLastReturn;
        private FRC_TimingParams lastReturnedTimingParams;
        
        
        public MutableFRC_TimingParams() {
           this.bondedNodesNum = FRC_TimingParams.DEFAULT_BONDED_NODES_NUM;
           this.rfMode = FRC_TimingParams.DEFAULT_RF_MODE;
           this.responseTime = FRC_TimingParams.DEFAULT_RESPONSE_TIME;
           
           this.changedFromLastReturn = false;
           this.lastReturnedTimingParams = null;
        }
        
        public void setBondedNodesNum(int bondedNodesNum) {
            changedFromLastReturn = (this.bondedNodesNum != bondedNodesNum);
            this.bondedNodesNum = bondedNodesNum;
        }
        
        public void setRfMode(RF_Mode rfMode) {
            changedFromLastReturn = (this.rfMode != rfMode);
            this.rfMode = rfMode;
        }
        
        public void setResponseTime(FRC_ResponseTime responseTime) {
            changedFromLastReturn = (this.responseTime != responseTime);
            this.responseTime = responseTime;
        }
        
        @Override
        public TimingParams getTimingParams() {
            if ( lastReturnedTimingParams != null ) {
                if ( !this.changedFromLastReturn ) {
                    return lastReturnedTimingParams;
                }
            }
            
            lastReturnedTimingParams = new FRC_TimingParams(
                    bondedNodesNum, rfMode, responseTime
            );
            this.changedFromLastReturn = false;
            
            return lastReturnedTimingParams;
        }
        
    }
    
    
    
    /**
     * Encapsulates initial settings for the storage.
     */
    public static final class InitialSettings {
        
        // bonded nodes
        private final Collection<String> bondedNodes;
        
        // actual RF mode
        private final RF_Mode rfMode;
        
        
        public static final class Builder {
            private final Collection<String> bondedNodes;
            private RF_Mode rfMode;

            public Builder(Collection<String> bondedNodes) {
                this.bondedNodes = bondedNodes;
            }

            public Builder rfMode(RF_Mode rfMode) {
                this.rfMode = rfMode;
                return this;
            }

            public InitialSettings build() {
                return new InitialSettings(this);
            }
        }
        
        /**
         * Creates new object of initial settings.
         * @param builder builder to get values from
         */
        private InitialSettings(Builder builder) {
            this.bondedNodes = builder.bondedNodes;
            this.rfMode = builder.rfMode;
        }

        /**
         * @return the bonded nodes
         */
        public Collection<String> getBondedNodes() {
            return bondedNodes;
        }

        /**
         * @return the RF mode
         */
        public RF_Mode getRfMode() {
            return rfMode;
        }
    }
    
    
    // updates timing parameters according to request-response info
    private abstract class TimingParamsUpdater {
        public abstract void update(short[] request, DPA_Response response);
    }
    
    // updates timing parameters according to coordinator request-response info
    private class Coordinator_Updater extends TimingParamsUpdater {

        // determines Coordinator's command
        private Coordinator.Command getCommand(short commandId) {
            for ( Coordinator.Command command : Coordinator.Command.values() ) {
                if ( command.asByteValue()== commandId ) {
                    return command;
                }
            }
            return null;
        }
        
        // returns number of bonded nodes
        private int getBondedNodesFromGetBondedNodesResponseData(short[] responseData) {
            int bondedNodes = 0;
            for ( int byteId = 0; byteId < 32; byteId++ ) {
                short dataItem = responseData[byteId];
                short testByte = (short)1;
                
                while ( testByte <= 128 ) {
                    if ( (dataItem & testByte) == testByte ) {
                        bondedNodes++;
                    }
                    testByte *= 2;
                }
            }
            return bondedNodes;
        }
        
        private void processGetBondedNodes(
            short[] request, DPA_Response response, MutableFRC_TimingParams frcTimingParams
        ) {
            int bondedNodesNum = getBondedNodesFromGetBondedNodesResponseData(response.getData());
            frcTimingParams.setBondedNodesNum(bondedNodesNum);
        }
        
        private void processClearAllBonds(
            short[] request, DPA_Response response, MutableFRC_TimingParams frcTimingParams
        ) {
            frcTimingParams.setBondedNodesNum(0);
        }
        
        private void processBondNode(
            short[] request, DPA_Response response, MutableFRC_TimingParams frcTimingParams
        ) {
            int bondedNodesNum = response.getData()[1];
            frcTimingParams.setBondedNodesNum(bondedNodesNum);
        }
        
        private void processRemoveBondedNode(
            short[] request, DPA_Response response, MutableFRC_TimingParams frcTimingParams
        ) {
            int bondedNodesNum = response.getData()[0]; 
            frcTimingParams.setBondedNodesNum(bondedNodesNum);
        }
        
        private void processRebondNode(
            short[] request, DPA_Response response, MutableFRC_TimingParams frcTimingParams
        ) {
            int bondedNodesNum = response.getData()[0];
            frcTimingParams.setBondedNodesNum(bondedNodesNum);
        }
        
        @Override
        public void update(short[] request, DPA_Response response) {
            MutableFRC_TimingParams frcTimingParams 
                = (MutableFRC_TimingParams)timingParamsMap.get(ProtocolProperties.getPeripheralNumber(request));
            if ( frcTimingParams == null ) {
                frcTimingParams = new MutableFRC_TimingParams();
            }
            
            Coordinator.Command command = getCommand(ProtocolProperties.getCommand(request));
            if ( command == null ) {
                logger.error("Coordinator command not recognized: {}", ProtocolProperties.getCommand(request));
                return;
            }
            
            switch ( command ) {
                case GET_BONDED_NODES:
                    processGetBondedNodes(request, response, frcTimingParams);
                    break;
                case CLEAR_ALL_BONDS:
                    processClearAllBonds(request, response, frcTimingParams);
                    break;
                case BOND_NODE:
                    processBondNode(request, response, frcTimingParams);
                    break;
                case REMOVE_BONDED_NODE:
                    processRemoveBondedNode(request, response, frcTimingParams);
                    break;
                case REBOND_NODE:
                    processRebondNode(request, response, frcTimingParams);
                    break;
                default:
                    break;
            }
        }
    }
    
    // updates timing parameters according to Peripheral Info Getter request-response info
    private class PeripheralInfoGetter_Updater extends TimingParamsUpdater {

        @Override
        public void update(short[] request, DPA_Response response) {
            MutableFRC_TimingParams frcTimingParams 
                = (MutableFRC_TimingParams)timingParamsMap.get(ProtocolProperties.getPeripheralNumber(request));
            if ( frcTimingParams == null ) {
                frcTimingParams = new MutableFRC_TimingParams();
            }
            
            int command = ProtocolProperties.getCommand(request);
            
            switch ( command ) {
                case 0x3F:
                    int flags = response.getData()[11];
                    RF_Mode rfMode = ((flags & 1) == 1)? RF_Mode.STD : RF_Mode.LP;
                    frcTimingParams.setRfMode(rfMode);
                    break;
                default:
                    break;
            }
        }
    }
    
    // updates timing parameters according to FRC request-response info
    private class FRC_Updater extends TimingParamsUpdater {
        
        // determines command of FRC
        private FRC.Command getCommand(short commandId) {
            for ( FRC.Command command : FRC.Command.values() ) {
                if ( command.asByteValue()== commandId ) {
                    return command;
                }
            }
            return null;
        }
        
        // extracts response time set by the request and checks the response - 
        // if the response time has been correctly set
        private FRC_ResponseTime getResponseTime(short[] request, DPA_Response response) {
            int responseTimeValue = ProtocolProperties.getData(request)[0];
            
            for ( FRC_ResponseTime respTime : FRC_ResponseTime.values() ) {
                if ( respTime.getTimeAsInt() == responseTimeValue ) {
                    return respTime;
                }
            }
            return null;
        }
        
        private void processSetFRC_Params(
            short[] request, DPA_Response response, MutableFRC_TimingParams frcTimingParams
        ) {
            FRC_ResponseTime responseTime = getResponseTime(request, response);
            if ( responseTime != null ) {
                frcTimingParams.setResponseTime(responseTime);
            }
        }
        
        @Override
        public void update(short[] request, DPA_Response response) {
            MutableFRC_TimingParams frcTimingParams 
                = (MutableFRC_TimingParams)timingParamsMap.get(ProtocolProperties.getPeripheralNumber(request));
            if ( frcTimingParams == null ) {
                frcTimingParams = new MutableFRC_TimingParams();
            }

            FRC.Command command = getCommand(ProtocolProperties.getCommand(request));
            if ( command == null ) {
                logger.error("FRC command not recognized: {}", ProtocolProperties.getCommand(request));
                return;
            }
            
            switch ( command ) {
                case SET_FRC_PARAMS:
                    processSetFRC_Params(request, response, frcTimingParams);
                    break;
                default:
                    break;
            }
        }
    }
    
    // timing parameters map - keys are peripheral numbers
    private Map<Short, MutableTimingParams> timingParamsMap;
    
    // timing parameters updaters
    private Map<Short, TimingParamsUpdater> timingParamsUpdaters;
    
    
    private Map<String, InitialSettings> checkInitialSettings(
            Map<String, InitialSettings> networksInitialSettings
    ) {
        if ( networksInitialSettings == null ) {
            throw new IllegalArgumentException("Initial settings cannot be null.");
        }
        return networksInitialSettings;
    }
    
    private void initTimingParamsUpdaters() {
        timingParamsUpdaters = new HashMap<>();
        timingParamsUpdaters.put((short)ProtocolProperties.PNUM_Properties.COORDINATOR, new Coordinator_Updater());
        timingParamsUpdaters.put((short)ProtocolProperties.PNUM_Properties.FRC, new FRC_Updater());
        timingParamsUpdaters.put(
            (short)ProtocolProperties.PNUM_Properties.DEVICE_EXPLORATION, new PeripheralInfoGetter_Updater()
        );
    }
    
    
    /**
     * Creates new object of timing parameters storage.
     */
    public TimingParamsStorage() {
        initTimingParamsUpdaters();
        timingParamsMap = new HashMap<>();
    }
    
    /**
     * Returns timing parameters for specified request, or {@code null} if no
     * timing parameters is found for specified request.
     * 
     * @param request request, which to find the timing parameters for
     * @return timing parameters found for {@code request}, or {@code null}
     */ 
    public synchronized TimingParams getTimingParams(short[] request) {
        logger.debug("getTimingParams - start: request={}", request);
        
        short perNum = ProtocolProperties.getPeripheralNumber(request);
        
        MutableTimingParams mutTimingParams = timingParamsMap.get(perNum);
        if ( mutTimingParams == null ) {
            logger.info("getTimingParams: not found timing params for peripheral {}", perNum);
            logger.debug("getTimingParams - end: null");
            return null;
        }
        
        logger.debug("getTimingParams - end: {}");
        return mutTimingParams.getTimingParams();
    }
}
