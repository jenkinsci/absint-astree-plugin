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

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Helper class for the com.absint.astree.AstreeBuilder.
 * 
 * Stores the project-level settings of the fail-on-switch
 * that allows the analyzer to fail a Jenkins projects on
 * <ul>
 *   <li>errors</li>
 *   <li>errors and alarms classified as true code defects by the user</li>
 *   <li>errors and alarms uncommented by the user</li>
 *   <li>errors and alarms</li>
 *   <li>errors, alarms, and data-flow anomalies</li>
 * </ul>
 *
 * @author AbsInt Angewandte Informatik GmbH
 */

public class FailonSwitch {


/**
 * Short name (visible in the (Jenkins) console output and
 * HTML pages) for the different situations in which
 * the plugin may fail a build.
 * <br>
 * Valid names are:
 * <ul>
 *   <li>errors</li>
 *   <li>true-alarms</li>
 *   <li>uncommented-alarms</li>
 *   <li>alarms</li>
 *   <li>data-flow-anomalies</li>
 * </ul>
*/
    private String failon;    

    @DataBoundConstructor
    public FailonSwitch(String failon) {
        this.failon = failon;
    }

    public void setFailon(String failon) {
        this.failon = failon;
    }

/**
 * Returns the short name for the currently 
 * configured/set situations in which
 * the plugin may fail a build.
 * <br>
 * Valid short names are:
 * <ul>
 *   <li>errors</li>
 *   <li>true-alarms</li>
 *   <li>uncommented-alarms</li>
 *   <li>alarms</li>
 *   <li>data-flow-anomalies</li>
 * </ul>
 *
 * @return java.lang.String
 */
    public String getFailon() {
        return this.failon;
    }


/*
 * Interface for Astrée PlugIn class. 
 */

/**
 * Determines whether the configuration is set to fail a build
 * in case a definite runtime error is reported.
 *
 *
 * @return boolean
 */
    public boolean failOnErrors() {
        if(failon == null)
            return false;
        return this.failon.equals("errors");
    }

/**
 * Determines whether the configuration is set to fail a build
 * in case a potential runtime error ("alarm") is reported.
 *
 *
 * @return boolean
 */
    public boolean failOnAlarms() {
        if(failon == null)
            return false;
        return this.failon.equals("alarms");
    }

/* *
 * Determines whether the configuration is set to fail a build
 * in case a potential runtime error ("alarm") classified as "true" is reported.
 *
 *
 * @return boolean
    public boolean failOnTrueAlarms() {
        if(failon == null)
            return false;
        return this.failon.equals("true-alarms");
    }
 */

/* *
 * Determines whether the configuration is set to fail a build
 * in case a potential runtime error ("alarm") which ist not commented is reported.
 *
 *
 * @return boolean
    public boolean failOnUncommentedAlarms() {
        if(failon == null)
            return false;
        return this.failon.equals("uncommented-alarms");
    }
 */

/**
 * Determines whether the configuration is set to fail a build
 * in case a data-flow anomaly ("Type D alarm") is reported.
 *
 *
 * @return boolean
 */
    public boolean failOnDataflowAnomalies() {
        if(failon == null)
            return false;
        return this.failon.equals("data-flow-anomalies");
    }
}
