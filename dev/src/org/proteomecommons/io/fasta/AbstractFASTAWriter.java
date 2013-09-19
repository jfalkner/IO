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

import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.proteomecommons.io.fasta.generic.*;
import org.proteomecommons.io.fasta.hupopsi.HUPOPSIFASTAWriterFactory;
import org.proteomecommons.io.fasta.ipi.IPIFASTAWriterFactory;
import org.proteomecommons.io.fasta.msdb.MSDBFASTAWriterFactory;
import org.proteomecommons.io.fasta.ncbi.NCBIFASTAWriterFactory;
import org.proteomecommons.io.fasta.swissprot.SwissProtFASTAWriterFactory;
import org.proteomecommons.io.fasta.uniref.UniRefFASTAWriterFactory;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public abstract class AbstractFASTAWriter implements FASTAWriter {
    
    // map of all readers
    private static LinkedHashMap writers = new LinkedHashMap();
    
    // the output writer
    private FileWriter fileWriter = null;
    
    // the name of the output file
    private String fileName = null;
    
    static {
        // regex for FASTA Files
        String fastaRegex = ".*.fasta|.*.FastA|.*.FASTA";
        
        // load the writer factories
        GenericFASTAWriterFactory generic = new GenericFASTAWriterFactory();
        setFASTAWriter(generic, fastaRegex);
        HUPOPSIFASTAWriterFactory hupopsi = new HUPOPSIFASTAWriterFactory();
        setFASTAWriter(hupopsi, fastaRegex);
        /*
        IPIFASTAWriterFactory ipi = new IPIFASTAWriterFactory();
        setFASTAWriter(ipi, fastaRegex);
        MSDBFASTAWriterFactory msdb = new MSDBFASTAWriterFactory();
        setFASTAWriter(msdb, fastaRegex);
        NCBIFASTAWriterFactory ncbi = new NCBIFASTAWriterFactory();
        setFASTAWriter(ncbi, fastaRegex);
        SwissProtFASTAWriterFactory swissprot = new SwissProtFASTAWriterFactory();
        setFASTAWriter(swissprot, fastaRegex);
        UniRefFASTAWriterFactory uniref = new UniRefFASTAWriterFactory();
        setFASTAWriter(uniref, fastaRegex);
        */
    }
    
    // helper method to dynamically load writer classes, this allows external libraries to be loaded without breaking things at compile time
    private static void loadWriterClass(String writer, String regex){
        try {
            FASTAWriterFactory factory = (FASTAWriterFactory)Class.forName(writer).newInstance();
            setFASTAWriter(factory, regex);
        } catch (Exception e){
            System.err.println("Failed to load writer class "+writer+", skipping.");
        }
    }
    
    /**
     * Returns all the registered writers.
     * @return
     */
    public static FASTAWriterFactory[] getRegisteredWriters(){
        return (FASTAWriterFactory[])writers.values().toArray(new FASTAWriterFactory[0]);
    }
    
    /**
     * Helper method to register writer objects. Each factory is registered with
     * a regular expression. The regular expression is used to screen filenames
     * for matches that can be parsed by the reader.
     *
     * @param factory
     *            An instance of FASTAWriterFactory.
     * @param regex
     *            A regular expression that is used to match files to the
     *            appropriate factory.
     */
    public synchronized static void setFASTAWriter(FASTAWriterFactory factory, String regex) {
        // treat the extension as a regular expression
        Pattern pattern = Pattern.compile(regex);
        writers.put(pattern, factory);
    }
    
    /**
     * Helper method to get an appropriate reader based on a filename.
     *
     * @param name
     */
    public synchronized static FASTAWriter getFASTAWriter(String filename) {
        // get the appropriate factory
        FASTAWriterFactory factory = (FASTAWriterFactory) getFASTAWriterFactory(filename);
        // return null if none exist
        if (factory == null) {
            return null;
        }
        
        // make the reader
        FASTAWriter writer = factory.newInstance(filename);
        
        // return a new FASTAReader instance
        return writer;
    }
    
    /**
     * Helper method to get an appropriate FASTAWriterFactory instance.
     *
     * @param name
     */
    public synchronized static FASTAWriterFactory getFASTAWriterFactory(String filename) {
        
        // check for the appropriate factory
        for (Iterator it = writers.keySet().iterator(); it.hasNext();) {
            Pattern p = (Pattern) it.next();
            // always match against lower case
            Matcher m = p.matcher(filename.toLowerCase());
            if (m.matches()) {
                return (FASTAWriterFactory) writers.get(p);
            }
        }
        
        // get the appropriate factory
        throw new RuntimeException("Can't find a create the fasta file "+filename+". Please try a different format.");
    }
    
    /**
     * Public constructor, automatically sets up buffered IO.
     *
     * @param filename
     */
    public AbstractFASTAWriter(String fileName) {
        try {
            this.fileName = fileName;
            fileWriter = new FileWriter(fileName, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public FileWriter getFileWriter() {
        return fileWriter;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    /**
     * @param FASTAReader
     */
    public void beginReader(FASTAReader reader) {}
    
    /**
     * Finish up
     */
    public void close() {
        // close the old stream
        try {
            getFileWriter().flush();
            getFileWriter().close();
        } catch (Exception e) {
            // do nothing
        }
    }
    
}
