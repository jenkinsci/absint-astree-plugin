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
import hudson.Proc;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 *
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link AstreeBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields 
 * to remember the configuration.
 *
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 * @author AbsInt Angewandte Informatik GmbH
 */
public class AstreeBuilder extends Builder implements SimpleBuildStep {
    private static final String PLUGIN_NAME = "Astrée for C Jenkins PlugIn";
    private static final String BUILD_NR    = "17.10";

    private static final String TMP_REPORT_FILE = "absint_astree_analysis_report";
    private static final String TMP_PREPROCESS_OUTPUT = "absint_astree_preprocess_output.txt";

    private String dax_file, output_dir, analysis_id;
    private FailonSwitch failonswitch;
    private boolean genXMLOverview, genXMLCoverage, genXMLAlarmsByOccurence, 
                    genXMLAlarmsByCategory, genXMLAlarmsByFile, genXMLRulechecks,
                    genPreprocessOutput, dropAnalysis;
    private boolean skip_analysis;

    private Proc proc; // reference to an associated a3c client process 

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public AstreeBuilder( String dax_file, String analysis_id, String output_dir, boolean skip_analysis,
                          boolean genXMLOverview, boolean genXMLCoverage, boolean genXMLAlarmsByOccurence,
                          boolean genXMLAlarmsByCategory, boolean genXMLAlarmsByFile, boolean genXMLRulechecks,
                          boolean dropAnalysis, boolean genPreprocessOutput, FailonSwitch failonswitch
                        ) 
    {
        this.dax_file        = dax_file;
        this.analysis_id     = analysis_id;
        this.output_dir      = output_dir;
        this.skip_analysis   = skip_analysis;
        this.failonswitch  = failonswitch;

        this.genXMLOverview          = genXMLOverview;
        this.genXMLCoverage          = genXMLCoverage;
        this.genXMLAlarmsByOccurence = genXMLAlarmsByOccurence;
        this.genXMLAlarmsByCategory  = genXMLAlarmsByCategory;
        this.genXMLAlarmsByFile      = genXMLAlarmsByFile;
        this.genXMLRulechecks        = genXMLRulechecks;

        this.dropAnalysis            = dropAnalysis;
        this.genPreprocessOutput     = genPreprocessOutput;
    }

    /*
     * Interface to <tt>config.jelly</tt>.
     */

    /**
     * Returns the currently set path to the DAX file used for the analysis run.
     *
     * @return java.lang.String
     */
    public String getDax_file() {
        return dax_file;
    }

    /**
     * Returns the currently set analysis ID used for the analysis run.
     *
     * @return java.lang.String
     */
    public String getAnalysis_id() {
        return analysis_id;
    } 

    /**
     * Returns the currently set path used as output directory for the analyses.
     *
     * @return java.lang.String
     */
    public String getOutput_dir() {
        return output_dir;
    }

    /**
     * Indicates whether the analysis run is configured to potentially fail a build.
     *
     * @return boolean
     */
    public boolean isFailonswitch() {
        return (this.failonswitch != null);
    }

    /**
     * @return java.lang.String
     */
    public String getFailon() {
        if(this.failonswitch == null) return ""; 
        return this.failonswitch.getFailon();
    }

    /**
     * Indicates whether the analysis run is configured to
     * be temporarily skipped (i.e., no analysis is to be done).
     *
     * @return boolean
     */
    public boolean isSkip_analysis() {
    	return this.skip_analysis;
    }    
 	
    /**
     * Indicates whether the analysis run is configured to produce the
     * XML overview summary.
     *
     * @return boolean
     */
    public boolean isGenXMLOverview() {
        return this.genXMLOverview;
    }

    /**
     * Indicates whether the analysis run is configured to produce the
     * XML coverage summary.
     *
     * @return boolean
     */
    public boolean isGenXMLCoverage() {
        return this.genXMLCoverage;
    }

    /**
     * Indicates whether the analysis run is configured to produce the
     * XML alarms-by-occurence summary.
     *
     * @return boolean
     */
    public boolean isGenXMLAlarmsByOccurence() {
        return this.genXMLAlarmsByOccurence;
    }

    /**
     * Indicates whether the analysis run is configured to produce the
     * XML alarms-by-category summary.
     *
     * @return boolean
     */
    public boolean isGenXMLAlarmsByCategory() {
        return this.genXMLAlarmsByCategory;
    }

