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

import com.microrisc.cdc.J_AsyncMsgListener;
import com.microrisc.dpa22x.DPA_Confirmation;
import com.microrisc.dpa22x.DPA_Response;
import com.microrisc.dpa22x.MessageParser;
import com.microrisc.dpa22x.MessageType;
import com.microrisc.dpa22x.ProtocolProperties;
import com.microrisc.dpa22x.byteaccess.ProcessingInfo;
import com.microrisc.dpa22x.byteaccess.RequestResult;
import com.microrisc.dpa22x.byteaccess.errors.DispatchRequestError;
import com.microrisc.dpa22x.byteaccess.errors.LibraryInternalError;
import com.microrisc.dpa22x.byteaccess.errors.NetworkInternalError;
import com.microrisc.dpa22x.byteaccess.errors.ReceiveDataError;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayer;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerException;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerListener;
import com.microrisc.dpa22x.peripherals.Coordinator;
import com.microrisc.dpa22x.timing.TimingParamsStorage;
import com.microrisc.dpa22x.timing.WaitingTimeCounter;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard implementation of Byte Accessor.
 * 
 * @author Michal Konopa
 */
public final class StandardByteAccessor 
extends AbstractByteAccessor
implements ByteAccessorControlInterface, J_AsyncMsgListener, NetworkLayerListener {
    
     /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(StandardByteAccessor.class);
    
    // multiplier for getting number of miliseconds from number of nanoseconds 
    private static final double NANOSEC_TO_MILISEC = 1.0/1000000;
    
    // time not defined
    private static final long TIME_NOT_DEFINED = -1;
    
    
    // data from network
    private Queue<short[]> dataFromNetwork;
    
    /** Synchronization object for waiting for responses from the network. */
    private final Object syncResponse = new Object();
    
    
    // confirmation of previous request
    private DPA_Confirmation prevRequestConfirmation = null;
    
    // reception time of previous request confirmation
    private long prevRequestConfirmationRecvTime = TIME_NOT_DEFINED;
    
    // previous request result
    private RequestResult previousRequestResult = null;
    
    
    // waits till previous IQMESH routing is finished
    private void waitTillPreviousIqmeshRoutingIsFinished() 
            throws InterruptedException, ByteAccessorException 
    {
        // no previous successfully sent request
        if ( previousRequestResult == null ) {
            return;
        }
        
        // successfully completed 
        if ( previousRequestResult.getStatus() == RequestResult.Status.SUCCESSFULLY_COMPLETED ) {
            
            // no confirmation
            if ( prevRequestConfirmation == null ) {
                return;
            }
            
            // illegal state - confirmation reception time MUST be set in this place
            if ( prevRequestConfirmationRecvTime == TIME_NOT_DEFINED ) {
                throw new ByteAccessorException("Confirmation reception time not set.");
            }
            
            long timeToWait = waitingTimeCounter
                .getTimeToWaitToFinishIqmeshRouting(
                        prevRequestConfirmation, 
                        previousRequestResult.getResponse().getData().length, 
                        prevRequestConfirmationRecvTime
                );
            Thread.sleep(timeToWait);
        }
        
        // if there was some error, do nothing as it is not avalaible the response
        // PData length
    }
    
    // cleans up previous request data
    private void cleanUpPreviousRequestData() {
        prevRequestConfirmation = null;
        prevRequestConfirmationRecvTime = TIME_NOT_DEFINED;
        previousRequestResult = null;
    }
    
    // updates previous request data
    private void updatePreviousRequestData(
        DPA_Confirmation confirmation, long confirmationRecvTime, RequestResult requestResult
    ) {
        prevRequestConfirmation = confirmation;
        prevRequestConfirmationRecvTime = confirmationRecvTime;
        previousRequestResult = requestResult;
    }
    
    // storage of timig params
    private TimingParamsStorage timingParamsStorage;
    
    // indicates, if the request denotes long lasting operation
    private static boolean isLongLastingOperation(short[] request) {
        short pnum = ProtocolProperties.getPeripheralNumber(request);
        switch ( pnum ) {
            case ProtocolProperties.PNUM_Properties.COORDINATOR:
                short command = ProtocolProperties.getCommand(request);
                if ( 
                    command == Coordinator.Command.BOND_NODE.asByteValue()
                    || command == Coordinator.Command.RUN_DISCOVERY.asByteValue()
                ) {
                    return true;
                } 
                return false;
            default:
                return false;
        }
    }
    
    
    /**
     * Creates Standard Byte Accessor object.
     *
     * @param networkLayer network layer to use
     * @param waitingTimeCounter reference to waiting time counter
     * @throws com.microrisc.dpa22x.byteaccess.accessors.ByteAccessorException
     *         if some exception has occurred during creation
     */
    public StandardByteAccessor(
            NetworkLayer networkLayer, WaitingTimeCounter waitingTimeCounter
    ) throws ByteAccessorException 
    {
        super(networkLayer, waitingTimeCounter);
        this.dataFromNetwork = new ConcurrentLinkedQueue<>();
        this.timingParamsStorage = new TimingParamsStorage();
        
        logger.debug("Standard byte accessor created");
    }
    
    @Override
    public void start() throws ByteAccessorException {
        logger.debug("start - start");
        
        try {
            networkLayer.start();
        } catch (NetworkLayerException ex) {
            throw new ByteAccessorException(ex);
        }
        
        networkLayer.registerListener(this);
        
        logger.debug("start - end");
    }
    
    @Override
    public RequestResult sendRequest(short[] request, long waitingTimeout) {
        logger.debug(
                "sendRequest - start: request={}, waitingTimeout={}",
                Arrays.toString(request), waitingTimeout
        );
        checkWaitingTimeout(waitingTimeout);
        
        try {
            waitTillPreviousIqmeshRoutingIsFinished();
        } catch ( Exception ex ) {
            logger.error("Error while waiting till previous IQMESH routing is finished: {}", ex);
            return new RequestResult(
                RequestResult.Status.ERROR, null, new ProcessingInfo( new LibraryInternalError(ex) )
            );
        }
        
        // cleaning up previous request data
        cleanUpPreviousRequestData();
        
        // sending data into network
        try {
            networkLayer.sendData(request);
        } catch ( NetworkLayerException ex ) {
            logger.error("Error while dispatching request: {}", ex);
            logger.debug("sendRequest - end");
            
            return new RequestResult(
                RequestResult.Status.ERROR, null, new ProcessingInfo( new DispatchRequestError(ex) )
            );
        }
        
        boolean waitForConfirmation = false;
        long timeToWait = 0;
        boolean isLongLastingOperationInProgress = false;
        
        if ( ProtocolProperties.isAddresseeLocalDevice(request) ) {
            if ( isLongLastingOperation(request) ) {
                isLongLastingOperationInProgress = true;
            } else {
                timeToWait = waitingTimeCounter.getTimeToWaitForResponse(
                    request, null, timingParamsStorage.getTimingParams(request)
                );
            }
        } else {
            timeToWait = waitingTimeCounter.getTimeToWaitForConfirmation(request);
            waitForConfirmation = true;
        }
        
        // data for lastly processed request to store
        DPA_Confirmation confirmation = null;
        long confirmationRecvTime = TIME_NOT_DEFINED;
        RequestResult requestResult = null;
        long startTime = 0;
        double timeElapsed = 0.0;
        
        // waiting for message from network
        synchronized ( syncResponse ) {
            while ( requestResult == null ) {
                boolean timeoutElapsed = false;
                boolean continueToWait = false;
                
                // waiting until some data has come into or timout has elapsed
                while ( dataFromNetwork.isEmpty() && !timeoutElapsed ) {
                    if ( !continueToWait ) {
                        if ( isLongLastingOperationInProgress ) {
                            if ( waitingTimeout == ByteAccessor.WAITING_TIMEOUT_NOT_LIMITED ) {
                                timeToWait = 0;
                            } else {
                                timeToWait = waitingTimeout;
                            }
                        }
                    }
                    
                    System.out.println("Time to wait: " + timeToWait);
                    startTime = System.nanoTime();
                    try {
                        syncResponse.wait(timeToWait);
                    } catch (InterruptedException ex ) {
                        logger.warn("Waiting for data from network interrupted: {}", ex);
                    
                        requestResult = new RequestResult(
                                RequestResult.Status.ERROR, null, 
                                new ProcessingInfo( new ReceiveDataError(
                                        "Waiting for data interrupted.") 
                                )
                        );
                    }
                    
                    timeElapsed = (System.nanoTime() - startTime) * NANOSEC_TO_MILISEC;
                    System.out.println("Time elapsed: " + timeElapsed);
                    
                    if ( !isLongLastingOperationInProgress 
                        || ( waitingTimeout != ByteAccessor.WAITING_TIMEOUT_NOT_LIMITED )
                    ) {
                        
                        timeToWait -= timeElapsed;
                        
                        // waiting for message timeouted
                        if ( timeToWait <= 0 ) {
                            logger.warn("Waiting for data from network timeouted ");

                            requestResult = new RequestResult(
                                    RequestResult.Status.ERROR, null, 
                                    new ProcessingInfo( new NetworkInternalError(
                                            "Waiting for data timeouted.") 
                                    )
                            );
                            timeoutElapsed = true;
                        } else {
                            continueToWait = true;
                        }
                    }
                }
                
                // if timeout has elapsed break and return request result
                if ( timeoutElapsed ) {
                    break;
                }
                
                short[] data = dataFromNetwork.poll();
                    
                MessageType msgType = null;
                try {
                    msgType = ProtocolProperties.getMessageType(data);
                } catch ( IllegalStateException ex ) {
                    // unknown type of message
                    logger.error("Unknown type of message: {}", Arrays.toString(data));

                    requestResult = new RequestResult(
                            RequestResult.Status.ERROR, null, 
                            new ProcessingInfo( new NetworkInternalError(
                                    "Unknown type of message: " + Arrays.toString(data)) 
                            )
                    );
                    break;
                }
                    
                switch ( msgType ) {
                    case CONFIRMATION:
                        if ( waitForConfirmation ) {
                            waitForConfirmation = false;

                            try {
                                confirmation = (DPA_Confirmation) MessageParser.parse(data);
                            } catch ( Exception ex ) {
                                logger.error("Error in parsing confirmation: {}", ex);

                                requestResult = new RequestResult(
                                    RequestResult.Status.ERROR, null,
                                    new ProcessingInfo( new ReceiveDataError(ex) ) 
                                );
                                break;
                            }

                            logger.info("Confirmation successfully received: {}", confirmation);

                            confirmationRecvTime = startTime + (long)timeElapsed;

                            if ( isLongLastingOperation(request) ) {
                                isLongLastingOperationInProgress = true;
                            } else {
                                timeToWait = waitingTimeCounter
                                        .getTimeToWaitForResponse(
                                                request, confirmation, 
                                                timingParamsStorage.getTimingParams(request)
                                        );
                            }
                        } else {
                            // unexpected confirmation
                            logger.error("Unexpected confirmation: {}", Arrays.toString(data));

                            requestResult = new RequestResult(
                                    RequestResult.Status.ERROR, null, 
                                    new ProcessingInfo( new NetworkInternalError(
                                            "Unexpected confirmation: " + Arrays.toString(data)) 
                                    )
                            );
                        }
                        break;
                    case RESPONSE:
                        if ( waitForConfirmation ) {
                            // unexpected response
                            logger.error("Unexpected response: {}", Arrays.toString(data));

                            requestResult = new RequestResult(
                                    RequestResult.Status.ERROR, null, 
                                    new ProcessingInfo( new NetworkInternalError(
                                        "Unexpected response: " + Arrays.toString(data)) 
                                    )
                            );
                        } else {
                            // response arrived
                            DPA_Response response = null;
                            try {
                                response = (DPA_Response) MessageParser.parse(data);
                            } catch ( Exception ex ) {
                                logger.error("Error in parsing response: {}. Data: {}", ex, Arrays.toString(data));

                                requestResult = new RequestResult(
                                    RequestResult.Status.ERROR, null,
                                    new ProcessingInfo( new ReceiveDataError(ex) ) 
                                );
                                break;
                            }

                            logger.info("Response successfully received: {}", response);

                            requestResult = new RequestResult(
                                    RequestResult.Status.SUCCESSFULLY_COMPLETED,
                                    response,
                                    new ProcessingInfo() 
                            );
                        }
                        break;
                    default:
                        // unknown type of message
                        logger.error("Unknown type of message: {}", Arrays.toString(data));

                        requestResult = new RequestResult(
                                RequestResult.Status.ERROR, null, 
                                new ProcessingInfo( new NetworkInternalError(
                                        "Unknown type of message: " + Arrays.toString(data)) 
                                )
                        );
                }
            }
        }
        
        updatePreviousRequestData(confirmation, confirmationRecvTime, requestResult);
        
        logger.debug("sendRequest - end");
        return requestResult;
    }

    @Override
    public RequestResult sendRequest(short[] request) {
        return sendRequest(request, defaultWaitingTimeout);
    }
    
    @Override
    public void onGetMessage(short[] data) {
        logger.debug("onGetMessage - start: data={}", Arrays.toString(data) );
        
        synchronized ( syncResponse ) {
            dataFromNetwork.add(data);
            syncResponse.notifyAll();
        }
        
        logger.debug("onGetMessage - end");
    }
    
    @Override
    public void terminateAndRelease() {
        logger.debug("destroy - start: ");
        
        networkLayer.unregisterListener();
        networkLayer.terminateAndRelease();

        dataFromNetwork.clear();
        dataFromNetwork = null;
        
        timingParamsStorage = null;
        
        logger.info("CDC Byte Accessor destroyed.");
        logger.debug("destroy - end");
    }

    @Override
    public void onGetData(short[] data) {
        logger.debug("onGetData - start: data={}", Arrays.toString(data) );
        
        synchronized ( syncResponse ) {
            dataFromNetwork.add(data);
            syncResponse.notifyAll();
        }
        
        logger.debug("onGetData - end");
    }
    
}
