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
package com.microrisc.dpa22x.byteaccess;

import com.microrisc.dpa22x.byteaccess.errors.ProcessingError;

/**
 * Processing information.
 * 
 * @author Michal Konopa
 */
public final class ProcessingInfo {
    
    // processing error
    private final ProcessingError procError;
            
    
    /**
     * Creates new object of processing info with no defined processing error.
     */
    public ProcessingInfo() {
        this.procError = null;
    }
    
    /**
     * Creates new object of processing info containing specified error.
     * @param procError processing error
     */
    public ProcessingInfo(ProcessingError procError) {
        this.procError = procError;
    }
    
    /**
     * Returns processing error.
     * If no processing error is present, {@code null} is returned.
     * @return processing error <br>
     *         {@code null}, if no processing error is present
     */
    public ProcessingError getProcesssingError() {
        return procError;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "processing error=" + procError +
                " }");
    }
}