    /**
     * Indicates whether the analysis run is configured to produce the
     * XML alarms-by-file summary.
     *
     * @return boolean
     */
    public boolean isGenXMLAlarmsByFile() {
        return this.genXMLAlarmsByFile;
    }

    /**
     * Indicates whether the analysis run is configured to produce the
     * XML rule checks summary.
     *
     * @return boolean
     */
    public boolean isGenXMLRulechecks() {
        return this.genXMLRulechecks;
    }

    /**
     * Indicates whether the analysis run is configured to produce the
     * (text) preprocess output report.
     *
     * @return boolean
     */
    public boolean isGenPreprocessOutput() {
        return this.genPreprocessOutput;
    }

    /**
     * Indicates whether the project is to be deleted on the server after
     * the analysis run.
     *
     * @return boolean
     */
    public boolean isDropAnalysis() {
        return this.dropAnalysis;
    }


    /*
     *  end interface to <tt>config.jelly</tt>.
     */


  /**
   * Expands environment variables of the form 
   *       ${VAR_NAME}
   * by their current value.
   *
   * @param cmdln	the java.lang.String, usually a command line, 
   *                    in which to expand variables
   * @param envMap	a java.util.Map containing the environment variables 
   *                    and their current values
   * @param isUnix      boolean
   * @return the input String with environment variables expanded to their current value
   */
   private static final String expandEnvironmentVarsHelper(
                                  String cmdln, Map<String, String> envMap, boolean isUnix ) {
      final String pattern = "\\$\\{([A-Za-z_][A-Za-z0-9_]*)\\}";
      final Pattern expr = Pattern.compile(pattern);
      Matcher matcher = expr.matcher(cmdln);
      String  envValue;
      Pattern subexpr;
      while (matcher.find()) {
         envValue = envMap.get(matcher.group(1).toUpperCase());
         if (envValue == null) {
            envValue = "";
         } else {
           envValue = envValue.replace("\\", "\\\\");
         }
         subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
         cmdln = subexpr.matcher(cmdln).replaceAll(envValue);
      }

      if(isUnix) {
         return cmdln.replace('\\','/');
      } else {
         return cmdln.replace('/','\\');
      } 
   }


    /**
      */
    private String constructCommandLineCall(String reportfile, String preprocessoutput ) {
        String cmd    = getDescriptor().getAlauncher();
        boolean c1610 = getDescriptor().getComp1610();

        cmd = cmd  + " " + (c1610 ? "-a " : "") + "-b -s "                        +
                     getDescriptor().getAstree_server()  + " "                     +
                     ((this.analysis_id != null && !this.analysis_id.trim().equals("")) ?
                        (" --id " + this.analysis_id) : "" )                       +
                     ((this.dax_file != null && !this.dax_file.trim().equals("") )      ?
                        (" --import \"" + this.dax_file + "\"") : "")              +
                     " --report-file " + "\"" + reportfile + ".txt\"" +
                     " --xml-result-file " + "\"" + reportfile + ".xml\"";
        if(this.genPreprocessOutput)
                cmd += " --preprocess-report-file " + "\"" + preprocessoutput + "\"";
        if(this.dropAnalysis)
                cmd += " --drop";

        if(!c1610)
            return cmd;

        if(this.genXMLOverview)
                cmd += " --report-overview " + "\"" + output_dir + "/Overview.xml\"";
        if(this.genXMLCoverage)
                cmd += " --report-coverage " + "\"" + output_dir + "/Coverage.xml\"";
        if(this.genXMLAlarmsByOccurence)
                cmd += " --report-alarmsByOccurence " + "\"" + output_dir + "/AlarmsByOccurence.xml\"";
        if(this.genXMLAlarmsByCategory)
                cmd += " --report-alarmsByCategory " + "\"" + output_dir + "/AlarmsByCategory.xml\"";
        if(this.genXMLAlarmsByFile)
                cmd += " --report-alarmsByFile " + "\"" + output_dir + "/AlarmsByFile.xml\"";
        if(this.genXMLRulechecks)
                cmd += " --report-rulechecks " + "\"" + output_dir + "/Rulechecks.xml\"";

        return cmd; 
    }


    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        int exitCode = -1;
        // Set some defaults and parameters.
        if(output_dir == null || output_dir.equals(""))
            output_dir = workspace.toString();
        String reportfile = workspace.toString() + (launcher.isUnix() ? "/" : "\\") + TMP_REPORT_FILE;

