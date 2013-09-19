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

package org.proteomecommons.io.fasta.generic;

import java.io.File;
import java.io.FileInputStream;
import org.proteomecommons.io.fasta.FASTAReader;
import org.proteomecommons.io.fasta.FASTAReaderFactory;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class GenericFASTAReaderFactory implements FASTAReaderFactory {
    
    public String getFileExtension() {
        return ".fasta";
    }
    
    public String getName() {
        return "Generic FASTA";
    }
    
    public FASTAReader newInstance(String filename) {
        try {
            return new GenericFASTAReader(new FileInputStream(new File(filename)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
