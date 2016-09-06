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
 * DPA Message.
 * 
 * @author Michal Konopa
 */
public abstract class DPA_Message {
    
    // NADR
    protected final short[] nadr;
    
    // PNUM
    protected final short pnum;
    
    // PCMD
    protected final short pcmd;
    
    // HWPID
    protected final short[] hwpid;
    
    
    /**
     * Protected constructor. Creates new DPA Message and initializes it according
     * to specified parameters.
     * @param nadr NADR
     * @param pnum PNUM
     * @param pcmd PCMD
     * @param hwpid HWPID
     */
    protected DPA_Message(short[] nadr, short pnum, short pcmd, short[] hwpid) {
        this.nadr = nadr;
        this.pnum = pnum;
        this.pcmd = pcmd;
        this.hwpid = hwpid;
    }

    /**
     * @return the NADR
     */
    public short[] getNadr() {
        return nadr;
    }

    /**
     * @return the PNUM
     */
    public short getPnum() {
        return pnum;
    }

    /**
     * @return the PCMD
     */
    public short getPcmd() {
        return pcmd;
    }

    /**
     * @return the HWPID
     */
    public short[] getHwpid() {
        return hwpid;
    }
    
    
}
