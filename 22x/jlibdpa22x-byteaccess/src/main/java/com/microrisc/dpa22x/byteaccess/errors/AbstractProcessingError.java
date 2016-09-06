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
 * Base class of a request processing errors.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractProcessingError implements ProcessingError {
    
    // error type
    protected ErrorType type;
    
    // description
    protected String description;
    
    // information about potential exception
    protected Exception exception;
    
    
    /**
     * Protected constructor. Creates new object of a processing error and sets
     * error type and description according to the specified ones.
     * @param errorType error type
     * @param description description
     */
    protected AbstractProcessingError(ErrorType errorType, String description) {
        this.type = errorType;
        this.description = description;
        this.exception = null;
    }
    
    /**
     * Protected constructor. Creates new object of a processing error and sets
     * error type, description and exception according to the specified ones.
     * @param errorType error type
     * @param description description
     * @param exception exception to set
     */
    protected AbstractProcessingError(ErrorType errorType, String description, Exception exception) {
        this.type = errorType;
        this.description = description;
        this.exception = exception;
    }

    /**
     * @return the type
     */
    @Override
    public ErrorType getType() {
        return type;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }
    
}
