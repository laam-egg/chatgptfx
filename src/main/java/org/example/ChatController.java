package org.example;

import com.google.common.html.HtmlEscapers;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;

import java.net.URL;

public class ChatController {
    @FXML
    private TextField messageInput;

    @FXML
    private WebView chatHistory;

    private StringBuilder chatHistoryHTML;

    @FXML
    private Button sendButton;

    @FXML
    private Label statusBar;

    private int numQueuedMessages;

    private ChatSession chatSession;

    private String getChatHistoryHTML() {
        return chatHistoryHTML.toString();
    }

    private void appendChatHistoryHTML(String html) {
        chatHistoryHTML.append(html);

        StringBuilder htmlBuilder = new StringBuilder().append("<html>");
        htmlBuilder.append("<head>");
        htmlBuilder.append("   <script language=\"javascript\" type=\"text/javascript\">");
        htmlBuilder.append("       function toBottom(){");
        htmlBuilder.append("           window.scrollTo(0, document.body.scrollHeight);");
        htmlBuilder.append("       }");
        htmlBuilder.append("   </script>");
        htmlBuilder.append("</head>");
        htmlBuilder.append("<body onload='toBottom()'>");
        htmlBuilder.append("<div class=\"chat-box\">");
        htmlBuilder.append(getChatHistoryHTML());
        htmlBuilder.append("</div></body></html>");

        chatHistory.getEngine().loadContent(
                htmlBuilder.toString()
        );
    }

    @FXML
    private void initialize() {
        chatHistoryHTML = new StringBuilder();

        numQueuedMessages = 0;

        chatSession = new ChatSession(this::receiveMessage);

        // https://stackoverflow.com/a/34339752/13680015
        final URL chatHistoryCSSLocation = ChatController.class.getResource("/css/chat-history.css");
        try {
            if (chatHistoryCSSLocation == null) {
                throw new Exception("Could not retrieve resource path for /css/chat-history.css");
            }
            chatHistory.getEngine().setUserStyleSheetLocation(chatHistoryCSSLocation.toString());
        } catch (Exception e) {
            throw new RuntimeException("Could not load chat history CSS: " + e.getMessage());
        }

        messageInput.setOnKeyPressed(this::onMessageInputKeyPressed);

        statusBar.setText("");
    }

    @FXML
    private void onMenuPreferencesClicked() {
        ConfigurationDialogController.showDialog();
    }

    @FXML
    private void onMenuAboutClicked() {
        AboutController.showDialog();
    }

    @FXML
    private void onMenuQuitClicked() {
        Utils.sendCloseWindowEvent(App.getPrimaryStage());
    }

    @FXML
    private void onMessageInputTextChanged() {
        // Currently empty
    }

    @FXML
    private void onMessageInputKeyPressed(final KeyEvent event) {
        if (KeyCode.ENTER == event.getCode()) {
            sendMessage();
        }
    }

    @FXML
    private void onSendButtonClicked() {
        sendMessage();
    }

    private void sendMessage() {
        final String messageToSend = messageInput.getText().trim();

        if (messageToSend.isEmpty()) {
            return;
        }

        appendChatHistoryHTML(
                String.format(
                        "<div class=\"message-row\"><div class=\"message-self-wrapper\">"
                                + "<p><span class=\"message-self\">%s</span></p>"
                        + "</div></div>",
                        HtmlEscapers.htmlEscaper().escape(messageToSend)
                )
        );

        statusBar.setText("ChatGPT is typing...");

        messageInput.setText("");

        ++numQueuedMessages;

        chatSession.send(messageToSend);
    }

    private Void receiveMessage(final String messageHtml) {
        appendChatHistoryHTML(
                String.format(
                        "<div class=\"message-row\"><div class=\"message-other-wrapper\">"
                            + "<span class=\"message-other\">%s</span>"
                        + "</div></div>",
                        messageHtml
                )
        );

        --numQueuedMessages;
        if (numQueuedMessages <= 0) {
            numQueuedMessages = 0;
            statusBar.setText("");
        }

        return null;
    }
}
