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
 * Library internal error. 
 * 
 * @author Michal Konopa
 */
public class LibraryInternalError
extends AbstractProcessingError implements ProcessingError
{
    private static final ErrorType ERROR_TYPE = ErrorType.LIBRARY_INTERNAL; 
    
    
    public LibraryInternalError() {
        super(ERROR_TYPE, "");
    }
    
    public LibraryInternalError(String description) {
        super(ERROR_TYPE, description);
    }
    
    public LibraryInternalError(Exception exception) {
        super(ERROR_TYPE, "", exception);
    }
    
    public LibraryInternalError(String description, Exception exception) {
        super(ERROR_TYPE, description, exception);
    }
}
