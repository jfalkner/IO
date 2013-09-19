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

package org.proteomecommons.io;

/**
 * This exception represents an invalid file format problem with the a peak list or spectrum file. PeakListReader object may choose to quietly handle any such exceptions in an effort to correct for poorly authored files; however, if the exception cannot be corrected than this exception is thrown.
 * @author Jayson Falkner - jfalkenr@umich.edu
 */
public class InvalidFileFormatException extends RuntimeException {
    
	public InvalidFileFormatException(Throwable cause) {
		super(cause);
	}

	public InvalidFileFormatException(String message) {
		super(message);
	}

	public InvalidFileFormatException(String message, Throwable cause) {
		super(message, cause);
	}    
}
