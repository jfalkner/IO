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
package org.proteomecommons.io.decoy;

import org.proteomecommons.jaf.Peptide;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class GenericScoredPeptide implements ScoredPeptide {
    public static final double UNKNOWN_SCORE = Double.MIN_VALUE;
    
    // the peptide
    private Peptide peptide = null;
    // the score
    private double score = GenericScoredPeptide.UNKNOWN_SCORE;
    // the peak list's identifier
    private String peakListIdentifier;
    
    public Peptide getPeptide() {
        return peptide;
    }
    
    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }

    public int compareTo(Object o) {
        ScoredPeptide sp = (ScoredPeptide)o;
        return Double.valueOf(sp.getScore()).compareTo(Double.valueOf(getScore()));
    }

    public String getPeakListIdentifier() {
        return peakListIdentifier;
    }

    public void setPeakListIdentifier(String peakListIdentifier) {
        this.peakListIdentifier = peakListIdentifier;
    }

    public boolean equals(Object obj) {
        ScoredPeptide sp = (ScoredPeptide)obj;
        String a = this.getPeptide().toString()+this.getPeakListIdentifier();
        String b = sp.getPeptide().toString()+sp.getPeakListIdentifier();
        return a.equals(b);
    }

    public int hashCode() {
        return new String(getPeptide().toString()+getPeakListIdentifier()).hashCode();
    }
}
