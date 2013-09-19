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
 * This is a checked exception thrown by the framework's IO methods, and it is
 * designed to keep developers honest. This sub-class forces you to handle any
 * other exception within your own method and pass it along as an appropriate
 * instance of this class. Don't be lazy. Use one of the appropriate sub-classes
 * to formally descript what the error was.
 * 
 * Treat instances of this class as fatal problems that are described by the
 * given message and option cause.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class CheckedIOException extends Exception {
	/**
	 * @author root
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
	public class CanOnlyContainOneSpectrumException {

	}
	public CheckedIOException(Throwable cause) {
		super(cause);
	}

	public CheckedIOException(String message) {
		super(message);
	}

	public CheckedIOException(String message, Throwable cause) {
		super(message, cause);
	}
}