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
package com.microrisc.dpa22x.byteaccess;

import com.microrisc.dpa22x.byteaccess.accessors.ByteAccessor;
import com.microrisc.dpa22x.byteaccess.accessors.ByteAccessorControlInterface;
import com.microrisc.dpa22x.byteaccess.accessors.ByteAccessorException;
import com.microrisc.dpa22x.byteaccess.accessors.ByteAccessorFactory;
import com.microrisc.dpa22x.byteaccess.accessors.ByteAccessorFactoryException;
import com.microrisc.dpa22x.byteaccess.accessors.StandardByteAccessorFactory;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Main object to use for access to functionality of the library.
 * 
 * @author Michal Konopa
 */
public final class JByteAccess {
    
    // byte accessor to use in user code
    private static ByteAccessor controlledByteAccessor;
    
    // creates and returns byte accessor
    private static ByteAccessor createByteAccessor(Configuration configuration) 
            throws JByteAccessException 
    {
        String byteAccessorFactoryClassName = configuration.getString("byteAccessor.factory.class", "");
        if ( byteAccessorFactoryClassName.isEmpty() ) {
            try {
                return new StandardByteAccessorFactory().getByteAccessor(configuration);
            } catch ( ByteAccessorFactoryException ex ) {
                throw new JByteAccessException(ex);
            }
        }
        
        try {
            Class byteAccessorFactoryClass = Class.forName(byteAccessorFactoryClassName);
            
            // each valid byte accessor factory must have parameter-less constructor!
            Constructor constructor = byteAccessorFactoryClass.getConstructor();
            ByteAccessorFactory byteAccessorFactory = (ByteAccessorFactory)constructor.newInstance();
            
            // creation of byte accessor
            ByteAccessor byteAccessor = byteAccessorFactory.getByteAccessor(configuration);
            if ( !(byteAccessor instanceof ByteAccessorControlInterface) ) {
                throw new JByteAccessException(
                        "Byte accessor must implement the " + ByteAccessorControlInterface.class
                        + " interface."
                );
            }
            return byteAccessor;
        } catch ( Exception ex ) {
            throw new JByteAccessException(ex);
        }
    }
    
    // terminates and releases byte accessor
    private static void terminateAndReleaseByteAccessor() {
        if ( controlledByteAccessor != null ) {
            ((ByteAccessorControlInterface) controlledByteAccessor).terminateAndRelease();
        }
    }
    
    /**
     * Initializes library according to settings in the specified configuration file.
     * 
     * @param fileName configuration file with settings for the library. The file must
     *        be the <b>property file</b> else exception is thrown.
     * @throws com.microrisc.dpa22x.byteaccess.JByteAccessException if some error occured
     *         during initialization process
     */
    public static void init(String fileName) throws JByteAccessException {
        Configuration configuration = null;
        
        try {
            configuration = new PropertiesConfiguration(fileName);
        } catch ( ConfigurationException ex ) {
            throw new JByteAccessException("Error in reading configuration file: " + ex);
        }
        
        controlledByteAccessor = createByteAccessor(configuration);
        
        // starting byte accessor
        try {
            ((ByteAccessorControlInterface) controlledByteAccessor).start();
        } catch ( ByteAccessorException ex ) {
            throw new JByteAccessException("Error while starting byte accessor: " +ex );
        }
    }
    
    /**
     * Returns byte accessor.
     * @return byte accessor
     */
    public static ByteAccessor getAccessor() {
        return controlledByteAccessor;
    }
    
    /**
     * Terminates the library and releases used resources.
     */
    public static void terminateAndRelease() { 
        terminateAndReleaseByteAccessor();
    }
}
