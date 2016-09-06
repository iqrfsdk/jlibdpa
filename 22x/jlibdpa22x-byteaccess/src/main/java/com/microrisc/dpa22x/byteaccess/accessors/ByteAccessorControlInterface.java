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
package com.microrisc.dpa22x.byteaccess.accessors;

/**
 * Interface for controling of byte accessors. 
 * 
 * @author Michal Konopa
 */
public interface ByteAccessorControlInterface {
    
    /**
     * Starts byte accessor.
     * 
     * @throws com.microrisc.dpa22x.byteaccess.accessors.ByteAccessorException if
     *         some error occured during start
     */
    void start() throws ByteAccessorException;
    
    /**
     * Terminates byte accessor and releases used resources.
     */
    void terminateAndRelease();
}
