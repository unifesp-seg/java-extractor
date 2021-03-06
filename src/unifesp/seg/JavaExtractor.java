package unifesp.seg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class JavaExtractor {
	
    public String outputDirPath;
    public String methodIdentifiersFileName;
    public String fieldIdentifiersFileName;
    public String varIdentifiersFileName;
    public String classIdentifiersFileName;
    public String interfaceIdentifiersFileName;
	public String commentsFileName;
    public String errorsFileName;
    public static String prefix;
    public static String fileIdPrefix;
    public static long FILE_COUNTER;
    public static long METHOD_COUNTER;
    private IInputProcessor inputProcessor;


    public JavaExtractor(String inputFilePath) {
        this.methodIdentifiersFileName = "method-ids.file";
        this.fieldIdentifiersFileName = "field-ids.file";
        this.varIdentifiersFileName = "variable-ids.file";
        this.classIdentifiersFileName = "class-ids.file";
        this.interfaceIdentifiersFileName = "interface-ids.file";
        this.commentsFileName = "comments.file";
        this.errorsFileName = "errors.file";
        JavaExtractor.prefix = this.getBaseName(inputFilePath);
        JavaExtractor.fileIdPrefix = "1";
        this.outputDirPath = JavaExtractor.prefix + "_extraction_output";
        File outDir = new File(this.outputDirPath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
    }

    private String getBaseName(String path) {
        File inputFile = new File(path);
        String fileName = inputFile.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        return fileName;
    }

    private void initializeWriters() throws IOException {
        FileWriters.methodIdentifiersFW = Util.openFile(this.outputDirPath + File.separator + this.methodIdentifiersFileName, false);
        FileWriters.fieldIdentifiersFW = Util.openFile(this.outputDirPath + File.separator + this.fieldIdentifiersFileName, false);
        FileWriters.varIdentifiersFW = Util.openFile(this.outputDirPath + File.separator + this.varIdentifiersFileName, false);
        FileWriters.classIdentifiersFW = Util.openFile(this.outputDirPath + File.separator + this.classIdentifiersFileName, false);
        FileWriters.interfaceIdentifiersFW = Util.openFile(this.outputDirPath + File.separator + this.interfaceIdentifiersFileName, false);
        FileWriters.commentsFW = Util.openFile(this.outputDirPath + File.separator + this.commentsFileName, false);
        FileWriters.errorsFW = Util.openFile(this.outputDirPath + File.separator + this.errorsFileName, false);
    }

    private void closeWriters() {
        Util.closeOutputFile(FileWriters.methodIdentifiersFW);
        Util.closeOutputFile(FileWriters.fieldIdentifiersFW);
        Util.closeOutputFile(FileWriters.varIdentifiersFW);
        Util.closeOutputFile(FileWriters.classIdentifiersFW);
        Util.closeOutputFile(FileWriters.interfaceIdentifiersFW);
        Util.closeOutputFile(FileWriters.commentsFW);
        Util.closeOutputFile(FileWriters.errorsFW);
    }

    private void handleInput(String inputMode, String filename) {
        BufferedReader br;
        try {
            this.initializeWriters();
            this.inputProcessor = new FolderInputProcessor();

            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null && line.trim().length() > 0) {
                line = line.trim();
                this.inputProcessor.processInput(line);
            }
            try {
                br.close();
            } catch (IOException e) {
                System.out.println("WARN, couldn't close inputfile: " + filename);
            }

            this.closeWriters();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


	public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0) {
            System.out.println(args[0]);
            String filename = args[0];
            String inputMode = args[1];
            JavaExtractor javaExtractor = new JavaExtractor(filename);
            System.out.println("File name: " + filename);
            javaExtractor.handleInput(inputMode, filename);
        } else {
            System.out.println("FATAL: please specify the file with list of directories!");
        }
        System.out.println("done!");
    }
}
