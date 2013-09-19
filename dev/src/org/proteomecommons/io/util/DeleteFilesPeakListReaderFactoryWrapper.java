package org.proteomecommons.io.util;

import java.io.File;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

/**
 * A helper class that will wrap any PeakListReaderFactory and have it produce classes that are automatically wrapped.
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class DeleteFilesPeakListReaderFactoryWrapper implements PeakListReaderFactory {
    private PeakListReaderFactory wrapped;
    private File[] toDelete;
    
    public DeleteFilesPeakListReaderFactoryWrapper(PeakListReaderFactory toWrap, File[] toDelete) {
        this.wrapped = toWrap;
        this.toDelete = toDelete;
    }
    
    public PeakListReader newInstance(String filename) {
        // get the wrapped PLR
        PeakListReader plr = wrapped.newInstance(filename);
        // wrap with a file deleting class
        DeleteFilesPeakListReaderWrapper plrw = new DeleteFilesPeakListReaderWrapper(plr, toDelete);
        // return the wrapped reader
        return plrw;
    }
        
}
