package org.flexthinker.hudson;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;

/**
 * Marker property to enable a custom location for flashlog.txt
 * @author Dragos Dascalita Haut
 */
public class FlashLogFileProperty extends NodeProperty<Node>
{
    public String customFlashLogPath;

    @DataBoundConstructor
    public FlashLogFileProperty() {}

    @Extension
    public static class DescriptorImpl extends NodePropertyDescriptor
    {
        @Override
        public String getDisplayName()
        {
            return "Specify a custom path to locate Flash Player's log";
        }

    }


}
