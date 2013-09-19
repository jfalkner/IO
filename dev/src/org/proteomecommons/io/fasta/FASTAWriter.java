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
 * An abstraction for writing fasta files.
 * @author James "Augie" Hill - augie@828productions.com
 */
public interface FASTAWriter {
    
    /**
     * @param FASTAReader
     */
    public abstract void beginReader(FASTAReader reader);
    
    /**
     * @param FASTAProtein
     * @throws IOException
     */
    public abstract void write(FASTAProtein protein);
    
    /**
     * @param FASTAProtein
     * @param String
     * @throws IOException
     */
    public abstract void writeReverse(FASTAProtein protein, String signifyReverse);
    
    /**
     * Finish writing the entire FASTA file.
     */
    public abstract void close();
}
