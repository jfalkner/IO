package org.proteomecommons.io.mzxml.v2_1;

/**
 * Encapsulates data about a parentFile attribute in an MsRun.
 * @author Jarret
 *
 */
public class ParentFile {
    private String fileName = "file:///file.unknown";
    private String fileType = "unknown";
    private String fileSHA = "bogusSHA1hashThatHas40Characters________";
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public String getFileSHA() {
        return fileSHA;
    }
    
    public void setFileSHA(String fileSHA) {
        this.fileSHA = fileSHA;
    }
}
