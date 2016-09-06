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
package com.microrisc.dpa22x.peripherals;

/**
 * Coordinator standard periperal.
 * 
 * @author Michal Konopa
 */
public class Coordinator {
    
    /** Peripheral number. */
    public static final int PNUM = 0x00;
    
    /** Commands. */
    public static enum Command {
        GET_ADDRESSING_INFO                 (0x00),
        GET_DISCOVERED_NODES                (0x01),
        GET_BONDED_NODES                    (0x02),
        CLEAR_ALL_BONDS                     (0x03),
        BOND_NODE                           (0x04),
        REMOVE_BONDED_NODE                  (0x05),
        REBOND_NODE                         (0x06),
        RUN_DISCOVERY                       (0x07),
        SET_DPA_PARAM                       (0x08),
        SET_HOPS                            (0x09),
        DISCOVERY_DATA                      (0x0A),
        BACKUP                              (0x0B),
        RESTORE                             (0x0C),
        AUTHORIZE_BOND                      (0x0D),
        BRIGDE                              (0x0E),
        ENABLE_REMOTE_BONDING               (0x11),
        READ_REMOTELY_BONDED_MODULE_ID      (0x0F),
        CLEAR_REMOTELY_BONDED_MODULE_ID     (0x10);
        
        
        private final int command;
        
        private Command(int command) {
            this.command = command;
        }
        
        public short asByteValue() {
            return (short)command;
        }
    }
}
