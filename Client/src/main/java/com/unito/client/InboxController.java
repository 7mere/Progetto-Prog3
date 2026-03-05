package com.unito.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class InboxController {

    @FXML
    private Label userEmailLabel;

    @FXML
    private Circle connectionDot;

    @FXML
    private Label connectionStatusLabel;

    @FXML
    private HBox notificationBar;

    @FXML
    private Label notificationLabel;

    @FXML
    private Button dismissNotificationButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Label lastUpdateLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView messageTable;

    @FXML
    private Button deleteButton;

    @FXML
    private Button replyButton;

    @FXML
    private Button replyAllButton;

    @FXML
    private Button forwardButton;

    @FXML
    private TabPane rightTabPane;

    @FXML
    private Label detailFromLabel;

    public Label detailToLabel;
    public Label detailDateLabel;
    public Label detailSubjectLabel;
    public TextArea detailBodyArea;
    public Button composeNewButton;
    public Label composeModeLabel;
    public TextField toField;
    public TextField ccField;
    public TextField subjectField;
    public TextArea bodyArea;
    public Label addressValidationLabel;
    public Button clearComposeButton;
    public Button sendButton;
    public Label statusBarLabel;
    public ProgressIndicator busyIndicator;


}
