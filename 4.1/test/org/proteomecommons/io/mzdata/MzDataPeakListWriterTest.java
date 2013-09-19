/*
 * Created on Jun 6, 2005
 *
 */
package org.proteomecommons.io.mzdata;

import java.io.*;
import java.util.HashMap;

import org.proteomecommons.io.*;

import junit.framework.TestCase;

/**
 * TODO Properly describe this class
 *
 * @author Jarret Falkner - jar@cs.washington.edu
 */
public class MzDataPeakListWriterTest extends TestCase {

	MzDataPeakListWriter writer;

	/*
	 * Class under test for void write(PeakList) 
	 */
	public void testWritePeakList1() throws IOException {
		writer = new MzDataPeakListWriter(new FileOutputStream(new File("test1.mzData")));
		//test bare minimum settings... 
		MzDataPeakList list = new MzDataPeakList();
		GenericPeak[] peaks = new GenericPeak[1];
		peaks[0] = new GenericPeak();
		peaks[0].setMassOverCharge(1.4653);
		peaks[0].setIntensity(914.3564);
		list.setPeaks(peaks);
		
		// specify some attributes
		MzDataPeakListSpectrumSettings settings = new MzDataPeakListSpectrumSettings(2, 100, 200);
		
		settings.setAcqSpecification("sum", "blah");
		
		settings.setAcqNumber(4836);
		
		HashMap map = new HashMap();
		map.put("type", "full");
		map.put("polarity", "+");
		settings.setSpectrumInstrumentParameters(map);
		
		list.setSpectrumSettings(settings);
		
		//make up a precursor
		MzDataPeakListPrecursorListItem item = new MzDataPeakListPrecursorListItem(1, 5);
		
		HashMap ionSelection = new HashMap();
		ionSelection.put("mz", "334.82");
		item.setIonSelection(ionSelection);
		
		HashMap activation = new HashMap();
		activation.put("method", "CID");
		item.setActivation(activation);
		
		//set the precursor list to have only the made up precursor
		MzDataPeakListPrecursorList plist = new MzDataPeakListPrecursorList(item);
		
		
		list.setPrecursorList(plist);
		
		try{
			writer.write(list);
			writer.close();
		} catch (CheckedIOException cioe){
			cioe.printStackTrace();
		}
	}
	
	/**
	 * Test all of the settings, and adds multiple peaklists, not just one
	 * @throws IOException
	 */
	public void testWritePeakList2() throws IOException {
		HashMap analyzer = new HashMap();
		analyzer.put("analyzerName","AnalyzerValue");
		
		HashMap detector = new HashMap();
		detector.put("detectorName","detectorValue");
		
		HashMap processingMethod = new HashMap();
		processingMethod.put("procMethodName","ProcMethodValue");
		
		writer = new MzDataPeakListWriter(
				new FileOutputStream(new File("test2.mzData")),
				"Name of the Sample",
				"Name of the File",
				"Filetype",
				"Path:/To\\File",
				"Relevant Contact Information",
				"An Institution",
				"Name",
				"An instrument",
				"The Source",
				//"filename.extension", 
				analyzer,
				detector,
				"Super Software",
				"Version 20",
				processingMethod);
		
		//test bare minimum settings... 
		MzDataPeakList list = new MzDataPeakList();
		GenericPeak[] peaks = new GenericPeak[1];
		peaks[0] = new GenericPeak();
		peaks[0].setMassOverCharge(1.4653);
		peaks[0].setIntensity(914.3564);
		list.setPeaks(peaks);
		
		// specify some attributes
		MzDataPeakListSpectrumSettings settings = new MzDataPeakListSpectrumSettings(2, 100, 200);
		
		settings.setAcqSpecification("sum", "blah");
		settings.setAcqNumber(4836);
		
		HashMap map = new HashMap();
		map.put("type", "full");
		map.put("polarity", "+");
		settings.setSpectrumInstrumentParameters(map);
		
		list.setSpectrumSettings(settings);
		
		//make up a precursor
		MzDataPeakListPrecursorListItem item = new MzDataPeakListPrecursorListItem(1, 5);
		
		HashMap ionSelection = new HashMap();
		ionSelection.put("mz", "334.82");
		item.setIonSelection(ionSelection);
		
		HashMap activation = new HashMap();
		activation.put("method", "CID");
		item.setActivation(activation);
		
		//set the precursor list to have only the made up precursor
		MzDataPeakListPrecursorList plist = new MzDataPeakListPrecursorList(item);
		list.setPrecursorList(plist);
		
		
		//test more settings and two peaklists....
		MzDataPeakList list2 = new MzDataPeakList();
		GenericPeak[] peaks2 = new GenericPeak[2];
		peaks2[0] = new GenericPeak();
		peaks2[0].setMassOverCharge(1.4653);
		peaks2[0].setIntensity(914.3564);
		peaks2[1] = new GenericPeak();
		peaks2[1].setMassOverCharge(1.4613);
		peaks2[1].setIntensity(2314.3564);
		list2.setPeaks(peaks2);
		
		// specify some attributes
		MzDataPeakListSpectrumSettings settings2 = new MzDataPeakListSpectrumSettings(2, 50, 90);
		
		settings2.setAcqSpecification("sum", "blah");
		settings2.setAcqNumber(23);
		
		HashMap map2 = new HashMap();
		map2.put("type", "full");
		map2.put("polarity", "+");
		settings.setSpectrumInstrumentParameters(map2);
		
		list2.setSpectrumSettings(settings2);
		
		//make up a precursor
		MzDataPeakListPrecursorListItem item2 = new MzDataPeakListPrecursorListItem(1, 5);
		
		HashMap ionSelection2 = new HashMap();
		ionSelection2.put("mz", "314.82");
		item2.setIonSelection(ionSelection2);
		
		HashMap activation2 = new HashMap();
		activation2.put("method", "CID2");
		item2.setActivation(activation2);
		
		//set the precursor list to have only the made up precursor
		MzDataPeakListPrecursorList plist2 = new MzDataPeakListPrecursorList(item2);
		list2.setPrecursorList(plist2);
		
		try{
			writer.write(list);
			writer.write(list2);
			writer.close();
		} catch (CheckedIOException cioe){
			cioe.printStackTrace();
		}
	}

}
