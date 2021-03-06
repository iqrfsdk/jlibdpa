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
 * LEDR standard periperal.
 * 
 * @author Michal Konopa
 */
public final class LEDR {
    
    /** Peripheral number. */
    public static final int PNUM = 0x06;
    
    /** Commands. */
    public static enum Command {
        OFF         (0x00),
        ON          (0x01),
        GET         (0x02),
        PULSE       (0x03);
        
        
        private final int  command;
        
        private Command(int command) {
            this.command = command;
        }
        
        public short asByteValue() {
            return (short)command;
        }
    }
}
