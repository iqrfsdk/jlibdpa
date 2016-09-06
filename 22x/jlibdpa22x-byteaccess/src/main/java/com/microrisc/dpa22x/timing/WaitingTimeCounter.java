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

import com.microrisc.dpa22x.DCTR;
import com.microrisc.dpa22x.DPA_Confirmation;
import com.microrisc.dpa22x.ProtocolProperties;
import com.microrisc.dpa22x.RF_Mode;
import static com.microrisc.dpa22x.RF_Mode.LP;
import static com.microrisc.dpa22x.RF_Mode.STD;
import com.microrisc.dpa22x.peripherals.FRC;
import com.microrisc.dpa22x.peripherals.UART;
import java.util.HashMap;
import java.util.Map;

/**
 * Counter of waiting times.
 * 
 * @author Michal Konopa
 */
public final class WaitingTimeCounter {
    
    /** Default RF mode - STD. */
    public static final RF_Mode RF_MODE_DEEFAULT = RF_Mode.STD;
    
    /** DCTR HW default - DCTR-7xD. */
    public static final DCTR DCTR_DEFAULT = DCTR.DCTR_7xD;
    
    
    /** Delay of incomming messages caused by the library itself. */
    public static final long LIBRARY_DELAY = 2000;
    
    /** Default time to wait for confirmation [ in ms ]. */
    public static final long TIME_TO_WAIT_FOR_CONFIRMATION_DEFAULT = LIBRARY_DELAY;
    
    /** Safety timeout [ in ms ]. */
    public static final long SAFETY_TIMEOUT = 40;
    
    
    // RF mode
    private final RF_Mode rfMode;
    
    // DCTR HW type
    private final DCTR dctr;
    
    // time to wait for confirmation
    private final long timeToWaitForConfirmation;
    
    
    // timeslot length for DCTR-5xD
    private static int getTimeslotLengthFor_5xD(int pDataLength, RF_Mode rfMode) {
        switch ( rfMode ) {
            case STD:
                if ( pDataLength < 12 ) {
                    return 30;
                } else if ( pDataLength <= 32 ) {
                    return 40;
                } else if ( pDataLength <= 53 ) {
                    return 50;
                } else {
                    return 60;
                }
            case LP:
                if ( pDataLength < 14 ) {
                    return 80;
                } else if ( pDataLength <= 35 ) {
                    return 90;
                } else {
                    return 100;
                }
            default:
                throw new IllegalArgumentException("Unsupported RF mode: " + rfMode);
        }
    }
    
    // timeslot length for DCTR-7xD
    private static int getTimeslotLengthFor_7xD(int pDataLength, RF_Mode rfMode) {
        switch ( rfMode ) {
            case STD:
                if ( pDataLength < 19 ) {
                    return 30;
                } else if ( pDataLength <= 41 ) {
                    return 40;
                } else {
                    return 50;
                }
            case LP:
                if ( pDataLength < 9 ) {
                    return 80;
                } else if ( pDataLength <= 31 ) {
                    return 90;
                } else {
                    return 100;
                }
            default:
                throw new IllegalArgumentException("Unsupported RF mode: " + rfMode);
        }
    }
    
    // returns timeslot length [in ms]
    private static int getTimeslotLength(int pDataLength, RF_Mode rfMode, DCTR dctr) {
        switch ( dctr ) {
            case DCTR_5xD:
                return getTimeslotLengthFor_5xD(pDataLength, rfMode);
            case DCTR_7xD:
                return getTimeslotLengthFor_7xD(pDataLength, rfMode);
            default:
                throw new IllegalArgumentException("Unsupported DCTR: " + dctr);
        }
        
    }
    
    private static RF_Mode checkRfMode(RF_Mode rfMode) {
        if ( rfMode == null ) {
            throw new IllegalArgumentException("RF mode cannot be null.");
        }
        return rfMode;
    }
    
    private static DCTR checkDctr(DCTR dctr) {
        if ( dctr == null ) {
            throw new IllegalArgumentException("DCTR mode cannot be null.");
        }
        return dctr;
    }
    
    private static long checkTimeToWaitForConfirmation(long timeToWaitForConfirmation) {
        if ( timeToWaitForConfirmation < 0 ) {
            throw new IllegalArgumentException("Time to wait for confirmation must be nonnegative.");
        }
        return timeToWaitForConfirmation;
    }
    
    
    // base class for all special waiting time for response counters 
    private static abstract class SpecialWaitingTimeForResponseCounter {
        
       // counts and returns waiting time [in ms]
       public abstract long count(short[] request, TimingParams timingParams);
    }
    
    
    // computes waiting time for FRC requests 
    private static class FRC_WaitingTimeForResponseCounter extends SpecialWaitingTimeForResponseCounter {
        
