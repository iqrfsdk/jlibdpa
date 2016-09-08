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
package com.microrisc.dpa22x.byteaccess.examples;

import com.microrisc.dpa22x.DPA_Response;
import com.microrisc.dpa22x.DPA_ResponseCode;
import com.microrisc.dpa22x.ProtocolProperties;
import com.microrisc.dpa22x.byteaccess.JByteAccess;
import com.microrisc.dpa22x.byteaccess.JByteAccessException;
import com.microrisc.dpa22x.byteaccess.RequestResult;
import com.microrisc.dpa22x.byteaccess.accessors.ByteAccessor;
import com.microrisc.dpa22x.peripherals.LEDR;
import java.io.File;

/**
 * Example of LEDR pulse on nodes.
 * 
 * @author Michal Konopa
 */
public final class Example_LedrPulse {
    
    // prints out specified message, releases the library and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        JByteAccess.terminateAndRelease();
        System.exit(1);
    }
    
    public static void main(String[] args) {
        // mandatory - initialization of the library
        try {
            JByteAccess.init("config" + File.separator + "byteaccess.properties");
        } catch ( JByteAccessException ex ) {
            printMessageAndExit("Initialization of the library failed: " + ex.getMessage());
        }
        
        // getting byte accessor
        ByteAccessor byteAccessor = JByteAccess.getAccessor();
        
        // creation of LEDR pulse request
        short[] ledrRequest = new short[ProtocolProperties.FOURSOME_LENGTH];
        ledrRequest[ProtocolProperties.PNUM_POS] = LEDR.PNUM;
        ledrRequest[ProtocolProperties.PCMD_POS] = LEDR.Command.PULSE.asByteValue();
        ledrRequest[ProtocolProperties.HWPID_START] = 0xFF;
        ledrRequest[ProtocolProperties.HWPID_START + 1] = 0xFF;
        
        // send requests for LEDR pulse
        for ( short nodeId = 0x00; nodeId <= 0x01; nodeId++ ) {
            
            // set target node address
            ledrRequest[ProtocolProperties.NADR_START] = nodeId;
            
            RequestResult result = byteAccessor.sendRequest( ledrRequest );
            if ( result.getStatus() == RequestResult.Status.SUCCESSFULLY_COMPLETED ) {
                DPA_Response response = result.getResponse();
                if ( response.getResponseCode() == DPA_ResponseCode.NO_ERROR ) {
                    System.out.println("Node " + nodeId + ": request successfully completed");
                } else {
                    System.out.println("Node " + nodeId + ": " + response.getResponseCode() );
                }
            } else {
                System.out.println(
                    "Node " + nodeId + ": error occured during processing of the request: " 
                    + result.getProcessingInfo().getProcesssingError()
                );
            }
        }
        
        // terminating and releasing of library's resources
        JByteAccess.terminateAndRelease();
    }
}
