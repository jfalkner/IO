/*
 * AnnotatedPeakList.java
 *
 * Created on August 14, 2006, 2:52 PM
 */

package org.proteomecommons.io;
import java.util.List;
import java.util.Collection;
import org.proteomecommons.jaf.Peptide;
/**
 * Implementing classes have some notion of the peptide that generated this sequence
 * @author Jarret
 */
public abstract class AnnotatedPeakList extends GenericPeakList{
    /**
     * What peptide this peak list claims to be
     */
    public abstract List<String> getPeptides();
    
    /**
     * A common name for the software, person, or other entity that made this annotation
     */
    public abstract String getAnnotationSource();
    
    /**
     * Implement getParent by using the silico version of the peak weight
     */
    
    public Peak getParent(){
        GenericPeak gp = new GenericPeak();
        String pep = getPeptides().get(0);
        try {
            double parent = new Peptide(pep).getMassWithCharge(1);
            gp.setMassOverCharge(parent);
            gp.setCharge(1);
            gp.setIntensity(100);
            return gp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
