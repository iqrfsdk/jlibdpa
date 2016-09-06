/*
 * Copyright 2016 Fs.
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
package com.microrisc.dpa22x.byteaccess.errors;

/**
 * Internal network error. 
 * 
 * @author Michal Konopa
 */
public final class NetworkInternalError
extends AbstractProcessingError implements ProcessingError 
{
    private static final ErrorType ERROR_TYPE = ErrorType.NETWORK_INTERNAL; 
    
    
    public NetworkInternalError() {
        super(ERROR_TYPE, "");
    }
    
    public NetworkInternalError(String description) {
        super(ERROR_TYPE, description);
    }
    
    public NetworkInternalError(Exception exception) {
        super(ERROR_TYPE, "", exception);
    }
    
    public NetworkInternalError(String description, Exception exception) {
        super(ERROR_TYPE, description, exception);
    }
}
