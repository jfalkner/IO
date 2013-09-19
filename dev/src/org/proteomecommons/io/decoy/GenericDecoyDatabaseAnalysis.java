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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class GenericDecoyDatabaseAnalysis implements DecoyDatabaseAnalysis {
    private TreeSet<ScoredPeptide> peptides = new TreeSet<ScoredPeptide>();
    private TreeSet<ScoredPeptide> decoyPeptides = new TreeSet<ScoredPeptide>();
    
    public void print(double confidence) {
        PrintWriter out = new PrintWriter(System.out);
        try {
            print(confidence, out);
        } catch (IOException e) {
            throw new RuntimeException("Can't handle IO!", e);
        }
    }
    
    public void print(double confidence, Writer out) throws IOException {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        out.write("*** Peptides at "+nf.format(100*confidence)+"% confidence ***\n");
        ArrayList<ScoredPeptide> ps = getPeptides(confidence);
        out.write("Peptides: "+ps.size()+"\n");
        for (ScoredPeptide sp : ps) {
//            System.out.println("Found: "+sp.getScore()+" "+sp.getPeptide());
//            
            out.write(Double.toString(sp.getScore())+"\t"+sp.getPeptide().toString()+"\t"+sp.getPeakListIdentifier()+"\n");
        }
        ArrayList<ScoredPeptide> dps = getDecoyPeptides(confidence);
        out.write("\nDecoy Peptides: "+dps.size()+"\n");
        for (ScoredPeptide sp : dps) {
            out.write(Double.toString(sp.getScore())+"\t"+sp.getPeptide().toString()+"\t"+sp.getPeakListIdentifier()+"\n");
        }
        out.flush();
    }
    
    public ArrayList<ScoredPeptide> getPeptides(double confidence) {
        // calc the appropriate score cutoff for the confidence
        double cutoffscore = calculateCutoff(confidence);
        
        // collect the peptides that scored better than this
        ArrayList<ScoredPeptide> matches = new ArrayList();
        for (ScoredPeptide sp : peptides) {
            if (sp.getScore() >= cutoffscore) {
                matches.add(sp);
            }
        }
        // return the matches
        return matches;
    }
    
    private double calculateCutoff(final double confidence) {
        // the false-positive ratios
        ArrayList<FalsePositiveRatioAtScore> fprs = new ArrayList();
        
        // calculate all of the false positives
        ScoredPeptide[] trueHits = (ScoredPeptide[])getPeptides().toArray(new ScoredPeptide[0]);
        for (int i=0;i<trueHits.length;i++) {
            ScoredPeptide fsp = trueHits[i];
            // true hit count
            double trueHitCount = i+1;
            double falseHitCount = 0;
            // check how many it has
            ScoredPeptide[] falseHits = (ScoredPeptide[])getDecoyPeptides().toArray(new ScoredPeptide[0]);
            for (int j=0;j<falseHits.length;j++) {
                if (fsp.getScore() < falseHits[j].getScore()) {
                    falseHitCount = j;
                }
            }
            // add to the mix
            FalsePositiveRatioAtScore fpr = new FalsePositiveRatioAtScore();
            fpr.setDecoyPeptideCount(falseHitCount);
            fpr.setPeptideCount(trueHitCount);
            fpr.setScore(fsp.getScore());
            // add the ratios
            fprs.add(fpr);
        }
        
        // sort the fpr's'
        Collections.sort(fprs);
        double cutoffscore = Double.MAX_VALUE;
        for (FalsePositiveRatioAtScore fpr : fprs) {
            if (fpr.getConfidence() <= confidence) {
                return fpr.getScore();
            }
        }
        return 0;
    }
    
    public ArrayList<ScoredPeptide> getDecoyPeptides(double confidence) {
        // calc the appropriate score cutoff for the confidence
        double cutoffscore = calculateCutoff(confidence);
        
        // collect the decoy peptides that scored better than this
        ArrayList<ScoredPeptide> matches = new ArrayList();
        for (ScoredPeptide sp : decoyPeptides) {
            if (sp.getScore() >= cutoffscore) {
                matches.add(sp);
            }
        }
        // return the matches
        return matches;
    }
    
    public TreeSet<ScoredPeptide> getPeptides() {
        return peptides;
    }
    
    public void setPeptides(TreeSet<ScoredPeptide> peptides) {
        this.peptides = peptides;
    }
    
    public TreeSet<ScoredPeptide> getDecoyPeptides() {
        return decoyPeptides;
    }
    
    public void setDecoyPeptides(TreeSet<ScoredPeptide> decoyPeptides) {
        this.decoyPeptides = decoyPeptides;
    }
}

class FalsePositiveRatioAtScore implements Comparable {
    private double score = GenericScoredPeptide.UNKNOWN_SCORE;
    private double peptideCount = 0;
    private double decoyPeptideCount = 1;
    
    public double getConfidence() {
        return getPeptideCount()/(getPeptideCount()+getDecoyPeptideCount()*2);
    }
    
    public int compareTo(Object o) {
        FalsePositiveRatioAtScore fpr = (FalsePositiveRatioAtScore)o;
        
        double r2 = fpr.getPeptideCount()/fpr.getDecoyPeptideCount();
        double r1 = getPeptideCount()/getDecoyPeptideCount();
        
        if (r1 > r2) {
            return -1;
        }
        if (r1 < r2) {
            return 1;
        }
        
        // if they are equal, compare trues
        if (getPeptideCount() > fpr.getPeptideCount()) {
            return -1;
        }
        if (getPeptideCount() < fpr.getPeptideCount()) {
            return 1;
        }
        return 0;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public double getPeptideCount() {
        return peptideCount;
    }
    
    public void setPeptideCount(double peptideCount) {
        this.peptideCount = peptideCount;
    }
    
    public double getDecoyPeptideCount() {
        return decoyPeptideCount;
    }
    
    public void setDecoyPeptideCount(double decoyPeptideCount) {
        this.decoyPeptideCount = decoyPeptideCount;
    }
}
