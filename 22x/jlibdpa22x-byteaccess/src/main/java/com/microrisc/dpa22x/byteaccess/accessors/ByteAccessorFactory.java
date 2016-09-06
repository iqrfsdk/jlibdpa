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

import org.apache.commons.configuration.Configuration;

/**
 * Byte Accessor factory.
 * 
 * @author Michal Konopa
 */
public interface ByteAccessorFactory {
    
    /**
     * Returns byte accessor instance - according to specified configuration.
     * 
     * @param configuration byte accessor's configuration
     * @return byte accessor
     * @throws com.microrisc.dpa22x.byteaccess.accessors.ByteAccessorFactoryException
     *         if some error occurs during returning of byte accessor
     */
    ByteAccessor getByteAccessor(Configuration configuration) throws ByteAccessorFactoryException;
}