        File rfile;
        try {
           // Analysis run started. ID plugin in Jenkins output.
            listener.getLogger().println("This is " + PLUGIN_NAME + " in version " + BUILD_NR);
            // Clear log file
            rfile = new java.io.File(reportfile + ".txt");
            if( rfile.delete() )
               listener.getLogger().println("Old log file erased.");
            if( rfile.createNewFile() )
               listener.getLogger().println("New log file created.");
            // Create log file reader thread
            StatusPoller sp = new StatusPoller(1000, listener, rfile);
        
            if(this.skip_analysis) {
        	listener.getLogger().println("Analysis run has been (temporarily) deactivated. Skipping analysis run.");
        	return; // nothing to do, exit method.
            }

            // Print some configuration info.
            if(failonswitch != null) 
                listener.getLogger().println( "Astrée fails build on " + failonswitch.getFailon() );        
        
            String infoStringSummaryDest = "Summary reports will be generated in " + output_dir;
            infoStringSummaryDest =  expandEnvironmentVarsHelper(
                                               "Summary reports will be generated in " + output_dir, 
                                               build.getEnvironment(listener),
                                               launcher.isUnix()); 
            listener.getLogger().println(infoStringSummaryDest);


            String cmd = this.constructCommandLineCall( reportfile,
                                                    workspace.toString() + "/" + TMP_PREPROCESS_OUTPUT  );
            

            cmd = expandEnvironmentVarsHelper(cmd, build.getEnvironment(listener), launcher.isUnix());
            sp.start();                       // Start log file reader
            proc = launcher.launch( cmd, // Command line call to Astree
                                    build.getEnvironment(listener), 
                                    listener.getLogger(),
                                    workspace );
            exitCode = proc.join();           // Wait for Astree to finish
            sp.kill();                        // Stop log file reader
            sp.join();                        // Wait for log file reader to finish
            if(exitCode == 0)
                listener.getLogger().println("Analysis run succeeded.");
            else 
                listener.getLogger().println("Analysis run failed.");
         } catch (IOException e) {
            e.printStackTrace();
            listener.getLogger().println("IOException caught during analysis run.");
         } catch (InterruptedException e) {
            e.printStackTrace();
            listener.getLogger().println("InterruptedException caught during analysis run.");
         }
         if(exitCode == 0) { // activities after successful analysis run
                /* Read analysis summary and 
                   check whether Astrée shall fail the build due to reported errors etc */
                AnalysisSummary summary = AnalysisSummary.readFromReportFile(reportfile + ".txt");
                if(      failonswitch != null && failonswitch.failOnErrors() 
                      && summary.getNumberOfErrors() > 0) {
                    listener.getLogger().println( "Errors reported! Number of errors: " + 
                                                  summary.getNumberOfErrors());
                    build.setResult(hudson.model.Result.FAILURE);
                }                
                else if(     failonswitch != null && failonswitch.failOnAlarms() 
                          && summary.getNumberOfAlarms() > 0) {
                    listener.getLogger().println( "Alarms reported! Number of alarms: " + 
                                                  summary.getNumberOfAlarms());

                    build.setResult(hudson.model.Result.FAILURE);
                }
                else if(     failonswitch != null && failonswitch.failOnFlowAnomalies()
                          && (   summary.getNumberOfFlowAnomalies() 
                               + summary.getNumberOfAlarms() > 0) ) {
                    build.setResult(hudson.model.Result.FAILURE);
                }
         } else {  // activities after unsuccessful analysis run
                // If Astrée cannot be invoked, conservatively fail the build...   
                build.setResult(hudson.model.Result.FAILURE);
         }
    }
    
    /**
     * Override finalize method to ensure existing a3c client processes are killed upon destruction
     * of AstreeBuilder objects.
     */
    protected void finalize() {
        try {
           if(proc != null)
              proc.kill();
        } catch(Exception e) {
        }
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }



    private void copyText2PrintStream( PrintStream dest, String srcPath ) {
        dest.println("Appending analysis report.");
        try{
            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(
                                       new FileInputStream(srcPath), "UTF-8" ));
            String line = br.readLine() ;
            while(line != null) {
                dest.println(line);
                line = br.readLine();
            }
            br.close();
        } catch(IOException e) {
        }       
    }   


    /**
     * Descriptor for {@link AstreeBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <br>
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /*
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         *
         * If you don't want fields to be persisted, use "transient".
         */

        /*
         * Properties set by the Astree configuration mask:
         *     Jenkins~~~Manage Jenkins~~~Configure System
         */
        private String alauncher;
        private boolean comp1610;
        private String astree_server;
        private String user, password;

        /**
         * Constructor.
         * <br>
         * Constructs a new object of this class and
         * loads the persisted global configuration.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Return the human readable name  used in the configuration screen.
         *
         * @return java.lang.String
         */
        public String getDisplayName() {
            return "Astrée Analysis Run";
        }


