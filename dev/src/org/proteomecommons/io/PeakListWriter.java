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
package org.proteomecommons.io;

import java.io.IOException;

/**
 * An abstraction for writing peak lists. Multiple peak lists may be written and specialized meta-information may be passed by either sub-casting the writer instance or passing in custom implementation of the PeakList interface.
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public interface PeakListWriter {
    
    /**
     * Method for writing an entire peak list, all at once.
     *
     * @param peaklist
     * @throws IOException
     */
    public abstract void write(PeakList peaklist);
    
    /**
     * Finish writing the entired peak list file, including all sub-peak lists.
     * Do not use this method to delimit the end of a single peak list when more
     * peak lists are to occur after it (e.g. a LC MS run). Use multiple
     * invokations of startPeakList().
     *
     */
    public abstract void close();
}