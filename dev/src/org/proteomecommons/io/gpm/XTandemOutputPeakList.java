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
package org.proteomecommons.io.gpm;

import java.util.*;
import org.proteomecommons.io.AnnotatedPeakList;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.Peak;

/**
 * A quartz peak list maintains a list of peptide sequences in addition to
 * the standard mz and inten peaks.
 * @author Jarret - jar@cs.washington.edu
 */
public class XTandemOutputPeakList extends AnnotatedPeakList{
    private List<String> peptides;
    
    public XTandemOutputPeakList(Peak[] peaks, List<String> peptides){
        this.peptides = peptides;
        this.setPeaks(peaks);
    }
    
    public List<String> getPeptides(){
        return peptides;
    }

    public String getAnnotationSource() {
        return "X!tandem internal file annotation";
    }
    
}
