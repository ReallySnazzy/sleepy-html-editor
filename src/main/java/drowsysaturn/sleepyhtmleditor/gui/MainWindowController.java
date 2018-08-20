package drowsysaturn.sleepyhtmleditor.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import drowsysaturn.sleepyhtmleditor.editor.BackgroundStyleHelper;
import drowsysaturn.sleepyhtmleditor.editor.CoordinateCoder;
import drowsysaturn.sleepyhtmleditor.editor.CoordinateCodingException;
import drowsysaturn.sleepyhtmleditor.editor.DocumentElement;
import drowsysaturn.sleepyhtmleditor.editor.DocumentLense;
import drowsysaturn.sleepyhtmleditor.editor.ScreenCoordinate;
import drowsysaturn.sleepyhtmleditor.editor.ScreenCoordinateSingle;
import drowsysaturn.sleepyhtmleditor.editor.StandardPanel;
import drowsysaturn.sleepyhtmleditor.editor.TextPanel;
import drowsysaturn.sleepyhtmleditor.html.HtmlExporter;

public class MainWindowController {
    @FXML
    private Pane centerPane;

    private DocumentCanvas documentCanvas;

    private DocumentLense lense = null;

    private DocumentElement selectedElement = null;

    @FXML
    private ColorPicker selectedBackgroundColorPicker;

    @FXML
    private TextField selectedBackgroundOpacityText;

    @FXML
    private Button selectedBackgroundImageBrowseButton;

    @FXML
    private ChoiceBox selectedBackgroundStyleChoiceBox;

    @FXML
    private TextField selectedTextContentTextBox;

    @FXML
    private ColorPicker selectedTextColorPicker;

    @FXML
    private TextField selectedTextSizeTextBox;

    @FXML
    private TextField selectedTextFontTextBox;

    @FXML
    private TextField selectedSizeTextBoxX;

    @FXML
    private TextField selectedSizeTextBoxY;

    @FXML
    private TextField selectedPositionTextBoxX;

    @FXML
    private TextField selectedPositionTextBoxY;

    @FXML
    private Button selectedApplyBackgroundButton;

    @FXML
    private Button selectedApplyTextButton;

    @FXML
    private Button selectedApplyPositionButton;

    @FXML
    private Button saveHtmlDocumentButton;

    @FXML
    private TextField htmlDocumentTitleTextField;

    @FXML
    public void onCreateStandardButtonClick() {
        int randomOffsetX = (int)Math.floor(Math.random()*200);
        int randomOffsetY = (int)Math.floor(Math.random()*200);
        ScreenCoordinate position = new ScreenCoordinate(randomOffsetX, 0.25, randomOffsetY, 0.25);
        ScreenCoordinate size = new ScreenCoordinate(0, 0.5, 0, 0.5);
        StandardPanel panel = new StandardPanel(position, size);
        lense.getRoot().addChild(panel);
        redrawDisplay();
    }

    @FXML
    public void onCreateTextButtonClick() {
        int randomOffsetX = (int)Math.floor(Math.random()*200);
        int randomOffsetY = (int)Math.floor(Math.random()*200);
        ScreenCoordinate position = new ScreenCoordinate(randomOffsetX, 0.25, randomOffsetY, 0.25);
        ScreenCoordinate size = new ScreenCoordinate(0, 0.5, 0, 0.5);
        TextPanel panel = new TextPanel(position, size, "Test text");
        lense.getRoot().addChild(panel);
        redrawDisplay();
    }

    @FXML
    public void onDeleteSelectedClick() {
        if (selectedElement != null && lense != null && lense.getRoot() != selectedElement) {
            lense.removeElement(selectedElement);
            redrawDisplay();
        }
    }

    private void redrawDisplay() {
        FxHelper.executeFxThread(()-> {
            documentCanvas.draw();
        });
    }

    private void selectElement(DocumentElement selection) {
        if (selection != null) {
            if (selectedElement != null) {
                selectedElement.showSelectionBox(false);
            }
            selectedElement = selection;
            selectedElement.showSelectionBox(true);
            redrawDisplay();
            updateSelectionPanel();
        }
    }

    private void updateSelectionPanel() {
        updateSelectionPanelBackground();
        updateSelectionPanelText();
        updateSelectionPanelSize();
    }

