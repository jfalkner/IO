/*
 *    Copyright 2004 Jayson Falkner
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
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 * An abstract representation of a spectrum. Precision is limited to 8 bytes before the decimal place and 8 bytes after, which is more than enough for the main-stream mass spectrometers.
 */
public class Spectrum {
  public double[][] points = new double[0][0];
}
