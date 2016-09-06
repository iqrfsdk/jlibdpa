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
 * DPA response codes - according to IQRF DPA, version v2.27.
 * 
 * 
 * @author Michal Konopa
 */
public enum DPA_ResponseCode {
    
    /** No error. */ 
    NO_ERROR                            (0),
    
    /** General fail. */
    ERROR_FAIL                          (1),
    
    /** Incorrect PCMD. */
    ERROR_PCMD                          (2),
    
    /** Incorrect PNUM or PCMD. */
    ERROR_PNUM                          (3),
    
    /** Incorrect Address. */
    ERROR_ADDR                          (4),
    
    /** Incorrect Data length. */
    ERROR_DATA_LEN                      (5),
    
    /** Incorrect data. */
    ERROR_DATA                          (6),
    
    /** Incorrect HW Profile ID used. */
    ERROR_HWPROFILE                     (7),
    
    /** Incorrect NADR. */
    ERROR_NADR                          (8),
    
    /** Data from interface consumed by Custom DPA Handler. */
    ERROR_IFACE_CUSTOM_HANDLER          (9),
    
    /** Custom DPA Handler is missing. */
    ERROR_MISSING_CUSTOM_DPA_HANDLER    (10),
    
    /** Beginning of the user code error interval. */
    ERROR_USER_FROM                     (0x80),
    
    /** End of the user error code interval. */
    ERROR_USER_TO                       (0xFE),
    
    /** Error code used to mark confirmation. */
    CONFIRMATION                        (0xFF)
    ;
    
    
    /** response code */
    private final int code;
    
    private DPA_ResponseCode(int code) {
        this.code = code;
    }
    
    /**
     * Returns integer value of response code.
     * @return integer value of response code.
     */
    public int getIntValue() {
        return code;
    }
    
}
