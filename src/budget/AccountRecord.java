package budget;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tbeauch on 1/22/17.
 */

public class AccountRecord
{
    private ArrayList<SimpleStringProperty> record;
    private SimpleObjectProperty<Date> date;
    private SimpleDoubleProperty amount;
    private SimpleStringProperty source;
    private SimpleDoubleProperty balance;

// SimpleObjectProperty<LocalDate> dateAssignedProperty = new SimpleObjectProperty<LocalDate>();


    public AccountRecord()
    {
        this.date = new SimpleObjectProperty<Date>(new Date());
        this.amount = new SimpleDoubleProperty(123.45);
        this.source = new SimpleStringProperty("the source");
        this.balance = new SimpleDoubleProperty(456.23);
        this.note = new SimpleStringProperty("note");
    }

    public String getNote()
    {
        return note.get();
    }

    public SimpleStringProperty noteProperty()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note.set(note);
    }

    private SimpleStringProperty note;

    public Double getAmount()
    {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount.set(amount);
    }

    public String getSource()
    {
        return source.get();
    }

    public SimpleStringProperty sourceProperty()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source.set(source);
    }

    public Double getBalance()
    {
        return balance.get();
    }

    public SimpleDoubleProperty balanceProperty()
    {
        return balance;
    }

    public void setBalance(Double balance)
    {
        this.balance.set(balance);
    }

    public Date getDate()
    {
        return date.get();
    }

    public SimpleObjectProperty<Date> dateProperty()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date.set(date);
    }

    public AccountRecord(int numCols, String prefix)
    {
        record = new ArrayList<SimpleStringProperty>();
        for(int i=0;i<numCols;i++)
        {
            record.add(new SimpleStringProperty(prefix + i));
        }
    }

    public AccountRecord(int numCols)
    {
        record = new ArrayList<SimpleStringProperty>();
        for(int i=0;i<numCols;i++)
        {
            record.add(new SimpleStringProperty("Field " + i));
        }
    }

    public SimpleStringProperty getDate(final int col)
    {

        return record.get(col);
    }


    @Override
    public String toString()
    {
        return (date.toString() + amount.toString() + source + balance.toString() + note);
    }
}