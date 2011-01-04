package org.flexthinker.hudson;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

/**
 * This plugin enables you to capture Flash Player logs that were recorded during a build.
 *
 * @author Dragos Dascalita Haut
 */
public class FlashLog extends BuildWrapper
{
    /**
     * The name of the file that is going to be stored in the artifacts dir,
     * containing the flash log recorded during build.
     */
    private static final String LOG_FILENAME = "flashlog.txt";

    @DataBoundConstructor
    public FlashLog()
    {
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener)
            throws IOException, InterruptedException
    {
        final PrintStream logger = listener.getLogger();

        logger.println("[FlashLog] [INFO] setting up ... ");


        // skip execution if this node is disabled
        if (Hudson.getInstance().getGlobalNodeProperties().get(FlashLogNodePropertyImpl.class) != null)
        {
            logger.println("[FlashLog] [INFO] skipping execution as FlashLog is not enabled on this node.");
            return new Environment()
            {
            };
        }

        DescriptorImpl DESCRIPTOR = Hudson.getInstance().getDescriptorByType(DescriptorImpl.class);
        String tempPath = DESCRIPTOR.getFlashlogPath();

        FlashLogFileProperty logFileProperty = Hudson.getInstance().getGlobalNodeProperties().get(FlashLogFileProperty.class);

        if ( logFileProperty != null)
        {
            File logF = new File(logFileProperty.customFlashLogPath);
            if (logF.exists())
            {
                tempPath = logFileProperty.customFlashLogPath;
            }
            else
            {
                logger.println("[FlashLog] [WARNING] The given logfile doesn't exists : " + logFileProperty.customFlashLogPath +
                        " ! The default path will be used.");
            }
        }



        final String flashlogPath = tempPath;


        createNewFlashLog(flashlogPath, build, listener);

        return new Environment()
        {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener)
                    throws IOException, InterruptedException
            {
                return restoreOriginalFlashLog(flashlogPath, build, listener);
            }

        };

    }

    private boolean createNewFlashLog(String flashlogPath, AbstractBuild build, BuildListener listener)
            throws IOException, InterruptedException
    {
        //1. backup current logs
        FilePath logFile = new FilePath(new File(flashlogPath));
        if (logFile.exists())
        {
            logFile.copyTo(new FilePath(new File(flashlogPath + ".old".toString())));
        }

        //2. create a clean log file to store logs during build
        logFile.write("FLASH LOG FOR BUILD:" + build.getDisplayName() + "\n", null);

        listener.getLogger().println("[FlashLog] [INFO] Listening for flash logs from: " + logFile.toURI());

        return true;
    }

    private boolean restoreOriginalFlashLog(String flashlogPath, AbstractBuild build, BuildListener listener)
            throws IOException, InterruptedException
    {
        //1. copy logs into build artifacts
        FilePath logFile = new FilePath(new File(flashlogPath));

        FilePath ws = build.getWorkspace();
        File artifactsDir = build.getArtifactsDir();
        artifactsDir.mkdirs();

        listener.getLogger().println("[FlashLog] [INFO] Saving flash log into artifacts dir, in " + artifactsDir.getAbsolutePath());
        if (logFile.exists())
        {
            logFile.copyTo(new FilePath(artifactsDir).child(LOG_FILENAME));
        }


        //2. restore back the original flashlog.txt
        FilePath oldLogFile = new FilePath(new File(flashlogPath + ".old".toString()));
        if (oldLogFile.exists())
        {
            oldLogFile.copyTo(logFile);
        }
        return true;
    }


    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor
    {
        private static final String DEFAULT_LINUX_FLASH_LOG =
                "~/.macromedia/Flash_Player/Logs/flashlog.txt";
        private static final String DEFAULT_OSX_FLASH_LOG =
                "~/Library/Preferences/Macromedia/Flash Player/Logs/flashlog.txt";
        private static final String DEFAULT_WIN_XP_FLASH_LOG =
                "~\\Application Data\\Macromedia\\Flash Player\\Logs\\flashlog.txt";
        private static final String DEFAULT_WIN_VISTA_FLASH_LOG =
                "~\\AppData\\Roaming\\Macromedia\\Flash Player\\Logs\\flashlog.txt";

        private String flashlogPath = DEFAULT_LINUX_FLASH_LOG;

        public DescriptorImpl()
        {
            super(FlashLog.class);
            load();
        }

        @Override
        public String getDisplayName()
        {
            return "This build generates Flash/Flex apps and I want to capture flash player log during build";
        }


        @Override
        public boolean isApplicable(AbstractProject<?, ?> item)
        {
            return true;
        }

        public String getFlashlogPath()
        {
            String OS = System.getProperty("os.name").toLowerCase();
            if (OS.indexOf("xp") >= 0)
            {
                flashlogPath = DEFAULT_WIN_XP_FLASH_LOG;
            }
            else if (OS.indexOf("vista") >= 0)
            {
                flashlogPath = DEFAULT_WIN_VISTA_FLASH_LOG;
            }
            else if (OS.indexOf("mac") >= 0)
            {
                flashlogPath = DEFAULT_OSX_FLASH_LOG;
            }

            flashlogPath = flashlogPath.replaceFirst("~", System.getProperty("user.home"));

            return flashlogPath;
        }


    }


}
