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
    public FlashLogFileProperty(String customPath)
    {
        // TBD: maybe normalize the path ?
        this.customFlashLogPath = customPath;
    }

    @Extension
    public static class FlashLogFilePropertyDescriptor extends NodePropertyDescriptor
    {
        @Override
        public String getDisplayName()
        {
            return Messages.FlashLog_File_description();
        }

    }


}
