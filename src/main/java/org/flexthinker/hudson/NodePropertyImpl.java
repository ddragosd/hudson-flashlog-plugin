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
public class NodePropertyImpl extends NodeProperty<Node>
{
    @DataBoundConstructor
    public NodePropertyImpl() {}

    @Extension
    public static class DescriptorImpl extends NodePropertyDescriptor
    {
        @Override
        public String getDisplayName()
        {
            return "Don't save flashlog on this node";
        }

    }
}
