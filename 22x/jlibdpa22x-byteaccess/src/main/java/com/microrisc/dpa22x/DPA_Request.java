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

/**
 * DPA Request.
 * 
 * @author Michal Konopa
 */
public final class DPA_Request extends DPA_Message {
    
    // request's data
    private final short[] data;
    
    
    /**
     * Creates new DPA Request and initializes it according to specified parameters.
     * @param nadr NADR
     * @param pnum PNUM
     * @param pcmd PCMD
     * @param hwpid HWPID
     * @param data request's data
     */
    public DPA_Request(short[] nadr, short pnum, short pcmd, short[] hwpid, short[] data) {
        super(nadr, pnum, pcmd, hwpid);
        this.data = data;
    }

    /**
     * @return the data
     */
    public short[] getData() {
        return data;
    }
    
}
