package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.NumberFormat;

public class MainFormController {
    public JFXButton btnSelectFile;
    public JFXButton btnSelectDirectory;
    public Rectangle pgbContainer;
    public Rectangle gbBar;
    public JFXButton btnCopy;
    public Label lblTitle;
    public Label lblProgress;
    public Label lblSize;
    public Label lblFile;
    public Label lblFolder;
    private File srcFile;
    private File desDir;


    public void initialize(){
        btnCopy.setDisable(true);
    }

    public void btnSelectFileOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Select a file to copy...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files (*.*), ","*.*"));

        srcFile = fileChooser.showOpenDialog(lblFile.getScene().getWindow());

        if(srcFile != null){
            lblFile.setText(srcFile.getName() + " : " + formatNumber(srcFile.length() / 1024.0) + "Kb");
        }else {
            lblFile.setText("No file selected");
        }

        btnCopy.setDisable(srcFile == null || desDir == null);
    }

    public void btnSelectDirectoryOnAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        directoryChooser.setTitle("Select a destination folder");

        desDir = directoryChooser.showDialog(lblFile.getScene().getWindow());

        if (desDir != null){
            lblFolder.setText(desDir.getAbsolutePath());
        }else{
            lblFolder.setText("No folder selected");
        }

        btnCopy.setDisable(srcFile == null || desDir == null);

    }

    public void btnCopyOnAction(ActionEvent actionEvent) {
    }

    private String formatNumber(double input){

        NumberFormat ni = NumberFormat.getNumberInstance();
        ni.setMinimumFractionDigits(2);
        ni.setMaximumFractionDigits(2);
        ni.setGroupingUsed(true);
        return ni.format(input);
    }
}
