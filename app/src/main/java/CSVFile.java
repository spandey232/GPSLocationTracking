/**
 * Created by Mr on 3/28/2018.
 */
import java.io.File;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.io.Writer;
import java.text.SimpleDateFormat;
public class CSVFile extends CSVWriter {


    CSVWriter writer = new CSVWriter(new FileWriter("yourfile.csv"), '\t');
    // feed in your array (or convert your data to an array)
    String[] entries = "first#second#third".split("#");

    public CSVFile(Writer writer) throws IOException {
        super(writer);
    }


    @Override
    public void writeNext(String[] nextLine) {
        writer.writeNext(entries);
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
