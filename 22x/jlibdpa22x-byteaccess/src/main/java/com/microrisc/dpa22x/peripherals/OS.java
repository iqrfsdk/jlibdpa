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
package com.microrisc.dpa22x.peripherals;

/**
 * OS standard peripheral.
 * 
 * @author Michal Konopa
 */
public final class OS {
    
    /** Peripheral number. */
    public static final int PNUM = 0x02;
    
    /** Commands. */
    public static enum Command {
        READ                            (0x00),
        RESET                           (0x01),
        READ_HWP_CONFIGURATION          (0x02),
        RUN_RFPGM                       (0x03),
        SLEEP                           (0x04),
        BATCH                           (0x05),
        SET_USEC                        (0x06),
        SET_MID                         (0x07),
        RESTART                         (0x08),
        WRITE_HWP_CONFIGURATION_BYTE    (0x09),
        LOAD_CODE                       (0x0A),
        WTITE_HWP_CONFIGURATION         (0x0F)
        ;
        
        private final int  command;
        
        private Command(int command) {
            this.command = command;
        }
        
        public short asByteValue() {
            return (short)command;
        }
    }
}
