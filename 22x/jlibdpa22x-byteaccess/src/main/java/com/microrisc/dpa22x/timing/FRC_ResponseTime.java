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
package com.microrisc.dpa22x.timing;

/**
 *
 * @author Michal Konopa
 */
public enum FRC_ResponseTime {
    
    /** Time 40ms. */
    TIME_40_MS(40, 0b00000000),
    
    /** Time 320ms. */
    TIME_320_MS(320, 0b00010000),
    
    /** Time 640ms. */
    TIME_640_MS(640, 0b00100000),
    
    /** Time 1280ms. */
    TIME_1280_MS(1280, 0b00110000),
    
    /** Time 2560ms. */
    TIME_2560_MS(2560, 0b01000000),
    
    /** Time 5120ms. */
    TIME_5120_MS(5120, 0b01010000),
    
    /** Time 10240ms. */
    TIME_10240_MS(10240, 0b01100000),
    
    /** Time 20480ms. */
    TIME_20480_MS(20480, 0b01110000);
    
    
    private final int time;
    private final int id;
        
    private FRC_ResponseTime(int time, int id){
        this.time = time;
        this.id = id;
    }

    public int getTimeAsInt(){
        return time;
    }

    public int getId(){
       return id;
    }
    
}
