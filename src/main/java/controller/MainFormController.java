package controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.text.NumberFormat;
import java.util.Optional;

public class MainFormController {
    public JFXButton btnSelectFile;
    public JFXButton btnSelectDirectory;
    public Rectangle pgbContainer;
    public JFXButton btnCopy;
    public Label lblTitle;
    public Label lblProgress;
    public Label lblSize;
    public Label lblFile;
    public Label lblFolder;
    public Rectangle pgbBar;
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

    public void btnCopyOnAction(ActionEvent actionEvent) throws IOException {
        File destFile = new File(desDir, srcFile.getName());
        if(!destFile.exists()){
            destFile.createNewFile();
        }else {
            Optional<ButtonType> option = new Alert(Alert.AlertType.INFORMATION, "The file already exists, Do yo want to overwrite it?", ButtonType.YES, ButtonType.NO).showAndWait();
            if (option.get() == ButtonType.NO){
                return;
            }
        }

        var task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                FileInputStream fis = new FileInputStream(srcFile);
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                long fileSize = srcFile.length();
                int totalRead = 0;
                while(true) {
                    byte[] buffer = new byte[1024 * 10];        // 10 Kb
                    int read = bis.read(buffer);
                    totalRead += read;
                    if (read == -1) break;
                    bos.write(buffer, 0, read);
                    updateProgress(totalRead, fileSize);
                }

                updateProgress(fileSize, fileSize);

                bos.close();
                bis.close();

                return null;
            }
        };

        task.workDoneProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number prevWork, Number curWork) {
                pgbBar.setWidth(pgbContainer.getWidth() / task.getTotalWork() * curWork.doubleValue());
                lblProgress.setText("Progress: " + formatNumber(task.getProgress() * 100) + "%");
                lblSize.setText(formatNumber(task.getWorkDone() / 1024.0) + " / " + formatNumber(task.getTotalWork() / 1024.0) + " Kb");
            }
        });

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                pgbBar.setWidth(pgbContainer.getWidth());
                new Alert(Alert.AlertType.INFORMATION, "File has been copied successfully").showAndWait();
                lblFolder.setText("No folder selected");
                lblFile.setText("No file selected");
                btnCopy.setDisable(true);
                pgbBar.setWidth(0);
                lblProgress.setText("Progress: 0%");
                lblSize.setText("0 / 0 Kb");
                srcFile = null;
                desDir = null;
            }
        });

        new Thread(task).start();
    }

    private String formatNumber(double input){

        NumberFormat ni = NumberFormat.getNumberInstance();
        ni.setMinimumFractionDigits(2);
        ni.setMaximumFractionDigits(2);
        ni.setGroupingUsed(true);
        return ni.format(input);
    }
}
