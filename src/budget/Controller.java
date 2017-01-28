package budget;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.*;


public class Controller
{
    @FXML
    private TableView dataTable;

    private ObservableList<Record> dataList;

    @FXML
    private void handleLoadButton()
    {
        System.out.println("Loading File");

        Main.me.loadInputFile();

        CSVDataModel dataModel = Main.me.csvDataModel;
        // Now add observability by wrapping it with ObservableList.
        dataList = FXCollections.observableList(dataModel.dataList);

        dataList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change) {
                System.out.println("Detected a change! ");
            }
        });



        dataTable.setItems(dataList);
        dataTable.getColumns().clear();

        for(int i = 0; i < dataModel.numFields; i++)
        {
            final int colnum = i;
            TableColumn titleCol = new TableColumn("Col" + i);
//            titleCol.setCellValueFactory(new PropertyValueFactory("col" + i));
            titleCol.setCellValueFactory(new PropertyValueFactory("col" + i));

            titleCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> p) {
                    // p.getValue() returns the Record instance for a particular TableView row
                    return p.getValue().getField(colnum);
                }
            });

            dataTable.getColumns().add(titleCol);
        }

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

            Record record = dataList.get(ix);
//            actionStatus.setText(book.toString());

        }
    }

    private ObservableList getInitialTableData() {

        List list = new ArrayList();
        list.add(new Record(8, "The Thief"));
        list.add(new Record(8, "Of Human Bondage"));
        list.add(new Record(8, "The Bluest Eye"));
        list.add(new Record(8, "I Am Ok You Are Ok"));
        list.add(new Record(8, "Magnificent Obsession"));
        list.add(new Record(8, "100 Years of Solitude"));
        list.add(new Record(8, "What the Dog Saw"));
        list.add(new Record(8, "The Fakir"));
        list.add(new Record(8, "The Hobbit"));
        list.add(new Record(8, "Strange Life of Ivan Osokin"));
        list.add(new Record(8, "The Hunt for Red October"));
        list.add(new Record(8, "Coma"));

        ObservableList data = FXCollections.observableList(list);

        return data;
    }

}
