package org.example;

import com.google.common.html.HtmlEscapers;
import com.plexpt.chatgpt.ChatGPT;
import javafx.application.Platform;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.IOException;
import java.util.function.Function;

public class ChatSession {
    private ChatGPT chatGPT;

    private Parser markdownParser;

    private HtmlRenderer markdownHtmlRenderer;

    private Function<String, Void> onIncomingMessageCallback;


    private static String getChatGPTAPIKey() {
        return App.getConfigurationManager().getChatGPTAPIKey();
    }

    public ChatSession(Function<String, Void> onIncomingMessageCallback) {
        this.onIncomingMessageCallback = onIncomingMessageCallback;

        chatGPT = ChatGPT.builder()
                .apiKey(getChatGPTAPIKey())
                .build()
                .init();

        markdownParser = Parser.builder().build();

        markdownHtmlRenderer = HtmlRenderer.builder().build();
    }

    public void send(final String message) {
        chatGPT.setApiKey(getChatGPTAPIKey());

        final Thread sendThread = new Thread(() -> {
            try {
                final String chatResponseInMarkdown = chatGPT.chat(message);
                final Node responseMarkdownDocument = markdownParser.parse(chatResponseInMarkdown);
                final String chatResponseInHTML = markdownHtmlRenderer.render(responseMarkdownDocument);
                Platform.runLater(() -> {
                    onIncomingMessageCallback.apply(
                            String.format("<span class=\"message-other\">%s</span>",
                                    chatResponseInHTML
                            )
                    );
                });
            } catch (final Exception e) {
                Platform.runLater(() -> {
                    // https://www.baeldung.com/java-escape-html-symbols#2-using-google-guava
                    String exceptionMessageBrief = "Unknown fatal error";
                    String exceptionMessageDetails = e.getMessage();

                    if (exceptionMessageDetails.contains("java.net.")) {
                        // https://stackoverflow.com/a/57004050/13680015
                        exceptionMessageBrief = "No Internet connection. Check your network and try again.";
                    } else if (exceptionMessageDetails.contains("Incorrect API key provided")) {
                        exceptionMessageBrief = "Incorrect ChatGPT API Key.";
                        exceptionMessageDetails = "Please set a valid ChatGPT API key in File -> Preferences.";
                    } else if (exceptionMessageDetails.contains("You didn't provide an API key.")) {
                        exceptionMessageBrief = "Missing ChatGPT API Key.";
                        exceptionMessageDetails = "Please set a valid ChatGPT API key in File -> Preferences.";
                    } else if (exceptionMessageDetails.contains("Rate limit reached")) {
                        exceptionMessageBrief = "Rate limit reached.";
                    }

                    final String exceptionMessageBriefHtml = HtmlEscapers.htmlEscaper().escape(exceptionMessageBrief);
                    final String exceptionMessageDetailsHtml = HtmlEscapers.htmlEscaper().escape(exceptionMessageDetails);
                    onIncomingMessageCallback.apply(
                            String.format(
                                    "<p><span class=\"message-error\">%s</span><br>"
                                    + "<span class=\"message-error-details\">%s</span></p>",
                                    exceptionMessageBriefHtml,
                                    exceptionMessageDetailsHtml
                            )
                    );
                });
            }
        });

        sendThread.setDaemon(true);
        sendThread.start();
    }
}
