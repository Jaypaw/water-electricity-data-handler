package controller;

import com.sun.javafx.collections.ObservableListWrapper;
import common.DataType;
import common.ServerFilesUtils;
import gui.window.DeleteRegionFromServerFileWindow;
import handling.XlsFileHandler;
import handling.util.HandlingType;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.List;


public class DeleteRegionFromServerFileWindowController extends BaseWindowController<DeleteRegionFromServerFileWindow> {
    private static final String SELECT_DELETING_REGION = "Выберите удаляемый регион";
    private static final String ERROR_GETTING_REGIONS = "Не удалось получить список регионов";
    private static final String GETTING_REGIONS_PLEASE_WAIT_TEXT = "Получаем список регионов. Пожалуйста, подождите";
    private static final String DELETING_REGION_PLEASE_WAIT_TEXT = "Удаляем регион. Пожалуйста, подождите";
    private static final String SUCCESSFUL_REGION_DELETING_TEXT = "Выбранный регион был удалён из файла";
    private static final String FAILED_REGION_DELETING = "Не удалось удалить регион из файла";

    private Integer selectedRegion;
    private List<Integer> loadedRegions;

    @Getter
    @Setter
    private MainWindowController mainWindowController;


    private void disableWindowElements() {
        window.getRegionsComboBox().setDisable(true);
        window.getDeleteRegionButton().setDisable(true);
    }

    private void enableWindowElements() {
        window.getRegionsComboBox().setDisable(false);
        window.getDeleteRegionButton().setDisable(false);
    }

    private void showLongTaskInfo(String info) {
        window.setCurrentTaskInfo(info);
        window.showProgressBar();
    }


    public void processDeleteRegionButtonClick(MouseEvent mouseEvent) {
        if (selectedRegion == null) {
            window.setCurrentTaskInfo(SELECT_DELETING_REGION);
            return;
        }
        showLongTaskInfo(DELETING_REGION_PLEASE_WAIT_TEXT);
        disableWindowElements();

        val task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                val serverFileName = mainWindowController.getServerFileName();
                val xlsFileHandler = new XlsFileHandler(selectedRegion,
                        serverFileName);
                val fileType = xlsFileHandler.getHeadlineFromServerFile(serverFileName);
                if (fileType == null) {
                    xlsFileHandler.getErrorsArray().add("FAILED_REGION_DELETION");
                } else if (fileType.equals(DataType.WATER)) {
                    xlsFileHandler.processWaterFileHandling(HandlingType.DELETE_REGION);
                } else if (fileType.equals(DataType.ELECTRICITY)) {
                    xlsFileHandler.processElectricityFileHandling(HandlingType.DELETE_REGION);
                }
                return null;
            }
        };
        task.setOnSucceeded(workerStateEvent -> {
            mainWindowController.showSuccessWindow(SUCCESSFUL_REGION_DELETING_TEXT);
            window.getStage().close();
        });
        task.setOnFailed(workerStateEvent -> {
            mainWindowController.showErrorWindow(FAILED_REGION_DELETING);
            window.getStage().close();
        });
        new Thread(task).start();

    }

    public void processRegionsComboBoxValueChanging(ObservableValue<? extends Integer> observable, Integer oldValue,
            Integer newValue) {
        selectedRegion = newValue;
    }

    public void processRegionsComboBoxClick(MouseEvent mouseEvent) {
        showLongTaskInfo(GETTING_REGIONS_PLEASE_WAIT_TEXT);
        val regionsComboBox = window.getRegionsComboBox();
        regionsComboBox.hide();
        disableWindowElements();
        val task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String serverFileName = mainWindowController.getServerFileName();
                loadedRegions = ServerFilesUtils.getRegions(serverFileName);
                return null;
            }
        };
        task.setOnSucceeded(workerStateEvent -> {
            regionsComboBox.setItems(new ObservableListWrapper<>(loadedRegions));
            enableWindowElements();
            window.hideProgressBar();
            window.setCurrentTaskInfo("");
            regionsComboBox.show();
        });
        task.setOnFailed(workerStateEvent -> {
            mainWindowController.showSuccessWindow(ERROR_GETTING_REGIONS);
            window.getStage().close();
        });
        new Thread(task).start();
    }
}
