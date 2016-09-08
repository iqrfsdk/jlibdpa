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
package com.microrisc.dpa22x;

import java.util.Arrays;

/**
 * DPA Response.
 * 
 * @author Michal Konopa
 */
public final class DPA_Response extends DPA_Message {
    
    // response code
    private final DPA_ResponseCode responseCode;
    
    // DPA Value
    private final short dpaValue;
    
    // response data
    private final short[] data;
            
    
    /**
     * Creates new DPA Response and initializes it according to specified parameters.
     * @param nadr NADR
     * @param pnum PNUM
     * @param pcmd PCMD
     * @param hwpid HWPID
     * @param responseCode response code
     * @param dpaValue DPA Value
     * @param data response's data
     */
    public DPA_Response(
            short[] nadr, short pnum, short pcmd, short[] hwpid,
            DPA_ResponseCode responseCode, short dpaValue, short[] data
    ) {
        super(nadr, pnum, pcmd, hwpid);
        this.responseCode = responseCode;
        this.dpaValue = dpaValue;
        this.data = data;
    }

    /**
     * @return the response code
     */
    public DPA_ResponseCode getResponseCode() {
        return responseCode;
    }

    /**
     * @return the DPA Value
     */
    public short getDpaValue() {
        return dpaValue;
    }

    /**
     * @return the data
     */
    public short[] getData() {
        return data;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" NADR: " + Arrays.toString(nadr) + NEW_LINE);
        strBuilder.append(" PNUM: " + pnum + NEW_LINE);
        strBuilder.append(" PCMD: " + pcmd + NEW_LINE);
        strBuilder.append(" HWP ID: " + Arrays.toString(hwpid) + NEW_LINE);
        strBuilder.append(" Response code: " + dpaValue + NEW_LINE);
        strBuilder.append(" DPA value: " + dpaValue + NEW_LINE);
        strBuilder.append(" Data: " + Arrays.toString(data) + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
}