/**
 * Performs on-the-fly validation of the form field 'astree_server'.
 *
 * @param value      The value that the user has typed.
 * @return
 *      Indicates the outcome of the validation. This is sent to the browser.
 *      <br>
 *      Note that returning {@link FormValidation#error(String)} does not
 *      prevent the form from being saved. It just means that a message
 *      will be displayed to the user. 
 * @throws IOException               as super class
 * @throws ServletException          as super class
 **/
        public FormValidation doCheckAstree_server(@QueryParameter String value)
                throws IOException, ServletException {
            if(value == null || value.trim().equals("") )
                return FormValidation.error("Please set a valid server of form <hostname>:<port>");
            if ( !(     value.matches("[a-zA-Z][a-zA-Z0-9\\.\\-]{0,22}[a-zA-Z0-9]:\\d{1,5}") /* hostname */
                     || value.matches("(\\d{1,3}\\.){3,3}\\d{1,3}:\\d{1,5}"                  /* ip address */)  ) )
                return FormValidation.warning("The Astrée Server needs to be specified as a hostname followed by a colon followed by a port number.");
            return FormValidation.ok();
        }

/**
 * Performs on-the-fly validation of the form field 'alauncher'.
 *
 * @param value           The value that the user has typed.
 * @return
 *      Indicates the outcome of the validation. This is sent to the browser.
 *      <br>
 *      Note that returning {@link FormValidation#error(String)} does not
 *      prevent the form from being saved. It just means that a message
 *      will be displayed to the user.
 * @throws IOException             as super class
 * @throws ServletException        as super class
 **/
        public FormValidation doCheckAlauncher(@QueryParameter String value, AbstractProject project)
                throws IOException, ServletException {
            if(value == null || value.trim().equals("") )
               return FormValidation.error("No file specified.");

            File ftmp = new File(value);
            if (!ftmp.exists())
                return FormValidation.error("Specified file not found.");
            if(!ftmp.isFile())
                return FormValidation.error("Specified file is not a normal file.");
            if (!ftmp.canExecute())
                return FormValidation.error("Specified file cannot be executed.");
            try {
                String line;
                StringBuffer ret = new StringBuffer();
                Process p = Runtime.getRuntime().exec(value + " -b c --version-file v.info");
                p.waitFor();
                BufferedReader input = new BufferedReader(
                                         new InputStreamReader(
                                          new FileInputStream("v.info"), "UTF-8" ));
                while ((line = input.readLine()) != null) {
                   ret.append("\n");
                   ret.append(line);
                }
                input.close();
                return FormValidation.ok(ret.toString() + "\n\n");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException ie) {
            }
            return FormValidation.ok();
        }



/**
 * Helper method to check whether a string contains an environment variable of form
 * <br><tt>${IDENTIFIER}</tt><br>
 *
 * @param   s    String to scan for environment variable expressions
 * @return  Outcome of the check as a boolean (true if such an expression
 *          was found, otherwise false).
 */
       public static final boolean containsEnvVars(String s)
       {
           final String pattern = "\\$\\{([A-Za-z_][A-Za-z0-9_]*)\\}";
           final Pattern expr = Pattern.compile(pattern);
           Matcher matcher = expr.matcher(s);
           return matcher.find();
       } 


