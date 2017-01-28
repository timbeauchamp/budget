package budget;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

/**
 * Created by tbeauch on 1/22/17.
 */

public class Record
{
    private ArrayList<SimpleStringProperty> record;

    public Record (int numCols, String prefix)
    {
        record = new ArrayList<SimpleStringProperty>();
        for(int i=0;i<numCols;i++)
        {
            record.add(new SimpleStringProperty(prefix + i));
        }
    }

    public Record (int numCols)
    {
        record = new ArrayList<SimpleStringProperty>();
        for(int i=0;i<numCols;i++)
        {
            record.add(new SimpleStringProperty("Field " + i));
        }
    }

    public SimpleStringProperty getField(final int col)
    {

        return record.get(col);
    }

    public void setField(int col, String s)
    {

        record.get(col).set(s);
    }

    @Override
    public String toString()
    {
        return ("Record number unknown");
    }
}