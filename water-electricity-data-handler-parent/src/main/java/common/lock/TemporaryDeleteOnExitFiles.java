package common.lock;

import common.logger.LogCategory;
import common.logger.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TemporaryDeleteOnExitFiles {
    public static List<String> currentFiles = new CopyOnWriteArrayList<>();

    public static void addFile(String fileName) {
        Logger logger = Logger.getLogger(TemporaryDeleteOnExitFiles.class.toString(), "addFile");
        if (fileName == null || fileName.isEmpty() || currentFiles.contains(fileName)) {
            return;
        }
        currentFiles.add(fileName);
        logger.log(LogCategory.INFO, "Adding file with name = '" + fileName + "'");
    }

    public static void removeFile(String fileName) {
        Logger logger = Logger.getLogger(TemporaryDeleteOnExitFiles.class.toString(), "removeFile");
        if (fileName != null && !fileName.isEmpty()) {
            currentFiles.remove(fileName);
            logger.log(LogCategory.INFO, "Removing lock file with name = '" + fileName + "'");
        }

    }
}