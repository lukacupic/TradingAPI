package com.dormire.trading.gui.controllers;

import com.dormire.trading.gui.Instrument;
import com.dormire.trading.gui.scenes.PromptScene;
import com.dormire.trading.gui.RingManager;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

public class MainController {

    private static final String TICKER_LOGO_URL = "https://trading212equities.s3.eu-central-1.amazonaws.com/%s.png";

    @FXML
    private JFXButton addButton;

    @FXML
    private VBox instrumentPane;

    @FXML
    private Label stepLabel;

    @FXML
    private Label mainLabel;

    @FXML
    private ImageView ringView;

    private Color focusColor = Color.rgb(31, 31, 31);
    private Color unfocusColor = Color.rgb(45, 44, 45);

    private Background focusBackground;
    private final Background unfocusBackground;

    public MainController() {
        this.focusBackground = new Background(new BackgroundFill(focusColor, CornerRadii.EMPTY, Insets.EMPTY));
        this.unfocusBackground = new Background(new BackgroundFill(unfocusColor, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public void initialize() {
        ControllerMediator.getInstance().setMainController(this);

        HBox home = loadInstrument();
        setupInstrument(home);
        instrumentPane.getChildren().add(home);

        ImageView imageView = (ImageView) home.getChildren().get(0);
        InputStream homeStream = getClass().getResourceAsStream("/images/home.png");
        imageView.setImage(new Image(homeStream));
        imageView.setFitWidth(50);

        addButton.setOnAction(event -> {
            try {
                Scene mainScene = ((Node) (event.getSource())).getScene();
                new PromptScene(mainScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        double ringSize = 350;
        ringView.setFitWidth(ringSize);
        ringView.setFitHeight(ringSize);

        bindLabelFontSize(stepLabel, 0.8 * ringSize, 18);
        bindLabelFontSize(mainLabel, 0.8 * ringSize, 18);
    }

    private void bindLabelFontSize(Label label, double ringSize, double defaultFontSize) {
        Font defaultFont = Font.font(defaultFontSize);

        label.textProperty().addListener((observable, oldValue, newValue) -> {
            Text tempText = new Text(newValue);
            tempText.setFont(defaultFont);

            double textWidth = tempText.getLayoutBounds().getWidth();
            System.out.println(label + " " + textWidth);

            if (textWidth <= ringSize) {
                label.setFont(defaultFont);

            } else {
                double newFontSize = defaultFontSize * ringSize / textWidth;
                label.setFont(Font.font(defaultFont.getFamily(), newFontSize));
            }
        });
    }

    public void addInstrument(Instrument instrument) {
        updateMainLabel("Loading browser emulator, please wait...");
        createHBox(instrument);
        createRingManager(instrument);
    }

    private void createRingManager(Instrument instrument) {
        RingManager manager = new RingManager(instrument);
        manager.start();
    }

    private void createHBox(Instrument instrument) {
        HBox instrumentBox = loadInstrument();

        // set logo image for the instrument
        String url = String.format(TICKER_LOGO_URL, instrument.getTicker());
        ImageView imageView = (ImageView) instrumentBox.getChildren().get(0);
        imageView.setImage(new Image(url));

        // set instrument bindings and click behaviour
        setupInstrument(instrumentBox);

        instrumentPane.getChildren().add(instrumentBox);
    }

    public void updateMainLabel(String text) {
        mainLabel.setText(text);
    }

    public void updateStepLabel(String text) {
        stepLabel.setText(text);
    }

    private HBox loadInstrument() {
        try {
            return FXMLLoader.load(getClass().getResource("/interfaces/instrument.fxml"));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Sets the bindings and click actions for the given stonk instrument.
     * The method will bind different instrument background colors depending
     * on whether an instrument is focused or not. It will also set-up click
     * actions resulting from clicking on the instrument.
     *
     * @param instrument the instrument
     */
    private void setupInstrument(HBox instrument) {
        instrument.setOnMouseClicked(event -> instrument.requestFocus());

        instrument.backgroundProperty().bind(Bindings
                .when(instrument.focusedProperty())
                .then(focusBackground)
                .otherwise(unfocusBackground));
    }

    public String showAlert(String text, String... buttons) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(text);

        ButtonType[] buttonTypes = Arrays.stream(buttons).map(ButtonType::new).toArray(ButtonType[]::new);

        alert.getButtonTypes().setAll(buttonTypes);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get().getText();
    }

    public String showInputDialog(String text) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(null);
        dialog.setHeaderText(null);
        dialog.setContentText(text);

        Optional<String> result = dialog.showAndWait();
        return result.get();
    }
}