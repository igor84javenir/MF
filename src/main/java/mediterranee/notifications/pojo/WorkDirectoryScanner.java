package mediterranee.notifications.pojo;

import mediterranee.notifications.model.SourceFileDetails;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static mediterranee.notifications.controller.MainController.WORKING_DIR;

public class WorkDirectoryScanner {

    public List<SourceFileDetails> getSourceFileDetails() throws IOException {
        List<SourceFileDetails> sourceFileDetailsList = new ArrayList<>();

        List<File> files = getFilesInWorkingDirectory();

        for (File file : files) {
            SourceFileDetails sourceFileDetails = new SourceFileDetails();
            sourceFileDetails.setName(file.getName());

            Instant modified = Files
                    .getLastModifiedTime(Paths.get(WORKING_DIR, file.getName()))
                    .toInstant();
            LocalDateTime modifiedDateTime = modified
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH'h'mm");

            String formattedDateTimeAsString = modifiedDateTime.format(formatter);

            sourceFileDetails.setDateAsString(formattedDateTimeAsString);
            sourceFileDetailsList.add(sourceFileDetails);

        }

        return sourceFileDetailsList;
    }

    private List<File> getFilesInWorkingDirectory() throws FileNotFoundException {
        final File folder = new File(WORKING_DIR);
        List<File> files = new ArrayList<>();

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry);
            }
        }

        return files;
    }
}
