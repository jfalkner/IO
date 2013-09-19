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
package org.proteomecommons.io.util;

import java.util.*;
import org.proteomecommons.io.*;

/**
 * Utility class that can be used to sort peaks based on their intensity.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class PeakIntensityComparator implements Comparator {
	public int compare(Object a, Object b) {
		if (!(a instanceof Peak && b instanceof Peak)) {
			return 0;
		}
		Peak pa = (Peak) a;
		Peak pb = (Peak) b;
		if (pa.getIntensity() > pb.getIntensity()) {
			return 1;
		}
		if (pa.getIntensity() < pb.getIntensity()) {
			return -1;
		}
		return 0;
	}
}