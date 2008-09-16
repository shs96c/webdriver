package org.openqa.selenium.chrome.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.internal.OperatingSystem;

public class ChromeBinary {
    private final File actualBinary;
    private Process process;

    public ChromeBinary() {
        this(null);
    }

    public ChromeBinary(File actualBinary) {
        this.actualBinary = locateChromeBinary(actualBinary);
    }

    protected File locateChromeBinary(File suggestedLocation) {
        if (suggestedLocation != null) {
            if (suggestedLocation.exists() && suggestedLocation.isFile())
                return suggestedLocation;
            else
                throw new RuntimeException("Given Chrome binary location does not exist or is not a real file: " + suggestedLocation);
        }

        File binary = locateChromeBinaryFromSystemProperty();
        if (binary != null)
            return binary;

        OperatingSystem os = OperatingSystem.getCurrentPlatform();
        switch (os) {
            case WINDOWS:
                String userProfile = System.getenv("USERPROFILE");
                if (userProfile == null) {
                    String userName = System.getenv("USERNAME");
                    if (userName == null)
                        userName = "All Users";
                    userProfile = "\\Documents and Settings\\" + userName;
                }
                binary = new File(userProfile + "\\Local Settings\\Application Data\\Google\\Chrome\\Application\\chrome.exe");
                break;

            default:
                throw new UnsupportedOperationException("Only Windows is supported on the ChromeDriver at this time.");
        }

        if (binary == null) {
            throw new RuntimeException("Cannot find Chrome binary in PATH. Make sure Chrome is installed");
        }

        if (binary.exists())
            return binary;

        throw new RuntimeException("Unable to locate Chrome binary. Please check that it is installed in the default location, " +
                "or the path given points to the Chrome binary. I would have used: " + binary.getPath());
    }

    protected File locateChromeBinaryFromSystemProperty() {
        String binaryName = System.getProperty("webdriver.chrome.bin");
        if (binaryName == null)
            return null;

        File binary = new File(binaryName);
        if (binary.exists())
            return binary;

        return null;
    }

    public void start(String... commandLineFlags) throws IOException {
        List<String> commands = new ArrayList<String>();
        commands.add(actualBinary.getAbsolutePath());
        commands.addAll(Arrays.asList(commandLineFlags));
        ProcessBuilder builder = new ProcessBuilder(commands);
        modifyLibraryPath(builder);
        process = builder.start();
    }

    public void kill() {
        process.destroy();
    }

    //TODO: copied from the Firefox launcher: need to remove unnecessary code
    protected void modifyLibraryPath(ProcessBuilder builder) {
        String propertyName;

        OperatingSystem os = OperatingSystem.getCurrentPlatform();
        switch (os) {
            case MAC:
                propertyName = "DYLD_LIBRARY_PATH";
                break;

            case WINDOWS:
                propertyName = "PATH";
                break;

            default:
                propertyName = "LD_LIBRARY_PATH";
                break;
        }

        String libraryPath = System.getenv(propertyName);
        if (libraryPath == null) {
            libraryPath = "";
        }

        String chromeLibraryPath = System.getProperty("webdriver.chrome.library.path", actualBinary.getParentFile().getAbsolutePath());

        libraryPath = chromeLibraryPath + File.pathSeparator + libraryPath;

        builder.environment().put(propertyName, libraryPath);
    }
}
