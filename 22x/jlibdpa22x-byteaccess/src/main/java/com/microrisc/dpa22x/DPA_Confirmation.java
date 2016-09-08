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
public final class DPA_Confirmation extends DPA_Message {
    
    // DPA Value
    private final short dpaValue;
    
    // hops
    private final short hops;
    
    // timeslot length
    private final short timeslotLength;
    
    // number of hops used to deliver the DPA response from the addressed node 
    // back to the coordinator
    private final short hopsResponse;
    
    
    /**
     * Creates new object of DPA Confirmation and initializes it according to
     * specified parameters.
     * 
     * @param nadr NADR
     * @param pnum PNUM
     * @param pcmd PCMD
     * @param hwpid HWPID
     * @param dpaValue DPA value
     * @param hops hops
     * @param timeslotLength timelost length
     * @param hopsResponse hops response
     */
    public DPA_Confirmation(
            short[] nadr, short pnum, short pcmd, short[] hwpid,
            short dpaValue, short hops, short timeslotLength, short hopsResponse
    ) {
        super(nadr, pnum, pcmd, hwpid);
        this.dpaValue = dpaValue;
        this.hops = hops;
        this.timeslotLength = timeslotLength;
        this.hopsResponse = hopsResponse;
    }

    /**
     * @return the DPA value
     */
    public short getDpaValue() {
        return dpaValue;
    }

    /**
     * @return the hops
     */
    public short getHops() {
        return hops;
    }

    /**
     * @return the timeslot length
     */
    public short getTimeslotLength() {
        return timeslotLength;
    }

    /**
     * @return the hops response
     */
    public short getHopsResponse() {
        return hopsResponse;
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
        strBuilder.append(" DPA value: " + dpaValue + NEW_LINE);
        strBuilder.append(" Hops: " + hops + NEW_LINE);
        strBuilder.append(" Timeslot length: " + timeslotLength + NEW_LINE);
        strBuilder.append(" Hops response: " + hopsResponse + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
}
