package mediterranee.notifications.controller;

import mediterranee.notifications.model.RDV;
import mediterranee.notifications.pojo.ExcelParser;
import mediterranee.notifications.model.SourceFileDetails;
import mediterranee.notifications.pojo.WorkDirectoryScanner;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

@Controller
public class MainController {

    public static final String WORKING_DIR = "src/main/resources/excel-source";

    @GetMapping("/")
    public String getMain(Model model) throws IOException, InvalidFormatException {
        WorkDirectoryScanner workDirectoryScanner = new WorkDirectoryScanner();
        List<SourceFileDetails> sourceFileDetailsList = workDirectoryScanner.getSourceFileDetails();

        model.addAttribute("sourceFileDetailsList", sourceFileDetailsList);

        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes ra) throws IOException, InvalidFormatException {
        String flash;
        String flashType;
        final File folder = new File(WORKING_DIR);

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        WorkDirectoryScanner workDirectoryScanner = new WorkDirectoryScanner();
        List<SourceFileDetails> sourceFileDetailsList = workDirectoryScanner.getSourceFileDetails();

        // check if any file exist already
        if (!sourceFileDetailsList.isEmpty()) {
            flash = "Supprimez un ancien fichier avant de téléverser un nouveau";
            flashType = "danger";

            ra.addFlashAttribute("flash", flash);
            ra.addFlashAttribute("flashType", flashType);

            return "redirect:/";
        }

        // check if file was selected
        if (fileName.isEmpty()) {
            flash = "Choisissez un fichier";
            flashType = "danger";

            ra.addFlashAttribute("flash", flash);
            ra.addFlashAttribute("flashType", flashType);

            return "redirect:/";
        }

        // check if file has .xlsx extension
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            flash = "Le fichier '" + fileName + "' n'est pas un fichier du format [.xlsx]. Choisissez un fichier Microsoft Excel à partir de la version 2007";
            flashType = "danger";

            ra.addFlashAttribute("flash", flash);
            ra.addFlashAttribute("flashType", flashType);

            return "redirect:/";
        }

        // check if file is empty (not working with Excel)
        if (file.isEmpty()) {
            flash = "Le fichier '" + fileName + "' est vide. Choisissez un autre fichier";
            flashType = "danger";

            ra.addFlashAttribute("flash", flash);
            ra.addFlashAttribute("flashType", flashType);

            return "redirect:/";
        }


        // save the file on the local file system
        try {
            Path path = Paths.get(WORKING_DIR + "/" + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            ExcelParser excelParser = new ExcelParser();
            List<RDV> rdvs = excelParser.getRDVsFromExcelFile();

            System.out.println(rdvs);

        } catch (Exception e) {
            e.printStackTrace();

            // return error response
            flash = "Le fichier '" + fileName + "' est vide soit erroné!";
            flashType = "danger";

            if (e instanceof ClassCastException) {
                flash = flash + " " + e.getMessage();
            }

            Files.delete(Path.of(WORKING_DIR + "/" + fileName));

            ra.addFlashAttribute("flash", flash);
            ra.addFlashAttribute("flashType", flashType);

            return "redirect:/";
        }

        // return success response
        flash = "Le fichier '" + fileName + "' à été téléversé avec succès!";
        flashType = "success";

        ra.addFlashAttribute("flash", flash);
        ra.addFlashAttribute("flashType", flashType);

        return "redirect:/";
    }

    @RequestMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadTextFileExample1(@PathVariable("fileName") String fileName) throws FileNotFoundException {
        final File file = new File(WORKING_DIR + "/" + fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(file.length())
                .body(inputStreamResource);
    }

    @PostMapping("/delete/{fileName}")
    public String deleteSourceFile(@PathVariable("fileName") String fileName) throws IOException {

        Files.delete(Path.of(WORKING_DIR + "/" + fileName));

        return "redirect:/";
    }
}
