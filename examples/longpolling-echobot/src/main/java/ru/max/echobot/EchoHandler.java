package ru.max.echobot;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.max.botapi.client.MaxClient;
import ru.max.botapi.exceptions.APIException;
import ru.max.botapi.exceptions.ClientException;
import ru.max.botapi.model.BotAddedToChatUpdate;
import ru.max.botapi.model.BotRemovedFromChatUpdate;
import ru.max.botapi.model.BotStartedUpdate;
import ru.max.botapi.model.ChatTitleChangedUpdate;
import ru.max.botapi.model.MessageCallbackUpdate;
import ru.max.botapi.model.MessageChatCreatedUpdate;
import ru.max.botapi.model.MessageCreatedUpdate;
import ru.max.botapi.model.MessageEditedUpdate;
import ru.max.botapi.model.MessageRemovedUpdate;
import ru.max.botapi.model.NewMessageBody;
import ru.max.botapi.model.Update;
import ru.max.botapi.model.UserAddedToChatUpdate;
import ru.max.botapi.model.UserRemovedFromChatUpdate;
import ru.max.botapi.queries.SendMessageQuery;
import ru.max.botapi.queries.MaxQuery;

public class EchoHandler implements Update.Visitor {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ObjectMapper mapper;
    private final MaxClient client;

    EchoHandler(MaxClient client) {
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void visit(MessageCreatedUpdate update) {
        Long chatId = Objects.requireNonNull(update.getMessage().getRecipient().getChatId(), "chatId");
        sendToChat(chatId, update);
    }

    @Override
    public void visit(MessageCallbackUpdate update) {
        sendToUser(update.getCallback().getUser().getUserId(), update);
    }

    @Override
    public void visit(MessageEditedUpdate update) {
        long chatId = Objects.requireNonNull(update.getMessage().getRecipient().getChatId(), "chatId");
        sendToChat(chatId, update);
    }

    @Override
    public void visit(MessageRemovedUpdate update) {

    }

    @Override
    public void visit(BotAddedToChatUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(BotRemovedFromChatUpdate update) {
        sendToUser(update.getUser().getUserId(), update);
    }

    @Override
    public void visit(UserAddedToChatUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(UserRemovedFromChatUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(BotStartedUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(ChatTitleChangedUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(MessageChatCreatedUpdate update) {
        sendToUser(Objects.requireNonNull(update.getChat().getOwnerId(), "ownerId"), update);
    }

    @Override
    public void visitDefault(Update update) {
        LOG.warn("Update {} is unsupported", update);
    }

    private void sendToChat(long chatId, Update update) {
        try {
            sendSafely(prepareQuery(update).chatId(chatId));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to prepare request to send update {} to chat {}", update, chatId, e);
        }
    }

    private void sendToUser(long userId, Update update) {
        try {
            sendSafely(prepareQuery(update).userId(userId));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to prepare request to send update {} to user {}", update, userId, e);
        }
    }

    private SendMessageQuery prepareQuery(Update update) throws JsonProcessingException {
        String text = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(update);
        return new SendMessageQuery(client, new NewMessageBody(text, null, null));
    }

    private void sendSafely(MaxQuery<?> query) {
        try {
            query.execute();
        } catch (APIException | ClientException e) {
            LOG.error("Failed to execute query {}", query, e);
        }
    }
}
