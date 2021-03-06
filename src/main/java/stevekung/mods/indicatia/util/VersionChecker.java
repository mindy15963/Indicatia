package stevekung.mods.indicatia.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import stevekung.mods.indicatia.core.IndicatiaMod;

public class VersionChecker implements Runnable
{
    private static boolean LATEST;
    private static boolean NO_CONNECTION;
    private static String LATEST_VERSION;
    private static String EXCEPTION_MESSAGE;
    private static List<String> ANNOUNCE_MESSAGE = new ArrayList<>();
    public static final VersionChecker INSTANCE = new VersionChecker();

    public static void startCheck()
    {
        Thread thread = new Thread(VersionChecker.INSTANCE);
        thread.start();
    }

    @Override
    public void run()
    {
        InputStream version = null;
        InputStream desc = null;
        String latest = null;
        int major = 0, minor = 0, build = 0;

        try
        {
            version = new URL("https://raw.githubusercontent.com/SteveKunG/VersionCheckLibrary/master/indicatia/indicatia_version.txt").openStream();
            desc = new URL("https://raw.githubusercontent.com/SteveKunG/VersionCheckLibrary/master/indicatia/indicatia_desc.txt").openStream();
        }
        catch (MalformedURLException e)
        {
            VersionChecker.EXCEPTION_MESSAGE = e.getClass().getName() + " " + e.getMessage();
            e.printStackTrace();
        }
        catch (UnknownHostException e)
        {
            VersionChecker.EXCEPTION_MESSAGE = e.getClass().getName() + " " + e.getMessage();
            e.printStackTrace();
        }
        catch (Exception e)
        {
            VersionChecker.EXCEPTION_MESSAGE = e.getClass().getName() + " " + e.getMessage();
            e.printStackTrace();
        }

        if (version == null && desc == null)
        {
            VersionChecker.NO_CONNECTION = true;
            return;
        }

        try
        {
            for (EnumMCVersion mcVersion : EnumMCVersion.valuesCached())
            {
                for (String enumVersion : mcVersion.getVersion().split(" "))
                {
                    if (IndicatiaMod.MC_VERSION.contains(enumVersion))
                    {
                        latest = IOUtils.readLines(version, StandardCharsets.UTF_8).get(mcVersion.ordinal());
                    }
                }
            }
        }
        catch (Exception e) {}
        finally
        {
            IOUtils.closeQuietly(version);
        }

        try
        {
            VersionChecker.ANNOUNCE_MESSAGE = IOUtils.readLines(desc, StandardCharsets.UTF_8);
        }
        catch (Exception e) {}
        finally
        {
            IOUtils.closeQuietly(desc);
        }

        for (String latestVersion : latest.split(" "))
        {
            if (latestVersion.contains(IndicatiaMod.MC_VERSION))
            {
                latestVersion = latest.substring(latest.indexOf("=")).replace("=", "").replace(".", "");

                try
                {
                    major = Integer.parseInt(String.valueOf(latestVersion.charAt(0)));
                    minor = Integer.parseInt(String.valueOf(latestVersion.charAt(1)));
                    build = Integer.parseInt(String.valueOf(latestVersion.charAt(2)));
                }
                catch (Exception e) {}
            }
        }
        String latestVersion = major + "." + minor + "." + build;
        VersionChecker.LATEST_VERSION = latestVersion;
        VersionChecker.LATEST = !IndicatiaMod.VERSION.equals(latestVersion) && (major > IndicatiaMod.MAJOR_VERSION || minor > IndicatiaMod.MINOR_VERSION || build > IndicatiaMod.BUILD_VERSION);
    }

    public boolean isLatestVersion()
    {
        return VersionChecker.LATEST;
    }

    public boolean noConnection()
    {
        return VersionChecker.NO_CONNECTION;
    }

    public String getLatestVersion()
    {
        return VersionChecker.LATEST_VERSION;
    }

    public String getExceptionMessage()
    {
        return VersionChecker.EXCEPTION_MESSAGE;
    }

    public List<String> getAnnounceMessage()
    {
        return VersionChecker.ANNOUNCE_MESSAGE;
    }
}