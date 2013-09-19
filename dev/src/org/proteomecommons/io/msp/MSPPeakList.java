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
package org.proteomecommons.io.msp;

import java.util.*;
import org.proteomecommons.io.AnnotatedPeakList;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.Peak;

/**
 * A custom peak list that keeps both MSMS information and the presumed peptide sequence.
 * @author Jayson Falkner - jfalkner@umich.edu
 * @author Jarret Falkner - jar@cs.washington.edu
 */
public class MSPPeakList extends AnnotatedPeakList{
    private List<String> peptides;
    
    public MSPPeakList(Peak[] peaks, List<String> peptides){
        this.peptides = peptides;
        this.setPeaks(peaks);
    }
    
    public List<String> getPeptides(){
        return peptides;
    }

    public String getAnnotationSource() {
        return "MSP internal file annotation";
    }
    
    
}

