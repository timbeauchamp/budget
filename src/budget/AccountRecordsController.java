package budget;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;


public class AccountRecordsController
{
    @FXML
    private TableView dataTable;

    @FXML
    private CheckBox cvRecent;

    @FXML
    private ChoiceBox<String> choiceBoxSourceFilter;

    @FXML
    private TextField filterField;

    @FXML
    private ObservableList<AccountRecord> dataList;

    @FXML
    private void handleRecentCB()
    {

    }

    @FXML
    private void handleChoiceFilter()
    {

    }

    public AccountRecordsController()
    {

    }

    @FXML
    private void handleSaveButton()
    {
        System.out.println("Saving File");
        Main.me.saveDataFile();;
    }


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     *
     * Initializes the table columns and sets up sorting and filtering.
     */
    @FXML
    private void initialize()
    {
        System.out.println("Loading File");

        Main.me.loadDataFile();

        AccountRecordsModel dataModel = Main.me.accountDataModel;
        // Now add observability by wrapping it with ObservableList.
        dataList = FXCollections.observableList(dataModel.records);

        dataList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(Change change) {
                System.out.println("Detected a change! ");
            }
        });

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<AccountRecord> filteredData = new FilteredList<>(dataList, p -> true);//   (dataList, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(record -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (record.getSource().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<AccountRecord> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(dataTable.comparatorProperty());

