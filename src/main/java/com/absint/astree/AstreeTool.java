package com.absint.astree;

import com.absint.astree.AstreeReportParser;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.DataBoundConstructor;

import io.jenkins.plugins.analysis.core.model.ReportScanningTool;

import hudson.Extension;

/**
 * Tool registration to be picked up by warnings pluging
 */
public class AstreeTool extends ReportScanningTool {
    private static final long serialVersionUID = 1L;
    private final static String PLUGIN_ID = "absint-astree"; 

    /** Creates a new instance of {@link AstreeTool}. */
    @DataBoundConstructor
    public AstreeTool() {
        super();
        // empty constructor required for stapler
    }

    @Override
    public AstreeReportParser createParser() {
        return new AstreeReportParser();
    }

    /** Descriptor for this static analysis tool. */
    @Extension
    public static class Descriptor extends ReportScanningToolDescriptor {
        /** Creates the descriptor instance. */
        public Descriptor() {
            super(PLUGIN_ID);
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "AbsInt Astrée Report Parser";
        }
    }
}
