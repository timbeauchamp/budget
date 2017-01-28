package budget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tbeauch on 1/22/17.
 */
public class AccountRecordsModel
{
    public int numFields = 0;
    public int numRecords = 0;
    public Date startDate;
    public Date endDate;
    public ArrayList<String> colNames;
    public List<String[]> data;
    public List<AccountRecord> records;

    public AccountRecordsModel()
    {
        records = new ArrayList<AccountRecord>();
    }
}
