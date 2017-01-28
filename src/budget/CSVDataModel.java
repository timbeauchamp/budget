package budget;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tbeauch on 1/22/17.
 */
public class CSVDataModel
{
    public int numFields = 0;
    public int numRecords = 0;
    public Date startDate;
    public Date endDate;
    public ArrayList<String> colNames;
    public List<String[]> data;
    public List<Record> dataList;

    public CSVDataModel(int numFields)
    {
        this.numFields = numFields;
        dataList = new ArrayList<Record>();
    }
}
