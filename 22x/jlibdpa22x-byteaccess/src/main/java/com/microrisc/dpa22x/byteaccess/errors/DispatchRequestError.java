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
 * Dispatch request error.
 * 
 * @author Fs
 */
public final class DispatchRequestError 
extends AbstractProcessingError implements ProcessingError 
{
    private static final ErrorType ERROR_TYPE = ErrorType.DISPATCH_REQUEST; 
    
    
    public DispatchRequestError() {
        super(ERROR_TYPE, "");
    }
    
    public DispatchRequestError(String description) {
        super(ERROR_TYPE, description);
    }
    
    public DispatchRequestError(Exception exception) {
        super(ERROR_TYPE, "", exception);
    }
    
    public DispatchRequestError(String description, Exception exception) {
        super(ERROR_TYPE, description, exception);
    }
}