        // FRC mode
        static enum FRC_Mode {
            STANDARD,
            ADVANCED
        }
        
        // maximal lenth of data for sending in standard FRC mode
        private static final int STANDARD_FRC_MAX_DATA_LENGTH = 2;
        
        // determines command
        private static FRC.Command getCommand(short commandId) {
            for ( FRC.Command command : FRC.Command.values() ) {
                if ( command.asByteValue()== commandId ) {
                    return command;
                }
            }
            return null;
        }
        
        // determines and returns FRC Mode
        private static FRC_Mode getFRC_Mode(short[] request, FRC.Command command) {
            switch ( command ) {
                case SEND:
                case SEND_SELECTIVE:
                    // -1 for FRC Command byte
                    int dataLength = ProtocolProperties.getData(request).length - 1;
                    return ( dataLength <= STANDARD_FRC_MAX_DATA_LENGTH)? 
                            FRC_Mode.STANDARD : FRC_Mode.ADVANCED;
                default:
                    return FRC_Mode.STANDARD;
            }
        }
          
        // counts waiting time according to specified parameters
        private static long countWaitingTime(
            FRC_Mode frcMode, FRC_TimingParams timingParams
        ) {
            FRC_ResponseTime coordWaitingTime = timingParams.getResponseTime();
            int coordWaitingTimeInInt = coordWaitingTime.getTimeAsInt();
            
            switch ( frcMode ) {
                case STANDARD:
                    return timingParams.getBondedNodesNum() * 130 + coordWaitingTimeInInt + 250;
                case ADVANCED:
                    if ( timingParams.getRfMode() == null ) {
                        throw new IllegalStateException("RF mode uknown.");
                    }
                    
                    switch ( timingParams.getRfMode() ) {
                        case STD:
                            return timingParams.getBondedNodesNum() * 150 
                                    + coordWaitingTimeInInt + 290;
                        case LP:
                            return timingParams.getBondedNodesNum() * 200 
                                    + coordWaitingTimeInInt + 390;
                        default:
                            throw new IllegalStateException("RF mode not supported: " + timingParams.getRfMode());
                    }
                default:
                    throw new IllegalStateException("FRC Mode not supported: " + frcMode); 
            }
        }
        
        @Override
        public long count(short[] request, TimingParams timingParams) {
            FRC.Command command = getCommand(ProtocolProperties.getCommand(request));
            if ( command == null ) {
                throw new IllegalStateException("FRC command not found.");
            }
            
            if ( (command == FRC.Command.EXTRA_RESULT) || (command == FRC.Command.SET_FRC_PARAMS)) {
                return 0;
            }
            
            if ( timingParams == null ) {
                throw new IllegalArgumentException("FRC timing parameters is null.");
            }
            
            if ( !(timingParams instanceof FRC_TimingParams) ) {
                throw new IllegalArgumentException(
                    "Timing parameters has not correct type. "
                    + "Expected: " + FRC_TimingParams.class
                    + "found: " + timingParams.getClass()
                );
            }
            
            FRC_Mode frcMode = getFRC_Mode(request, command);
            return countWaitingTime(frcMode, (FRC_TimingParams)timingParams);
        }
    }
    
    private static class UART_WaitingTimeForResponseCounter extends SpecialWaitingTimeForResponseCounter {
        
        // determines the command
        private static UART.Command getCommand(short commandId) {
            for ( UART.Command command : UART.Command.values() ) {
                if ( command.asByteValue() == commandId ) {
                    return command;
                }
            }
            return null;
        }
        
        // return value of timeout argument from READ AND WRITE command
        private int getTimeout(short[] request) {
            return request[ProtocolProperties.PDATA_START_POS];
        }
        
        @Override
        public long count(short[] request, TimingParams timingParams) {
            UART.Command command = getCommand(ProtocolProperties.getCommand(request));
            if ( command == null ) {
                throw new IllegalStateException("UART method not found.");
            }
            
            if ( command != UART.Command.WRITE_READ ) {
                return 0; 
            }
            
            int timeout = getTimeout(request);
            
            // if timeout = 0xff, no data should be read from UART after data is
            // (optionally) witten
            if ( timeout == 0xFF ) {
                return 0;
            }
            
            return timeout * 10;
        }
    }
    
    
    // if the request is special request(FRC, UART, ...), returns additional time 
    // for waiting for a response, else returns 0
    private long getAdditionalTimeForSpecialRequest(short[] request, TimingParams timingParams) 
    {
        SpecialWaitingTimeForResponseCounter specialWaitingTimeCounter
                = specialWaitingTimeCounters.get(ProtocolProperties.getPeripheralNumber(request));
    
        if ( specialWaitingTimeCounter == null ) {
            return 0;
        }
        
        return specialWaitingTimeCounter.count(request, timingParams);
    }
    
