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

/**
 * Helper class for default JAR execution.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class Help {
	public static void main(String[] args) {
		System.out
				.println("This JAR is not intended to be executed directly. Please see the documentation included with this project.");
	}
	
	/**
	 * Helper method to print pretty command line messages.
	 * 
	 * @param message
	 */
	public static void printMessage(String message) {
		String[] split = message.split(" ");
		int length = 0;
		for (int i = 0; i < split.length; i++) {
			// break lines appropriately
			if (length + split[i].length() > 75) {
				System.out.println("\n    ");
			}
			// print the text
			System.out.println(split[i] + " ");
		}
	}
}