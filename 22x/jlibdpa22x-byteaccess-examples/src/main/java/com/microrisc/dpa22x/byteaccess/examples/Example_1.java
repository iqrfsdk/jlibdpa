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
import com.microrisc.dpa22x.byteaccess.JByteAccess;
import com.microrisc.dpa22x.byteaccess.JByteAccessException;
import com.microrisc.dpa22x.byteaccess.RequestResult;
import com.microrisc.dpa22x.byteaccess.accessors.ByteAccessor;
import java.util.Arrays;

/**
 * Simple example of jbyteaccess's basic usage.
 * 
 * @author Michal Konopa
 */
public final class Example_1 {
    
    // prints out specified message, releases the library and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        JByteAccess.terminateAndRelease();
        System.exit(1);
    }
    
    public static void main(String[] args) {
        // mandatory - initialization of the library
        try {
            JByteAccess.init("byteaccess.properties");
        } catch ( JByteAccessException ex ) {
            printMessageAndExit("Initialization of the library failed: " + ex.getMessage());
        }
        
        // getting byte accessor
        ByteAccessor byteAccessor = JByteAccess.getAccessor();
        
        // send request and getting result
        RequestResult result = byteAccessor.sendRequest( new short[] {} );
        if ( result.getStatus() == RequestResult.Status.ERROR ) {
            System.out.println(
                "Error occured during processing of the request: " 
                + result.getProcessingInfo().getProcesssingError()
            );
            System.exit(2);
        }
        
        // analyzing of the result
        DPA_Response response = result.getResponse();
        if ( response.getResponseCode() == DPA_ResponseCode.NO_ERROR ) {
            System.out.println("Response: " + Arrays.toString( response.getData()));
        } else {
            System.out.println("DPA error: " + response.getResponseCode());
        }
        
        // terminating and releasing of library's resources
        JByteAccess.terminateAndRelease();
    }
}
