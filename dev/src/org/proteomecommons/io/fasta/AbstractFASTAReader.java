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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.proteomecommons.io.UnknownFileFormatException;
import org.proteomecommons.io.fasta.generic.GenericFASTAReaderFactory;
import org.proteomecommons.io.fasta.hupopsi.HUPOPSIFASTAReaderFactory;
import org.proteomecommons.io.fasta.ipi.IPIFASTAReaderFactory;
import org.proteomecommons.io.fasta.msdb.MSDBFASTAReaderFactory;
import org.proteomecommons.io.fasta.ncbi.NCBIFASTAReaderFactory;
import org.proteomecommons.io.fasta.swissprot.SwissProtFASTAReaderFactory;
import org.proteomecommons.io.fasta.uniref.UniRefFASTAReaderFactory;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public abstract class AbstractFASTAReader extends FASTAReader {
    // the name of this reader, usually the name of a file
    private String filename;
    
    // map of all readers
    private static LinkedHashMap readers = new LinkedHashMap();
    
    static {
        String fastaRegex = ".*.fasta|.*.FastA|.*.FASTA";
        // load the reader factories
        GenericFASTAReaderFactory gen = new GenericFASTAReaderFactory();
        setFASTAReader(gen, fastaRegex);
        HUPOPSIFASTAReaderFactory hupopsi = new HUPOPSIFASTAReaderFactory();
        setFASTAReader(hupopsi, fastaRegex);
        IPIFASTAReaderFactory ipi = new IPIFASTAReaderFactory();
        setFASTAReader(ipi, fastaRegex);
        MSDBFASTAReaderFactory msdb = new MSDBFASTAReaderFactory();
        setFASTAReader(msdb, fastaRegex);
        NCBIFASTAReaderFactory ncbi = new NCBIFASTAReaderFactory();
        setFASTAReader(ncbi, fastaRegex);
        SwissProtFASTAReaderFactory swissprot = new SwissProtFASTAReaderFactory();
        setFASTAReader(swissprot, fastaRegex);
        UniRefFASTAReaderFactory uniref = new UniRefFASTAReaderFactory();
        setFASTAReader(uniref, fastaRegex);
    }
    
    // helper method to dynamically load reader classes, this allows external libraries to be loaded without breaking things at compile time
    private static void loadReaderClass(String reader, String regex){
        try {
            FASTAReaderFactory plrf = (FASTAReaderFactory)Class.forName(reader).newInstance();
            setFASTAReader(plrf, regex);
        } catch (Exception e){
            System.err.println("Failed to load writer class "+reader+", skipping.");
        }
    }
    
    /**
     * Returns all the registered readers.
     * @return
     */
    public static FASTAReaderFactory[] getRegisteredReaders(){
        return (FASTAReaderFactory[])readers.values().toArray(new FASTAReaderFactory[0]);
    }
    
    /**
     * Public constructor, automatically
     *
     * @param in
     */
    public AbstractFASTAReader(String filename) {
        setName(filename);
    }
    
    /**
     * Helper method to register reader objects. Each factory is registered with
     * a regular expression. The regular expression is used to screen filenames
     * for matches that can be parsed by the reader.
     *
     * @param factory
     *            An instance of FASTAReaderFactory.
     * @param regex
     *            A regular expression that is used to match files to the
     *            appropriate factory.
     */
    public synchronized static void setFASTAReader(FASTAReaderFactory factory, String regex) {
        // treat the extension as a regular expression
        Pattern pattern = Pattern.compile(regex);
        readers.put(pattern, factory);
    }
    
    /**
     * Helper method to get an appropriate reader based on a filename.
     *
     * @param name
     */
    public synchronized static FASTAReader getFASTAReader(String filename) throws UnknownFileFormatException {
        // get the appropriate factory
        FASTAReaderFactory factory = (FASTAReaderFactory) getFASTAReaderFactory(filename);
        
        // make the reader
        FASTAReader reader = factory.newInstance(filename);
        
        // return a new FASTAReader instance
        return reader;
    }
    
    /**
     * Helper method to get an appropriate FASTAReaderFactory instance.
     *
     * @param name
     */
    public synchronized static FASTAReaderFactory getFASTAReaderFactory(String filename) throws UnknownFileFormatException {
        
        // check for the appropriate factory
        for (Iterator it = readers.keySet().iterator(); it.hasNext();) {
            // get the patter
            Pattern p = (Pattern) it.next();
            // always match against lowercase
            Matcher m = p.matcher(filename.toLowerCase());
            
            // if they match, return them
            if (m.matches()) {
                return (FASTAReaderFactory) readers.get(p);
            }
        }
        
        // get the appropriate factory
        throw new UnknownFileFormatException("Can't find a FASTAReaderFactory for "+filename);
    }
    
    /**
     * Sets the name of this FASTAReader. If you are parsing a file, and you
     * are using one of the helper methods of this class, the name is
     * automatically set to be the name of the file.
     *
     * @param name
     *            The name of this FASTAReader
     */
    public void setName(String name) {
        this.filename = name;
    }
    
    /**
     * A method to notify the FASTAReader instance that no more FASTAProtein
     * objects are going to be read. This gives the reader a change
     * to free up any unneeded resources that are allocated.
     *
     */
    public abstract void close();
    
    /**
     * Returns the name of this FASTAReader object. If this FASTAReader
     * was derived from a file, the name of the file is returned. For all other
     * cases the name is undefined.
     *
     * @return
     */
    public String getName() {
        return filename;
    }
    
    public abstract FASTAProtein getFASTAProtein();
}