//        // 5. Add sorted (and filtered) data to the table.
        dataTable.setItems(sortedData);

 //       dataTable.setItems(dataList);

        TableColumn newCol;

        newCol = new TableColumn("date");
        newCol.setCellValueFactory(new PropertyValueFactory("date"));
        newCol.setCellFactory(getCustomDateCellFactory());
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("source");
        newCol.setCellValueFactory(new PropertyValueFactory("source"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("amount");
        newCol.setCellValueFactory(new PropertyValueFactory("amount"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("balance");
        newCol.setCellValueFactory(new PropertyValueFactory("balance"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("note");
        newCol.setCellValueFactory(new PropertyValueFactory("note"));
        dataTable.getColumns().add(newCol);


        dataTable.setPrefWidth(450);
        dataTable.setPrefHeight(300);
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dataTable.getSelectionModel().selectedIndexProperty().addListener(
                new RowSelectChangeListener());

    }

    private void wraptit()
    {
//        // 0. Initialize the columns.
//        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
//        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<AccountRecord> filteredData = new FilteredList<>(dataList, p -> true);//   (dataList, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(record -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (record.getSource().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<AccountRecord> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(dataTable.comparatorProperty());

//        // 5. Add sorted (and filtered) data to the table.
        dataTable.setItems(sortedData);
    }


    @FXML
    private void handleMoveButton()
    {
        AccountRecordsModel dataModel = Main.me.accountDataModel;
        List<Record> inputList = Main.me.csvDataModel.dataList;

        dataList.clear();

        String dateStr;
        String amountStr;
        String balStr;
        String info1Str;
        String info2Str;

        AccountRecord newRecord;

        for (Record rec: inputList)
        {
            dateStr =  rec.getField(1).get();
            amountStr =  rec.getField(2).get();
            balStr =  rec.getField(3).get();
            info1Str =  rec.getField(5).get();
            info2Str =  rec.getField(6).get();
            SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/YYYY");

            Date newDate = null;

            try
            {
                newDate  = sdf.parse(dateStr);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }

            newRecord = new AccountRecord();
            double amount = parseDollars(amountStr);
            double bal = parseDollars(balStr);

            newRecord.setDate(newDate);
            newRecord.setAmount(amount);
            newRecord.setBalance(bal);
            newRecord.setNote(info1Str + " " + info2Str);
            newRecord.setSource(findSource(info1Str,info2Str));

            dataList.add(newRecord);
        }

    }

    private String findSource(String str1, String str2)
    {
        String source = null;
        String regex = null;
        ArrayList<String[]> sources = getSourceIdentificationList();



        for(String[] entry: sources )
        {
            source = null;
            regex = "";
            if(entry.length >= 2 && (str1.matches(entry[0]) || str2.matches(entry[0])))
            {
                source = entry[1];
                return source;
            }
        }

        return "unknown";
    }

    private double parseDollars(String inStr)
    {
        double retVal;
        boolean neg = inStr.startsWith("(") ? true : false;

        String newStr = inStr.replaceAll("[^\\d.]+", "");

        retVal = Double.parseDouble(newStr);
        if(neg)
        {
            retVal *= -1;
        }

        return retVal;
    }

    @FXML
    private void handleLoadButton()
    {
        System.out.println("Loading File");

        Main.me.loadDataFile();

        AccountRecordsModel dataModel = Main.me.accountDataModel;
        // Now add observability by wrapping it with ObservableList.
        dataList = FXCollections.observableList(dataModel.records);

        dataList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(Change change) {
                System.out.println("Detected a change! ");
            }
        });


        dataTable.setItems(dataList);
        dataTable.getColumns().clear();

        TableColumn newCol;

        newCol = new TableColumn("date");
        newCol.setCellValueFactory(new PropertyValueFactory("date"));
        newCol.setCellFactory(getCustomDateCellFactory());
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("source");
        newCol.setCellValueFactory(new PropertyValueFactory("source"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("amount");
        newCol.setCellValueFactory(new PropertyValueFactory("amount"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("balance");
        newCol.setCellValueFactory(new PropertyValueFactory("balance"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("note");
        newCol.setCellValueFactory(new PropertyValueFactory("note"));
        dataTable.getColumns().add(newCol);


        dataTable.setPrefWidth(450);
        dataTable.setPrefHeight(300);
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dataTable.getSelectionModel().selectedIndexProperty().addListener(
                new RowSelectChangeListener());

    }

    private Callback<TableColumn,TableCell> getCustomDateCellFactory()
    {
        return new Callback<TableColumn, TableCell>() {

            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell() {

                    @Override
                    protected void updateItem(Object item, boolean empty)
                    {
                        if (item != null)
                        {
                            final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            setText(sdf.format((Date)item));
                            setStyle("-fx-text-fill: " + "green" + ";");
                        }
                    }

                };
                return cell;
            }
        };

    }
    @FXML
    private void handleCSVButton()
    {
        System.out.println("CSV Loading File");

//        dataList  =  this.getInitialTableData();
        Main.me.loadInputFile();

        AccountRecordsModel dataModel = Main.me.accountDataModel;
        // Now add observability by wrapping it with ObservableList.
        dataList = FXCollections.observableList(dataModel.records);

        dataList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(Change change) {
                System.out.println("Detected a change! ");
            }
        });
        dataTable.setItems(dataList);
        dataTable.getColumns().clear();

//        for(int i = 0; i < dataModel.numFields; i++)
//        {
//            final int colnum = i;
//            TableColumn titleCol = new TableColumn("Col" + i);
////            titleCol.setCellValueFactory(new PropertyValueFactory("col" + i));
//            titleCol.setCellValueFactory(new PropertyValueFactory("col" + i));
//
//            titleCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
//                public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> p) {
//                    // p.getValue() returns the Record instance for a particular TableView row
//                    return p.getValue().getField(colnum);
//                }
//            });
//
//            dataTable.getColumns().add(titleCol);
//        }

        TableColumn newCol;

        newCol = new TableColumn("date");
        newCol.setCellValueFactory(new PropertyValueFactory("date"));
        newCol.setCellFactory(getCustomDateCellFactory());
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("source");
        newCol.setCellValueFactory(new PropertyValueFactory("source"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("amount");
        newCol.setCellValueFactory(new PropertyValueFactory("amount"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("balance");
        newCol.setCellValueFactory(new PropertyValueFactory("balance"));
        dataTable.getColumns().add(newCol);

        newCol = new TableColumn("note");
        newCol.setCellValueFactory(new PropertyValueFactory("note"));
        dataTable.getColumns().add(newCol);


        dataTable.setPrefWidth(450);
        dataTable.setPrefHeight(300);
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dataTable.getSelectionModel().selectedIndexProperty().addListener(
                new RowSelectChangeListener());

    }




        private class RowSelectChangeListener implements ChangeListener
    {

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue)
        {
            int ix = ((Integer)newValue).intValue();

            if ((ix < dataList.size())) {

                return; // invalid data
            }

            AccountRecord record = dataList.get(ix);
//            actionStatus.setText(book.toString());

        }
    }

    private ArrayList<String[]> getSourceIdentificationList() {

        ArrayList<String[]> list = new ArrayList<String[]>();

        list.add(new String[]{".*TIVO.*","TIVO"});
        list.add(new String[]{"Audible.*","AUDIBLE"});
        list.add(new String[]{"MOREFLAVOR.*","BREWING"});
        list.add(new String[]{".*Transfer Deposit.*","DEPOSIT"});
        list.add(new String[]{".*VIVACE.*","VIVACE"});
        list.add(new String[]{".*NETWORKSOLUTIONS.*","DOMAINS"});
        list.add(new String[]{".*ADOBE.*","ADOBE"});
        list.add(new String[]{".*CALTRAIN.*","CALTRAIN"});
        list.add(new String[]{".*ORCHARD SUPPLY.*","HOMEREPAIR"});
        list.add(new String[]{".*PAYPAL.*","PAYPAL"});
        list.add(new String[]{".*PEPBOYS.*","AUTO"});
        list.add(new String[]{".*MORE FLAVOR.*","BEER"});
        list.add(new String[]{".*HOME DEPOT.*","HOMEREREPAIR"});
        list.add(new String[]{".*ATM Withdrawal.*","ATM"});
        list.add(new String[]{".*1AND1.*","DOMAINS"});
        list.add(new String[]{".*FASTRAK.*","FASTRAK"});
        list.add(new String[]{".*AMAZON VIDEO.*","AMAZONVIDEO"});
        list.add(new String[]{".*Deposit Square.*","SQUARE"});
        list.add(new String[]{".*HALF MOON BAY BREWING.*","DINING"});
        list.add(new String[]{".*STATE FARM.*","STATEFARM"});
        list.add(new String[]{".*ONAIR INTERNET.*","INTERNET"});
        list.add(new String[]{".*Beta Theta Pi.*","FRAT"});
        list.add(new String[]{".*STARBUCKS.*","COFFEE"});
        list.add(new String[]{".*SAFEWAY.*","GROCERIES"});
        list.add(new String[]{".*SHELL.*","GASOLINE"});
        list.add(new String[]{".*CHEVRON.*","GASOLINE"});
        list.add(new String[]{".*AMEX.*","AMEX"});
        list.add(new String[]{".*PEET.*","COFFEE"});
        list.add(new String[]{".*ARCO.*","GASOLINE"});
        list.add(new String[]{".*BETATHETAPI.*","FRAT"});
        list.add(new String[]{".*AAA.*","AUTO"});
        list.add(new String[]{".*Norton.*","NORTON"});
        list.add(new String[]{".*ACE HARDWARE.*","HOME"});
        list.add(new String[]{".*TAP PLASTICS.*","HOME"});
        list.add(new String[]{".*ALE ARESENAL.*","DINING"});
        list.add(new String[]{".*TARGET.*","HOME"});
        list.add(new String[]{".*SMART CENTER.*","AUTO"});
        list.add(new String[]{".*SOUTH BAY RECYCLING.*","HOME"});
        list.add(new String[]{".*OREILLY AUTO PARTS.*","AUTO"});
        list.add(new String[]{".*ORIGINAL GRAVITY.*","DINING"});
        list.add(new String[]{".*UCLA.*","UCLA"});
        list.add(new String[]{".*ADOBE.*","DINING"});
        list.add(new String[]{".*GOGOAIR.*","INTERNET"});
        list.add(new String[]{".*Check Withdrawal.*","CHECK"});

        list.add(new String[]{".*ALASKA.*","ALASKA"});
        list.add(new String[]{".*(?i)AMAZON.*","AMAZON"});
        list.add(new String[]{".*UNITED.*","UNITED"});
        list.add(new String[]{".*GOGO.*","INTERNET"});
        list.add(new String[]{".*ACH Deposit.*","DEPOSIT"});
        list.add(new String[]{".*ACH Withdrawal .*","WITHDRAWL"});

        list.add(new String[]{".*(?i)BEST BUY.*","BESTBUY"});
        list.add(new String[]{".*(?i)COSTCO.*","COSTCO"});
        list.add(new String[]{".*(?i)DELMAS.*","GROCERIES"});
        list.add(new String[]{".*(?i)BEVERAGES.*","GROCERIES"});
        list.add(new String[]{".*(?i)CITY OF.*","CITY"});
        list.add(new String[]{".*CVS.*","CVS"});
        list.add(new String[]{".*GAMMA NU.*","FRAT"});
        list.add(new String[]{".*(?i)Brewery.*","DINNING"});
        list.add(new String[]{".*Paid NSF Fee.*","DINNING"});


        return list;
    }

}