    @FXML
    private void onSaveDocumentClick() {
        Stage stage = (Stage)documentCanvas.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save html");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                String title = htmlDocumentTitleTextField.getText();
                HtmlExporter htmlExporter = new HtmlExporter(lense, title);
                htmlExporter.write(file);
            } catch (IOException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("File error");
                alert.setContentText("Failed to save HTML document.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onApplyBackgroundClick() {
        FxHelper.executeFxThread(() -> {
            if (selectedElement != null && selectedElement instanceof StandardPanel) {
                StandardPanel selectedStandardPanel = (StandardPanel)selectedElement;
                Color colorWithoutOpacity = FxHelper.fromFxColorToAwt(selectedBackgroundColorPicker.getValue());
                selectedStandardPanel.setBackgroundStyle(
                    BackgroundStyleHelper.parseString((String)selectedBackgroundStyleChoiceBox.getValue()));
                String opacityString = selectedBackgroundOpacityText.getText();
                double opacity = 1;
                try {
                    opacity = Double.parseDouble(opacityString);
                } catch (Exception ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Invalid input");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Invalid number entered for background opacity");
                    alert.showAndWait();
                }
                selectedStandardPanel.setBackgroundColor(
                    new Color(
                        colorWithoutOpacity.getRed(),
                        colorWithoutOpacity.getGreen(),
                        colorWithoutOpacity.getBlue(),
                        (int)(opacity*255)));
                redrawDisplay();
            }
        });
    }

