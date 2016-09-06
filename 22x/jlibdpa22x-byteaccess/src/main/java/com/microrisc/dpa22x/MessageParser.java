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
 * DPA message parser.
 * 
 * @author Michal Konopa 
 */
public final class MessageParser {
    
    // parses and returns response code
    private static DPA_ResponseCode parseResponseCode(short respCodeVal) 
            throws MessageParserException 
    {
        for ( DPA_ResponseCode respCode : DPA_ResponseCode.values() ) {
            if ( respCode.getIntValue() == respCodeVal ) {
                return respCode;
            }
        }
        
        throw new MessageParserException("Uknown value of respose code: " + respCodeVal);
    }
    
    // check if the most significant bit of specified byte is flagged
    private static boolean isMostSignificantBitFlagged(short value) {
        return ( (value & 0b10000000) == 0b10000000 );
    }
    
    // parses specified confirmation data a returns corresponding DPA Confirmation object
    private static DPA_Confirmation parseConfirmation(short[] confirmData) 
            throws MessageParserException 
    {
        if ( confirmData.length != ProtocolProperties.CONFIRMATION_LENGTH ) {
            throw new MessageParserException(
                    "Invalid confirmation's data length."
                    + "Expected: " + ProtocolProperties.CONFIRMATION_LENGTH
                    + " Got: " + confirmData.length
            );
        }
        
        if ( confirmData[ProtocolProperties.RESPONSE_CODE_POS] != DPA_ResponseCode.CONFIRMATION.getIntValue() ) {
            throw new MessageParserException(
                    "Invalid status confirmation code."
                    + "Expected: " + DPA_ResponseCode.CONFIRMATION.getIntValue()
                    + " Got: " + confirmData[ProtocolProperties.RESPONSE_CODE_POS]
            );
        }
        
        // address
        short[] addr = new short[ProtocolProperties.NADR_LENGTH];
        System.arraycopy(confirmData, ProtocolProperties.NADR_START, addr, 0, ProtocolProperties.NADR_LENGTH);
        
        // hwpid
        short[] hwpid = new short[ProtocolProperties.HWPID_LENGTH];
        System.arraycopy(confirmData, ProtocolProperties.HWPID_START, addr, 0, ProtocolProperties.HWPID_LENGTH);
        
        return new DPA_Confirmation(
                addr,
                confirmData[ProtocolProperties.PNUM_POS],
                confirmData[ProtocolProperties.PCMD_POS],
                hwpid,
                confirmData[ProtocolProperties.DPA_VALUE_POS], 
                confirmData[ProtocolProperties.HOPS_POS], 
                confirmData[ProtocolProperties.TIMESLOT_LENGTH_POS],
                confirmData[ProtocolProperties.HOPS_RESPONSE_POS]
        );
    }
    
    // parses specified response data a returns corresponding DPA Response object
    private static DPA_Response parseResponse(short[] responseData) 
            throws MessageParserException 
    {
        if ( 
            responseData.length < (ProtocolProperties.DPA_VALUE_POS + 1) 
            || responseData.length > ProtocolProperties.RESPONSE_MAX_LENGTH
        ) {
            throw new MessageParserException(
                    "Invalid response's data length."
                    + "Response length must be within the interval of: "
                            + "<"
                            + (ProtocolProperties.DPA_VALUE_POS + 1)
                            + ", "
                            + ProtocolProperties.RESPONSE_MAX_LENGTH
                            + ">"
                    + " Got: " + responseData.length
            );
        }
        
        // address
        short[] addr = new short[ProtocolProperties.NADR_LENGTH];
        System.arraycopy(responseData, ProtocolProperties.NADR_START, addr, 0, ProtocolProperties.NADR_LENGTH);
        
        // hwpid
        short[] hwpid = new short[ProtocolProperties.HWPID_LENGTH];
        System.arraycopy(responseData, ProtocolProperties.HWPID_START, addr, 0, ProtocolProperties.HWPID_LENGTH);
        
        // check, if the PCMD most significat bit is flagged
        if ( !isMostSignificantBitFlagged(responseData[ProtocolProperties.PCMD_POS]) ) {
            throw new MessageParserException("Most significant bit of PCMD must be flagged.");
        }
        
        // response code
        DPA_ResponseCode responseCode = parseResponseCode(responseData[ProtocolProperties.RESPONSE_CODE_POS]);
        if ( responseCode == DPA_ResponseCode.CONFIRMATION ) {
            throw new MessageParserException(
                    "Invalid response code: " + DPA_ResponseCode.CONFIRMATION.getIntValue()
            );
        }
        
        // response data
        short[] pData = null;
        if ( responseCode != DPA_ResponseCode.NO_ERROR ) {
            pData = new short[0];
        } else {
            pData = new short[responseData.length - ProtocolProperties.DPA_VALUE_POS - 1];
            System.arraycopy(responseData, ProtocolProperties.PDATA_START_POS, pData, 0, pData.length);
        }
        
        return new DPA_Response(
                addr,
                responseData[ProtocolProperties.PNUM_POS],
                responseData[ProtocolProperties.PCMD_POS],
                hwpid,
                responseCode,
                responseData[ProtocolProperties.DPA_VALUE_POS], 
                pData
        );
    }
    
    /**
     * Parses specified message's data and returns corresponding DPA message object.
     * 
     * @param msgData message data to parse
     * @return DPA message, which corresponds to {@code msgData}
     * @throws com.microrisc.jiqrfdpa.v22x.MessageParserException 
     *         if some error occured during parsing
     */
    public static DPA_Message parse(short[] msgData) throws MessageParserException {
        // type of the incomming message
        MessageType msgType = ProtocolProperties.getMessageType(msgData);
        
        switch ( msgType ) {
            case CONFIRMATION:
                return parseConfirmation(msgData);
            case RESPONSE:
                return parseResponse(msgData);
            default:
                throw new MessageParserException("Unsupported message type: " + msgData);
        }
    }
}
