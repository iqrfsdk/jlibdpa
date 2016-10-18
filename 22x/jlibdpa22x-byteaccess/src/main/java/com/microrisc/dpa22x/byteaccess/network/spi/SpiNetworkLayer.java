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
package com.microrisc.dpa22x.byteaccess.network.spi;

import com.microrisc.dpa22x.byteaccess.network.AbstractNetworkLayer;
import com.microrisc.dpa22x.byteaccess.network.NetworkLayerException;
import com.microrisc.rpi.spi.SPI_Exception;
import com.microrisc.rpi.spi.iqrf.SPI_Master;
import com.microrisc.rpi.spi.iqrf.SPI_Status;
import com.microrisc.rpi.spi.iqrf.SimpleSPI_Master;
import java.util.LinkedList;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SPI network layer.
 * 
 * @author Michal Konopa
 */
public final class SpiNetworkLayer 
extends AbstractNetworkLayer {
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpiNetworkLayer.class);

    /** Port name. */
    private String portName = null;
    
    /** Default maximal number of SPI status retries. */
    public static int MAX_SPI_STATUS_RETRIES = 3;
    
    /** Maximal number of SPI status retries. */
    private int maxStatusRetries = MAX_SPI_STATUS_RETRIES;
    
    
    /** SPI master. */
    private SPI_Master spiMaster = null;
    
    /** Data received from SPI. */
    private Queue<short[]> dataFromSPI = null;
    
    
    /** Default maximal size of received packets [in bytes]. */
    public static int MAX_RECEIVED_PACKET_SIZE = 128;

    /** Maximal size of received packets [in bytes]. */
    private int maxRecvPacketSize = MAX_RECEIVED_PACKET_SIZE;
    
    /** Synchronization to SPI access. */
    private final Object spiSynchro = new Object();

    /**
     * Synchronization between socket reader thread and listener caller thread.
     */
    private final Object threadsSynchro = new Object();
    
    
    // coverts data to hex values
    private static String toHexString(short[] data) {
        StringBuilder sb = new StringBuilder();

        sb.append('[');
        for ( int i = 0; i < data.length; i++ ) {
            sb.append(String.format("%02X", data[i]));
            sb.append('.');
        }
        sb.append(']');

        return sb.toString();
    }
    
    
    /** Reader of data from SPI. */
    private class SpiReader extends Thread {

        @Override
        public void run() {
            short[] buffer = new short[maxRecvPacketSize];
            boolean newDataReceived = false;
            int dataLen = 0;

            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("SPI reader thread interrupted");
                    return;
                }

                try {
                    synchronized ( spiSynchro ) {
                        SPI_Status spiStatus = spiMaster.getSlaveStatus();
                        //logger.info("Reading thread SPI status: {}", spiStatus.getValue());

                        if ( spiStatus.isDataReady() ) {
                            logger.info("Data ready!");
                            if ( spiStatus.getValue() == 0x40 ) {
                                dataLen = 64;
                            } else {
                                dataLen = spiStatus.getValue() - 0x40;
                            }

                            buffer = spiMaster.readData(dataLen);
                            newDataReceived = true;
                        }
                    }

                    // if new data has received add it into the queue
                    if ( newDataReceived ) {
                        logger.info("New data from SPI: {}", toHexString(buffer));

                        synchronized ( threadsSynchro ) {
                            dataFromSPI.add(buffer);
                            threadsSynchro.notify();
                        }
                        newDataReceived = false;
                    }

                    Thread.sleep(10);
                } catch ( SPI_Exception ex ) {
                    logger.error("Error while receiving SPI interface: ", ex);
                } catch ( InterruptedException ex ) {
                    logger.warn("SPI reader thread interrupted while sleeping.");
                    return;
                }
            }
        }
    }
    
    /**
     * Calling listener callback method - when new data has arrived from socket.
     */
    private class ListenerCaller extends Thread {

        // already consumed data from socket
        private Queue<short[]> consumedData = new LinkedList<>();

        // indicates, whether there are new data from SPI
        private boolean areDataReadyFromSPI() {
            return ( !dataFromSPI.isEmpty() );
        }

        // consume data from spi and adds them into buffer
        private void consumeDataFromSPI() {
            while ( !dataFromSPI.isEmpty() ) {
                short[] packetData = dataFromSPI.poll();
                consumedData.add(packetData);
            }
        }

        /**
         * Frees up used resources.
         */
        private void freeResources() {
            consumedData.clear();
        }

        @Override
        public void run() {
            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("SPI caller thread interrupted");
                    freeResources();
                    return;
                }

                // consuming new data from SPI
                synchronized ( threadsSynchro ) {
                    while ( !areDataReadyFromSPI() ) {
                        try {
                            threadsSynchro.wait();
                        } catch ( InterruptedException ex ) {
                            logger.warn("SPI caller thread interrupted while "
                                    + "waiting on data from SPI.");
                            freeResources();
                            return;
                        }
                    }
                    consumeDataFromSPI();
                }

                // remove data from queue and send it to listener
                while ( !consumedData.isEmpty() ) {
                    short[] userData = consumedData.poll();
                    if ( listener != null ) {
                        listener.onGetData( userData );
                    }
                }
            }
        }
    }
    
    
    /** SPI reader thread. */
    private Thread spiReader = null;
    
    /** Listener caller thread. */
    private Thread listenerCaller = null;
    
    
    // creates and starts threads
    private void createAndStartThreads() {
        spiReader = new SpiReader();
        spiReader.start();

        listenerCaller = new ListenerCaller();
        listenerCaller.start();
    }
    
    // terminates SPI reader and listener caller threads
    private void terminateThreads() {
        logger.debug("terminateThreads - start:");

        // termination signal to socket reader thread
        spiReader.interrupt();

        // termination signal to listener caller thread
        listenerCaller.interrupt();

        // Waiting for threads to terminate. Cancelling worker threads has higher 
        // priority than main thread interruption. 
        while ( spiReader.isAlive() || listenerCaller.isAlive() ) {
            try {
                if ( spiReader.isAlive() ) {
                    spiReader.join();
                }

                if ( listenerCaller.isAlive() ) {
                    listenerCaller.join();
                }
            } catch ( InterruptedException e ) {
                // restoring interrupt status
                Thread.currentThread().interrupt();
                logger.warn("Termination - SPI Network Layer interrupted");
            }
        }

        logger.info("SPI Network Layer stopped.");
        logger.debug("terminateThreads - end");
    }
    
    private static String checkPortName(String portName) {
        if ( portName == null ) {
            throw new IllegalArgumentException("Port name cannot be null");
        }

        if ( portName.equals("") ) {
            throw new IllegalArgumentException("Port name cannot be empty string");
        }

        return portName;
    }
    
    private static int checkMaxStatusRetries(int maxStatusRetries) {
        if ( maxStatusRetries < 0 ) {
            throw new IllegalArgumentException("Maximal status retries must be nonnegative.");
        }

        return maxStatusRetries;
    }
    
    
    /**
     * Creates new SPI network layer object.
     * 
     * @param portName SPI-port name for communication
     */
    public SpiNetworkLayer(String portName) {
        this.portName = checkPortName(portName);
        this.maxStatusRetries = MAX_SPI_STATUS_RETRIES;
    }
    
    /**
     * Creates new SPI network layer object.
     * 
     * @param portName SPI-port name for communication
     * @param maxStatusRetries maximal number of status retries, must be nonnegative
     * @throws IllegalArgumentException if {@code maxStatusRetries} is less than 0
     */
    public SpiNetworkLayer(String portName, int maxStatusRetries) {
        this.portName = checkPortName(portName);
        this.maxStatusRetries = checkMaxStatusRetries(maxStatusRetries);
    }
    
    @Override
    public void start() throws NetworkLayerException {
        logger.debug("start - start:");

        // initialization
        try {
            spiMaster = new SimpleSPI_Master(portName);
        } catch ( SPI_Exception ex ) {
            throw new NetworkLayerException(ex);
        }

        // init queue of data comming from SPI
        dataFromSPI = new LinkedList<>();

        // creating and starting threads
        createAndStartThreads();

        logger.info("Receiving data started");
        logger.debug("start - end");
    }

    @Override
    public void sendData(short[] data) throws NetworkLayerException {
        logger.debug("sendData - start: data={}", toHexString(data));

        try {
            logger.info("Data will be sent to SPI...");
            
            synchronized ( spiSynchro ) { 
                boolean dataSent = false;
                int attempt = 0;
                
                while ( attempt++ < maxStatusRetries ) {
                    // have some space before sending another request
                    Thread.sleep(50);
                    
                    // getting slave status
                    SPI_Status spiStatus = spiMaster.getSlaveStatus();
                    logger.info("Writing thread SPI status: {}", spiStatus.getValue());

                    if ( spiStatus.getValue() == SPI_Status.READY_COMM_MODE ) {
                        // sending some data to device
                        spiMaster.sendData(data);
                        logger.info("Data successfully sent to SPI");
                        dataSent = true;
                        break;
                    } else {
                        logger.info("Data not sent to SPI, module is not in READY_COMM_MODE: retries {} ", attempt);
                    }
                }
                
                if ( !dataSent ) {
                    throw new NetworkLayerException( new SPI_Exception("Data has not been sent to the module!"));
                }
            }
        } catch ( SPI_Exception ex ) {
            throw new NetworkLayerException(ex);
        } catch ( InterruptedException ex ) {
            throw new NetworkLayerException(ex);
        }
    }

    @Override
    public void terminateAndRelease() {
        logger.debug("terminateAndRelease - start: ");
        
        unregisterListener();
        logger.info("SPI Listener unregistered");
        
        terminateThreads();
        dataFromSPI.clear();
        spiMaster.destroy();
        spiMaster = null;
        
        logger.info("SPI Network Layer terminated and released");
        logger.debug("terminateAndRelease - end");
    }
    
}