    // special waiting time for response counters
    private Map<Short, SpecialWaitingTimeForResponseCounter> specialWaitingTimeCounters;
    
    // inits special waitig counters
    private void initSpecialWaitingCounters() {
        specialWaitingTimeCounters = new HashMap<>();
        specialWaitingTimeCounters.put(Short.MIN_VALUE, new FRC_WaitingTimeForResponseCounter());
        specialWaitingTimeCounters.put(Short.MIN_VALUE, new UART_WaitingTimeForResponseCounter());
    }
    
    
    /**
     * Creates new object of Waiting Counter with default settings.
     */
    public WaitingTimeCounter() {
        this.rfMode = RF_MODE_DEEFAULT;
        this.dctr = DCTR_DEFAULT;
        this.timeToWaitForConfirmation = TIME_TO_WAIT_FOR_CONFIRMATION_DEFAULT;
        initSpecialWaitingCounters();
    }
    
    /**
     * Creates new object of Waiting Counter with specified settings.
     * 
     * @param rfMode RF mode
     * @param dctr DCTR HW type
     * @param timeToWaitForConfirmation time to wait for confirmation [in ms]
     */
    public WaitingTimeCounter(RF_Mode rfMode, DCTR dctr, long timeToWaitForConfirmation) {
        this.rfMode = checkRfMode(rfMode);
        this.dctr = checkDctr(dctr);
        this.timeToWaitForConfirmation = checkTimeToWaitForConfirmation(timeToWaitForConfirmation);
        initSpecialWaitingCounters();
    }
    
    
    /**
     * Returns time to wait for response on specified request.
     * 
     * @param request request whose response waiting time to return
     * @param confirmation confirmation on specified request
     *        Can be {@code null}, if the request is targeted for local device and
     *        no confirmation is emited
     * @param timingParams timing parameters 
     *        Can be {@code null}, if the waiting time for the request is counted
     *        in a normal way (unlike FRC, UART, ...)
     * @return waiting time for response on {@code request}
     */
    public long getTimeToWaitForResponse(
            short[] request, DPA_Confirmation confirmation, TimingParams timingParams
    ) {
        if ( request == null ) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        
        // handling special requests, i.e. FRC, UART etc. where it is neccessary 
        // to add some additional time 
        long additionalTimeForSpecialRequest = getAdditionalTimeForSpecialRequest(request, timingParams);
        
        // waiting for response from local device
        if ( confirmation == null ) {
            return LIBRARY_DELAY + additionalTimeForSpecialRequest;
        }
        
        long estimatedTimeout = (confirmation.getHops() + 1) * confirmation.getTimeslotLength() * 10;
            
        long respTimeslotLength = 0;
        if ( confirmation.getTimeslotLength() == 20 ) {
            respTimeslotLength = 200;
        } else {
            if ( confirmation.getTimeslotLength() > 6 ) {
                // DPA in LP mode
                respTimeslotLength = 100;
            } else {
                // DPA in STD mode
                respTimeslotLength = 50;
            }
        }

        estimatedTimeout += (confirmation.getHopsResponse() + 1) * respTimeslotLength + SAFETY_TIMEOUT;
        return estimatedTimeout + LIBRARY_DELAY + additionalTimeForSpecialRequest;
    }
    
    /**
     * Returns time to wait for confirmation on specified request.
     * 
     * @param request request whose confirmation waiting time to return
     * @return waiting time for confirmation on {@code request}
     */
    public long getTimeToWaitForConfirmation(short[] request) {
        return timeToWaitForConfirmation;
    }
    
    /**
     * Returns time to wait to finish IQMESH routing.
     * 
     * @param confirmation confirmation
     * @param reponseDataLength length of response's PData
     * @param confirmRecvTime time the confirmation was received
     * @return time to wait to finish IQMESH routing
     */
    public long getTimeToWaitToFinishIqmeshRouting(
            DPA_Confirmation confirmation, int reponseDataLength, long confirmRecvTime
    ) {
        long actualRespTimeslotLength = getTimeslotLength(reponseDataLength, rfMode, dctr);
        long timeToWait = 
            confirmRecvTime
            +
            (confirmation.getHops() + 1 ) * confirmation.getTimeslotLength() * 10
            + ( confirmation.getHopsResponse() + 1 ) * actualRespTimeslotLength  * 10
            - System.currentTimeMillis();
        
        if ( timeToWait < 0 ) {
            timeToWait = 0;
        }
        
        return timeToWait;
    }
}
