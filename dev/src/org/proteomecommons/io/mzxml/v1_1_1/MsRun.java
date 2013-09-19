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
package org.proteomecommons.io.mzxml.v1_1_1;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MsRun {
    private List<ParentFile> parentFiles = new LinkedList();
    private MsInstrument msInstrument = null;
    private List<DataProcessing> dataProcessings = new LinkedList();
    private Separation separation = null;
    private Spotting spotting = null;
    private Sha1 sha1 = null;
    private String scanCount = null;
    private String startTime = null;
    private String endTime = null;
    
    public MsRun() {
        // add the data processing for ProteomeCommons.org IO Framework
        DataProcessing dp = new DataProcessing();
        Software software = new Software();
        software.setName("ProteomeCommons.org IO Framework - http://www.proteomecommons.org");
        software.setVersion("6.0");
        software.setType("processing");
        dp.setSoftware(software);
        addDataProcessing(dp);
    }
    
    public List<ParentFile> getParentFiles() {
        return parentFiles;
    }
    
    public void addParentFile(ParentFile parentFile) {
        parentFiles.add(parentFile);
    }
    
    public MsInstrument getMsInstrument() {
        return msInstrument;
    }
    
    public void setMsInstrument(MsInstrument msInstrument) {
        this.msInstrument = msInstrument;
    }
    
    public List<DataProcessing> getDataProcessings() {
        return dataProcessings;
    }
    
    public void addDataProcessing(DataProcessing dataProcessing) {
        dataProcessings.add(dataProcessing);
    }
    
    public Separation getSeparation() {
        return separation;
    }
    
    public void setSeparation(Separation separation) {
        this.separation = separation;
    }
    
    public Spotting getSpotting() {
        return spotting;
    }
    
    public void setSpotting(Spotting spotting) {
        this.spotting = spotting;
    }
    
    public Sha1 getSha1() {
        return sha1;
    }
    
    public void setSha1(Sha1 sha1) {
        this.sha1 = sha1;
    }
    
    public String getScanCount() {
        return scanCount;
    }
    
    public void setScanCount(String scanCount) {
        this.scanCount = scanCount;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
