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
 * FRC standard periperal.
 * 
 * @author Michal Konopa
 */
public final class FRC {
    
    /** Peripheral number. */
    public static final int PNUM = 0x0D;
    
    /** Commands. */
    public static enum Command {
        SEND            (0x00),
        EXTRA_RESULT    (0x01),
        SEND_SELECTIVE  (0x02),
        SET_FRC_PARAMS  (0x03);
        
        
        private final int  command;
        
        private Command(int command) {
            this.command = command;
        }
        
        public short asByteValue() {
            return (short)command;
        }
    }
}