/**
 * Performs on-the-fly validation of the form field 'dax_file'.
 *
 * @param value
 *      This parameter receives the value that the user has typed.
 * @return
 *      Indicates the outcome of the validation. This is sent to the browser.
 *      <br>
 *      Note that returning {@link FormValidation#error(String)} does not
 *      prevent the form from being saved. It just means that a message
 *      will be displayed to the user. 
 * @throws IOException               as super class
 * @throws ServletException          as super class
 **/
        public FormValidation doCheckDax_file(@QueryParameter String value)
                throws IOException, ServletException {
            if(value == null || value.trim().equals("") )
               return FormValidation.warning("No file specified.");

            if(containsEnvVars(value)) {
               return FormValidation.warning("The specified path contains an environment variable, please make sure the constructed paths are correct.");
            }

            File ftmp = new File(value);
            if (!ftmp.exists())
                return FormValidation.error("Specified file not found.");
            if (!ftmp.canRead())
                return FormValidation.error("Specified file cannot be read.");
            if (!value.endsWith(".dax"))
                return FormValidation.warning("The specified file exists, but does not have the expected suffix (.dax).");

            return FormValidation.ok();
        }

/**
 * Performs on-the-fly validation of the form field 'analysis_id'.
 *
 * @param value
 *      This parameter receives the value that the user has typed.
 * @return
 *      Indicates the outcome of the validation. This is sent to the browser.
 *      <br>
 *      Note that returning {@link FormValidation#error(String)} does not
 *      prevent the form from being saved. It just means that a message
 *      will be displayed to the user. 
 * @throws IOException               as super class
 * @throws ServletException          as super class
 **/
        public FormValidation doCheckAnalysis_id(@QueryParameter String value)
                throws IOException, ServletException {
            if(value == null || value.trim().equals("") )
               return FormValidation.warning("No ID specified.");

            if(containsEnvVars(value)) {
               return FormValidation.warning("The ID contains an environment variable, please make sure that the constructed IDs are valid.");
            }


            if(!value.matches("\\d*"))
               return FormValidation.error("ID is not valid.");

            return FormValidation.ok();
        }

/**
 * Performs on-the-fly validation of the form field 'output_dir'.
 *
 * @param value
 *      This parameter receives the value that the user has typed.
 * @return
 *      Indicates the outcome of the validation. This is sent to the browser.
 *      <br>
 *      Note that returning {@link FormValidation#error(String)} does not
 *      prevent the form from being saved. It just means that a message
 *      will be displayed to the user. 
 * @throws IOException               as super class
 * @throws ServletException          as super class
 **/
        public FormValidation doCheckOutput_dir(@QueryParameter String value)
                throws IOException, ServletException {
            if(value == null || value.trim().equals("") )
               return FormValidation.warning("No directory specified.");

            if(containsEnvVars(value)) {
               return FormValidation.warning("The specified path contains an environment variable, please make sure that the constructed paths are correct.");
            }

            File ftmp = new File(value);
            if (!ftmp.exists())
                return FormValidation.error("Specified directory not found.");
            if (!ftmp.isDirectory())
                return FormValidation.error("Specified path is no directory.");
            if (!ftmp.canRead() || !ftmp.canWrite())
                return FormValidation.warning("No permissions to read/write the specified directory.");

            return FormValidation.ok();
        }



/**
 * Indicates that this builder can be used with all kinds of project types.
 *
 * @return boolean
 */
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

/**
 * Sets a new configuration.
 * 
 * @throws FormException           as super class
 */
        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            this.alauncher     = formData.getString("alauncher");
            this.comp1610      = formData.getBoolean("comp1610");
            this.astree_server = formData.getString("astree_server");
            this.user          = formData.getString("user");
            this.password      = formData.getString("password");
            // ... data set, so call save(): 
            save();
            return super.configure(req,formData);
        }

/**
 * Returns the currently configured Astrée server
 * (as <i>host:port</i>).
 *
 * @return java.lang.String
 */
        public String getAstree_server() {
            return this.astree_server;
        }

/**
 * Returns the currently configured alauncher.
 *
 * @return java.lang.String
*/
        public String getAlauncher() {
            return this.alauncher;
        }

/**
 * Returns the status of compatibility mode with release 16.10.
 *
 * @return boolean
*/
        public boolean getComp1610() {
            return this.comp1610;
        }


/**
 * Returns the currently configured Astrée user.
 *
 * @return java.lang.String
 */
        public String getUser() {
            return this.user;
        }

/**
 * Returns the currently configured Astrée (user) password.
 *
 * @return java.lang.String
 */
        public String getPassword() {
            return this.password;
        }
    }
}
