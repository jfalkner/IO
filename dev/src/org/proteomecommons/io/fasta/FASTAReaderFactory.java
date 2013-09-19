/*
 *    Copyright 2005 The Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.proteomecommons.io.fasta;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public interface FASTAReaderFactory {
    /**
     * Requests a new FASTAReader instance using the data from the
     * given file.
     *
     * @param is
     * @return
     */
    public FASTAReader newInstance(String filename);
    
    /**
     * Returns the extension expected for this type of FASTA file.
     * @return
     */
    public String getFileExtension();
    
    /**
     * Return's the common name associated with this file format.
     * @return
     */
    public String getName();
}
