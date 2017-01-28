package budget;

import com.opencsv.CSVReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.json.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main extends Application
{
    public static Main me;
    public AccountRecordsModel accountDataModel;
    public CSVDataModel csvDataModel;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
//        BudgetJSONReader.doit();
        me = this;
        accountDataModel = new AccountRecordsModel();

        Parent root = FXMLLoader.load(getClass().getResource("AccountRecords.fxml"));
        primaryStage.setTitle("Budget");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public void loadDataFile()
    {
        try
        {
            BudgetJSONReader();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void saveDataFile()
    {
        try
        {
            BudgetJSONWriter(accountDataModel);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }


    public void loadInputFile()
    {
        boolean firstRowIsHeaders = true;

        try
        {
            // Iterator style pattern
//            java.io.File temp =  new java.io.File("input.csv");
//            String path = temp.getAbsolutePath();

            CSVReader reader = new CSVReader(new FileReader("input.csv"));
//            String [] nextLine;
//            while ((nextLine = reader.readNext()) != null)
//            {
//                // nextLine[] is an array of values from the line
//                System.out.println(nextLine[0] + nextLine[1] + "etc...");
//            }

            // Or, if you might just want to slurp the whole lot into a List, just call readAll()...

            List <String[]> myEntries = reader.readAll();
            Object entry = myEntries.get(0);

            int numLines = 0, numFields = 0;

            for (String [] nextLine : myEntries)
            {
                if(nextLine.length > 0)
                {
                    numLines++;
                }
                if(nextLine.length > numFields)
                {
                    numFields = nextLine.length;
                }
            }


            csvDataModel = new CSVDataModel(numFields);

            csvDataModel.data = myEntries;
            csvDataModel.numRecords = numLines;
            csvDataModel.colNames = new ArrayList<String>();
            for(int i = 0; i < numFields; i++)
            {
                csvDataModel.colNames.add("Field " + i);
            }

            int row = 0;
            int col = 0;
            Record currentRecord;
            String field;

            for (String [] nextLine : myEntries)
            {
                col = 0;
                currentRecord = new Record(numFields);
                for(int i=0; i < numFields; i++)
                {
                    field =  nextLine[i];
                    currentRecord.setField(i,field);
                    col++;
                }

                if(firstRowIsHeaders && row == 0)
                {
                    // skipping first row
                }
                else
                {
                    csvDataModel.dataList.add(currentRecord);
                }

                row++;
            }

            System.out.println("Number of Lines:" + numLines + "    Number of Fields:" + numFields);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static final String JSON_FILE="budget.json";

    public void BudgetJSONReader() throws IOException
    {
            InputStream fis = new FileInputStream(JSON_FILE);

        //create JsonReader object
        JsonReader jsonReader = Json.createReader(fis);

        /**
         * We can create JsonReader from Factory also
         JsonReaderFactory factory = Json.createReaderFactory(null);
         jsonReader = factory.createReader(fis);
         */

        //get JsonObject from JsonReader
        JsonObject jsonObject = jsonReader.readObject();

        //we can close IO resource and JsonReader now
        jsonReader.close();
        fis.close();

        accountDataModel = new AccountRecordsModel();



        //Retrieve data from JsonObject and create Employee bean
        AccountRecord rec;
        JsonObject recordObject;
        JsonArray jsonArray = jsonObject.getJsonArray("records");

        accountDataModel.numRecords = jsonArray.size();

        accountDataModel.colNames = new ArrayList<String>();
        accountDataModel.colNames.add("Date");
        accountDataModel.colNames.add("Source");
        accountDataModel.colNames.add("Amount");
        accountDataModel.colNames.add("Balance");
        accountDataModel.colNames.add("Note");
        accountDataModel.numFields = accountDataModel.colNames.size();

        for(JsonValue record : jsonArray)
        {
            rec = new AccountRecord();
            if(record.getValueType() != JsonValue.ValueType.OBJECT)
            {
                continue;
            }
            recordObject = (JsonObject) record;
            String dateStr = recordObject.getString("date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date recordDate;

            try
            {
                recordDate = sdf.parse(dateStr);
                rec.setDate(recordDate);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }

            rec.setAmount(recordObject.getJsonNumber("amount").doubleValue());
            rec.setSource(recordObject.getString("source"));
            rec.setNote(recordObject.getString("note"));
            rec.setBalance(recordObject.getJsonNumber("balance").doubleValue());

            accountDataModel.records.add(rec);
            //print employee bean information
            System.out.println(rec);
        }
    }


    public void BudgetJSONWriter(AccountRecordsModel model) throws FileNotFoundException
    {
        JsonObjectBuilder recordBuilder;
        JsonObjectBuilder recordsBuilder = Json.createObjectBuilder();
        JsonArrayBuilder recordsArrayBuilder = Json.createArrayBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(AccountRecord record : model.records)
        {
            recordBuilder = Json.createObjectBuilder();

            recordBuilder.add("date", sdf.format(record.getDate()))
                    .add("amount", record.getAmount())
                    .add("source", record.getSource())
                    .add("balance", record.getBalance())
                    .add("note", record.getNote());
//            JsonObject jsonRecord = recordBuilder.build();
            recordsArrayBuilder.add(recordBuilder);
        }

        recordsBuilder.add("records",recordsArrayBuilder);

        //write to file
        OutputStream os = new FileOutputStream(JSON_FILE);

//         JsonWriterFactory factory = Json.createWriterFactory()   .createWriterFactory  (null);

        Map<String, Object> config = new HashMap<String, Object>();
        //if you need pretty printing
        config.put("javax.json.stream.JsonGenerator.prettyPrinting", Boolean.valueOf(true));
        JsonWriterFactory factory = Json.createWriterFactory(config);

//        JsonWriter jsonWriter = Json.createWriter(os);
        JsonWriter jsonWriter = factory.createWriter(os);

        jsonWriter.writeObject(recordsBuilder.build());
        jsonWriter.close();
    }

}