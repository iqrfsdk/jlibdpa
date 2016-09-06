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
 * Properties of IQRF DPA protocol.
 * According to IQRF DPA v2.27.
 * 
 * @author Michal Konopa
 */
public final class ProtocolProperties {
    
    /** NADR start position. */
    public static final int NADR_START = 0;
    
    /** NADR length. */
    public static final int NADR_LENGTH = 2;
    
    /** PNUM position. */
    public static final int PNUM_POS = 2;
    
    /** PCMD position. */
    public static final int PCMD_POS = 3;
    
    /** HWPID start position. */
    public static final int HWPID_START = 4;
    
    /** HWPID length. */
    public static final int HWPID_LENGTH = 2;
    
    /** Foursome length. */
    public static final int FOURSOME_LENGTH = HWPID_START + HWPID_LENGTH;
    
    /** PDATA start position. */
    public static final int PDATA_START_POS = FOURSOME_LENGTH;
    
    /** PDATA max. length. */
    public static final int PDATA_MAX_LENGTH = 56;
    
    
    /** Response code position. */
    public static final int RESPONSE_CODE_POS = FOURSOME_LENGTH;
    
    /** DPA Value position. */
    public static final int DPA_VALUE_POS = FOURSOME_LENGTH + 1;
    
    
    // CONFIRMATION 
    /** Hops position. */
    public static final int HOPS_POS = FOURSOME_LENGTH + 2;
    
    /** Timeslot length position. */
    public static final int TIMESLOT_LENGTH_POS = FOURSOME_LENGTH + 3;
    
    /** Hops response position. */
    public static final int HOPS_RESPONSE_POS = FOURSOME_LENGTH + 4;
    
    /** Length of confirmation. */
    public static final int CONFIRMATION_LENGTH = HOPS_RESPONSE_POS + 1;
    
    
    // RESPONSE
    /** Maximal length of response. */
    public static final int RESPONSE_MAX_LENGTH = PDATA_START_POS + PDATA_MAX_LENGTH;
    
    
    /**
     * NADR properties.
     */
    public static final class NADR_Properties {
        
        // suppresses default constructor for noninstantiability
        private NADR_Properties() {
            throw new AssertionError();
        }
        
        /** Starting position of NADR in packet.  */
        public static int START_POSITION = 0;
        
        /** Length (in bytes) of NADR. */
        public static int LENGTH = 2;
        
        
        /** IQMESH Coordinator address. */
        public static final int IQMESH_COORDINATOR_ADDRESS = 0x00;
        
        /** IQMESH Node addresses. */
        public static final int IQMESH_NODE_ADDRESS_MIN = 0x01;
        public static final int IQMESH_NODE_ADDRESS_MAX = 0xEF;

        /** Local ( over SPI ) device address. */
        public static final int LOCAL_DEVICE_ADDRESS = 0xFC;

        /** IQMESH Temporary address. */
        public static final int IQMESH_TEMPORARY_ADDRESS = 0xFE;

        /** IQMESH Broadcast address. */
        public static final int IQMESH_BROADCAST_ADDRESS = 0xFF;
        
        /**
         * Indicates, wheather the specified value of NADR is reserved.
         * @param nadr NADR value to check
         * @return {@code true} if {@code nadr} is reserved <br>
         *         {@code false} otherwise
         */
        public static boolean isReserved(int nadr) {
            return (
                    ( nadr >= 0xF0 ) && ( nadr <= 0xFB )
                    || ( nadr == 0xFD )
                    || ( nadr >= 0x100 ) && ( nadr <= 0xFFFF )
            );
        }
    }
    
    /** PNUM properties. */
    public static final class PNUM_Properties {
        
        // Suppresses default constructor for noninstantiability
        private PNUM_Properties() {
            throw new AssertionError();
        }
        
        /** Special number for device exploration. */
        public static final int DEVICE_EXPLORATION = 0xFF;
        
        /** Numbers of standard peripherals. */
        public static final int COORDINATOR =   0x00;
        public static final int NODE =          0x01;
        public static final int OS =            0x02;
        public static final int EEPROM =        0x03;
        public static final int EEEPROM =       0x04;
        public static final int RAM =           0x05;
        public static final int LEDR =          0x06;
        public static final int LEDG =          0x07;
        public static final int SPI =           0x08;
        public static final int IO =            0x09;
        public static final int THERMOMETER =   0x0A;
        public static final int PWM =           0x0B;
        public static final int UART =          0x0C;
        public static final int FRC =           0x0D;        
        
        /** User peripherals properties. */
        public static final int USER_PERIPHERAL_START =   0x20;
        public static final int USER_PERIPHERAL_END =     0x6F;
    }
    
    
    /**
     * Indicates, whether addressee of specified request is a local device or not.
     * 
     * @param request request to send
     * @return {@code true} if addressee of the request is a local device <br>
     *         {@code false} otherwise
     */
    public static boolean isAddresseeLocalDevice(short[] request) {
        return (
                ( request[NADR_START] == 0x00 )
                || 
                ( request[NADR_START] == NADR_Properties.LOCAL_DEVICE_ADDRESS )
        );
    }
    
    /**
     * Returns type of message of specified message comming from network.
     * 
     * @param message message from network
     * @return type of specified message
     */
    public static MessageType getMessageType(short[] message) {
        if ( message.length >= RESPONSE_CODE_POS ) {
            throw new IllegalArgumentException(
                    "Bad message length. "
                    + "Expected at least: " + RESPONSE_CODE_POS
                    + ", got: " + message.length
            );
        }
        
        short responseCodeVal = message[RESPONSE_CODE_POS];
        if ( responseCodeVal == DPA_ResponseCode.CONFIRMATION.getIntValue() ) {
            return MessageType.CONFIRMATION;
        }
        
        // check, if there is flag
        return MessageType.RESPONSE;
    }
    
    /**
     * Returns PNUM field of specified message.
     * 
     * @param message source message
     * @return PNUM field of specified message.
     */
    public static short getPeripheralNumber(short[] message) {
        return message[PNUM_POS];
    }
    
    /**
     * Returns PCMD field of specified message.
     * 
     * @param message source message
     * @return PCMD field of specified message.
     */
    public static short getCommand(short[] message) {
        return message[PCMD_POS];
    }
    
    /**
     * Returns PData field of specified message.
     * 
     * @param message source message
     * @return PData field of specified message.
     */
    public static short[] getData(short[] message) {
        short[] pData = new short[message.length - PDATA_START_POS];
        System.arraycopy(message, PDATA_START_POS, pData, 0, pData.length);
        return pData;
    }
}
