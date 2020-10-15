/*
 * The MIT License
 *
 * Copyright (c) 2016, AbsInt Angewandte Informatik GmbH
 * Author: Dr.-Ing. Joerg Herter
 * Email: herter@absint.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.absint.astree;

import hudson.FilePath;

import java.io.*;


/**
 * Stores an analysis (result) summary and provides an interface to the analysis result for the
 * Astrée PlugIn classes.
 *
 *
 * @author AbsInt Angewandte Informatik GmbH
 */
public class AnalysisSummary {

    private int numberOfErrors;
    private int numberOfAlarms;
    private int numberOfFlowAnomalies;
    private int numberOfRuleViolations;
    private int numberOfTrueAlarms;           // not implemented yet
    private int numberOfUncommentedAlarms;    // not implemented yet

/**
 * Private constructor.
 */
    private AnalysisSummary(int numberOfErrors, int numberOfAlarms, 
                            int numberOfFlowAnomalies, int numberOfRuleViolations,
                            int numberOfTrueAlarms, int numberOfUncommentedAlarms ) {
        this.numberOfErrors = numberOfErrors;
        this.numberOfAlarms = numberOfAlarms;
        this.numberOfFlowAnomalies = numberOfFlowAnomalies;
        this.numberOfRuleViolations    = numberOfRuleViolations;
        this.numberOfTrueAlarms        = numberOfTrueAlarms;
        this.numberOfUncommentedAlarms = numberOfUncommentedAlarms;
    }

/**
 * Constructs an AnalyisSummary object from a report file (txt version).
 *
 * @param path        Path (as {@link java.lang.String}) to the Astrée text report 
 *                    from which the object is to be constructed.
 * @return AnalysisSummary object providing easy access to data of an Astrée report 
 * @throws IOException               as super class
 * @throws InterruptedException      as super class
 */
    static public AnalysisSummary readFromReportFile(FilePath path)
            throws IOException, InterruptedException {
        int numberOfErrors = 0;
        int numberOfAlarms = 0;
        int numberOfFlowAnomalies     = 0;
        int numberOfRuleViolations    = 0;
        int numberOfTrueAlarms        = 0;   // not implemented yet
        int numberOfUncommentedAlarms = 0;   // not implemented yet
            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(path.read(), "UTF-8" ));
            String line = br.readLine();
            boolean skipping = true;
            while(line != null) {
                if(!skipping) {
                    if(line.trim().startsWith("Errors:"))
                        numberOfErrors = Integer.parseInt(
                                             line.substring(line.indexOf(":") + 1, line.length()).trim());    
                    else if(line.trim().startsWith("Run-time errors:"))
                        numberOfAlarms = Integer.parseInt(
                                             line.substring(line.indexOf(":") + 1, line.length()).trim());
                    else if(line.trim().startsWith("Flow anomalies:"))
                        numberOfFlowAnomalies = Integer.parseInt(
                                             line.substring(line.indexOf(":") + 1, line.length()).trim());
                    else if(line.trim().startsWith("Rule violations:"))
                        numberOfRuleViolations = Integer.parseInt(
                                             line.substring(line.indexOf(":") + 1, line.length()).trim());


                }
                // skip report until Summary section is reached
                if(skipping && line.trim().startsWith("/* Result summary */"))
                    skipping = false;
                line = br.readLine();
            }
            br.close();
        return new AnalysisSummary(numberOfErrors, numberOfAlarms, 
                                   numberOfFlowAnomalies, numberOfRuleViolations,
                                   numberOfTrueAlarms, numberOfUncommentedAlarms );
    }


/*
 * Interface for Astrée PlugIn class. 
 */

/**
 * Returns the number of reported definite runtime errors ("errors").
 *
 * @return int
 */ 
    public int getNumberOfErrors() {
        return this.numberOfErrors;
    }

/**
 * Returns the number of reported potential runtime errors ("alarms").
 *
 * @return int
 */ 
    public int getNumberOfAlarms() {
        return this.numberOfAlarms;
    }

/**
 * Returns the number of reported flow anaomalies ("Type D alarms").
 *
 * @return int
 */ 
    public int getNumberOfFlowAnomalies() {
        return this.numberOfFlowAnomalies;
    }

/**
 * Returns the number of reported rule violations ("Type R alarms").
 *
 * @return int
 */ 
    public int getNumberOfRuleViolations() {
        return this.numberOfRuleViolations;
    }

/**
 * Returns the number of reported potential runtime errors ("alarms") classified as "true".
 *
 * @return int
 */
    public int getNumberOfTrueAlarms() {
        return this.numberOfTrueAlarms;
    }

/**
 * Returns the number of reported potential runtime errors ("alarms") not commented.
 *
 * @return int
 */
    public int getNumberOfUncommentedAlarms() {
        return this.numberOfUncommentedAlarms;
    }
}
