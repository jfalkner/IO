/*
 * DecoyDatabaseMaker.java
 *
 * Created on September 28, 2006, 12:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.proteomecommons.io.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import org.proteomecommons.io.ReversedProteinReader;
import org.proteomecommons.io.fasta.generic.GenericFASTAReader;
import org.proteomecommons.jaf.Protein;

/**
 *
 * @author Jayson
 */
public class DecoyDatabaseMaker {
    
    public static void main(String[] args) throws Exception {
        File dir = new File("C:/Documents and Settings/Jayson/Desktop/databases");
//        File dir = new File("C:/Documents and Settings/Jayson/Desktop/databases-example");
        File[] files = dir.listFiles();
        for (File f : files) {
            // read in the database
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bis = new BufferedInputStream(fis);
            GenericFASTAReader fpr = new GenericFASTAReader(bis);
            ReversedProteinReader rpr = new ReversedProteinReader(fpr);
            // write out the results
            File out = new File(f.getParentFile(), f.getName()+".reverse");
            System.out.println("Making "+out.getName());
            FileWriter fos = new FileWriter(out);
            BufferedWriter bos = new BufferedWriter(fos);
            int sequences = 0;
            for (Protein p = rpr.next();p!= null;p=rpr.next()) {
                // write out the protein
                bos.write(new String(">"+p.getName()+"\n"));
                bos.write(new String(p.getSequence()+"\n"));
                sequences++;
            }
            System.out.println("Finished: "+sequences);
            bos.flush();
            fos.flush();
            bos.close();
            fos.close();
            bis.close();
            fis.close();
        }
    }
}
