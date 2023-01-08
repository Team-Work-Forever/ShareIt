package shareit.repository;

import org.springframework.stereotype.Repository;

import com.opencsv.CSVWriter;

import shareit.helper.CSVSerializable;

import java.io.FileOutputStream;
import java.io.Writer;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Repository
public class ReportRepository {

    private Writer writer;

    private CSVWriter csvWriter;
    
    private Writer writer(String path) {

        Writer fileWriter = null;
        
        try {

            fileWriter = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.out.println("Vai passear");
        }

        return fileWriter;

    }

    public <T extends CSVSerializable> void writeToFile(Collection<T> collection, String[] headers, String path) throws IOException {
        
        writer = writer(path);
        csvWriter = new CSVWriter(writer, ';', '\0');

        csvWriter.writeAll(toStringArray(collection, headers));
        csvWriter.close();
        writer.close();

    }

    private static <T extends CSVSerializable> List<String[]> toStringArray(Collection<T> emps, String[] headers) {

		List<String[]> records = new ArrayList<String[]>();

		records.add(headers); // new String[] { "ID", "Name", "Age", "Country" }

		Iterator<T> it = emps.iterator();

		while (it.hasNext()) {

			T emp = it.next();

			records.add(emp.serialize());

		}

		return records;

	}

}