    @FXML
    private void onApplyPositionClick() {
        FxHelper.executeFxThread(() -> {
            if (selectedElement != null && selectedElement != lense.getRoot()) {
                try {
                    ScreenCoordinateSingle posX = CoordinateCoder.decode(selectedPositionTextBoxX.getText());
                    ScreenCoordinateSingle posY = CoordinateCoder.decode(selectedPositionTextBoxY.getText());
                    ScreenCoordinateSingle sizeX = CoordinateCoder.decode(selectedSizeTextBoxX.getText());
                    ScreenCoordinateSingle sizeY = CoordinateCoder.decode(selectedSizeTextBoxY.getText());
                    ScreenCoordinate size = new ScreenCoordinate(sizeX, sizeY);
                    ScreenCoordinate pos = new ScreenCoordinate(posX, posY);
                    selectedElement.setPosition(pos);
                    selectedElement.setSize(size);
                } catch (CoordinateCodingException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Invalid input");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Invalid input for position or size.");
                    alert.showAndWait();
                }
            }
            redrawDisplay();
        });
    }

    @FXML
    private void onApplyTextClick() {
        FxHelper.executeFxThread(() -> {
            if (selectedElement != null && selectedElement instanceof StandardPanel) {
                TextPanel selectedTextPanel = (TextPanel)selectedElement;
                selectedTextPanel.setForegroundColor(
                    FxHelper.fromFxColorToAwt(selectedTextColorPicker.getValue()));
                selectedTextPanel.setText(selectedTextContentTextBox.getText());
                selectedTextPanel.setFontFamily(selectedTextFontTextBox.getText());
                try {
                    selectedTextPanel.setFontSize(Integer.parseInt(selectedTextSizeTextBox.getText()));
                } catch (Exception ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Invalid input");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Invalid number entered for font size.");
                    alert.showAndWait();
                }
            }
            redrawDisplay();
        });
    }

    private void updateSelectionPanelBackground() {
        if (selectedElement instanceof StandardPanel) {
            StandardPanel selectedStandardPanel = (StandardPanel)selectedElement;
            FxHelper.executeFxThread(() -> {
                selectedBackgroundColorPicker.setDisable(false);
                selectedBackgroundColorPicker.setValue(FxHelper.fromAwtColorToFx(selectedStandardPanel.getBackgroundColor()));
                selectedBackgroundImageBrowseButton.setDisable(false);
                selectedBackgroundOpacityText.setDisable(false);
                double opacity = (double)selectedStandardPanel.getBackgroundColor().getAlpha()/255;
                selectedBackgroundOpacityText.setText(String.format("%.3f", opacity));
                selectedBackgroundStyleChoiceBox.setDisable(false);
                String backgroundStyleString = BackgroundStyleHelper.toString(selectedStandardPanel.getBackgroundStyle());
                selectedBackgroundStyleChoiceBox.setValue(backgroundStyleString);
                selectedApplyBackgroundButton.setDisable(false);
            });
        } else {
            FxHelper.executeFxThread(() -> {
                selectedBackgroundColorPicker.setDisable(true);
                selectedBackgroundImageBrowseButton.setDisable(true);
                selectedBackgroundOpacityText.setDisable(true);
                selectedBackgroundStyleChoiceBox.setDisable(true);
                selectedApplyBackgroundButton.setDisable(true);
            });
        }
    }

    private void updateSelectionPanelText() {
        FxHelper.executeFxThread(() -> {
            boolean isTextPanel = (selectedElement instanceof TextPanel);
            selectedTextColorPicker.setDisable(!isTextPanel);
            selectedTextContentTextBox.setDisable(!isTextPanel);
            selectedTextFontTextBox.setDisable(!isTextPanel);
            selectedTextSizeTextBox.setDisable(!isTextPanel);
            selectedApplyTextButton.setDisable(!isTextPanel);
            if (isTextPanel) {
                TextPanel selectedTextPanel = (TextPanel)selectedElement;
                String text = selectedTextPanel.getText();
                Color color = selectedTextPanel.getForegroundColor();
                String fontFamily = selectedTextPanel.getFontFamily();
                String fontSize = Integer.toString(selectedTextPanel.getFontSize());
                selectedTextContentTextBox.setText(text);
                selectedTextColorPicker.setValue(FxHelper.fromAwtColorToFx(color));
                selectedTextFontTextBox.setText(fontFamily);
                selectedTextSizeTextBox.setText(fontSize);
            }
        });
    }

    private void updateSelectionPanelSize() {
        FxHelper.executeFxThread(() -> {
            if (selectedElement != lense.getRoot()) {
                ScreenCoordinate position = selectedElement.getPosition();
                ScreenCoordinate size = selectedElement.getSize();
                String posXString = position.getPercentX()*100 + "% + " + position.getPixelsX() + "px";
                String posYString = position.getPercentY()*100 + "% + " + position.getPixelsY() + "px";
                String sizeXString = size.getPercentX()*100 + "% + " + size.getPixelsX() + "px";
                String sizeYString = size.getPercentY()*100 + "% + " + size.getPixelsY() + "px";
                selectedSizeTextBoxX.setText(sizeXString);
                selectedSizeTextBoxY.setText(sizeYString);
                selectedPositionTextBoxX.setText(posXString);
                selectedPositionTextBoxY.setText(posYString);
                selectedSizeTextBoxX.setDisable(false);
                selectedSizeTextBoxY.setDisable(false);
                selectedPositionTextBoxX.setDisable(false);
                selectedPositionTextBoxY.setDisable(false);
                selectedApplyPositionButton.setDisable(false);
            } else {
                selectedSizeTextBoxX.setDisable(true);
                selectedSizeTextBoxY.setDisable(true);
                selectedPositionTextBoxX.setDisable(true);
                selectedPositionTextBoxY.setDisable(true);
                selectedApplyPositionButton.setDisable(true);
            }
        });
    }

    private void onDocumentClicked(MouseEvent event) {
        if (lense == null) {
            return;
        }
        int x = (int)Math.floor(event.getX());
        int y = (int)Math.floor(event.getY());
        int width = (int)Math.floor(documentCanvas.getWidth());
        int height = (int)Math.floor(documentCanvas.getHeight());
        DocumentElement selection = lense.select(width, height, x, y);
        if (selection != null) {
            selectElement(selection);
        }
    }

    @FXML
    private void initialize() {
        addDocumentCanvas();
        centerPane.requestLayout();
        documentCanvas.setOnMouseClicked(mouseEvent -> {
            onDocumentClicked(mouseEvent);
        });
        fillChoiceBoxes();
        createRootElement();
    }

    private void fillChoiceBoxes() {
        selectedBackgroundStyleChoiceBox.setItems(FXCollections.observableArrayList(
            "Solid color",
            "Stretch",
            "Cover",
            "Original",
            "Repeat"
        ));
        selectedBackgroundStyleChoiceBox.setValue("Original");
    }

    private void createRootElement() {
        ScreenCoordinate position = new ScreenCoordinate(0, 0, 0, 0);
        ScreenCoordinate size = new ScreenCoordinate(0, 1, 0, 1);
        StandardPanel panel = new StandardPanel(position, size);
        panel.setBackgroundColor(Color.WHITE);
        lense = new DocumentLense(panel);
        documentCanvas.setLense(lense);
    }

    private void addDocumentCanvas() {
        documentCanvas = new DocumentCanvas();
        centerPane.getChildren().add(documentCanvas);
        documentCanvas.bindDimsToParent(centerPane);
    }
}
