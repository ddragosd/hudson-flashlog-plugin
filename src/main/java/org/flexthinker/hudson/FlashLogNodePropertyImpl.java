package org.flexthinker.hudson;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;

/**
 *
 * Marker property to disable flashlog capturing on specific nodes.
 *
 * @author Dragos Dascalita Haut
 */
public class FlashLogNodePropertyImpl extends NodeProperty<Node>
{
    @DataBoundConstructor
    public FlashLogNodePropertyImpl() {}

    @Extension
    public static class FlashLogPropertyDescriptor extends NodePropertyDescriptor
    {
        @Override
        public String getDisplayName()
        {
            return Messages.FlashLog_Node_description();
        }

    }
}
