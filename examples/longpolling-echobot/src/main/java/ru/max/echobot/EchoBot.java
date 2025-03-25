package ru.max.echobot;

import java.lang.invoke.MethodHandles;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.max.bot.exceptions.MaxBotException;
import ru.max.bot.longpolling.LongPollingBot;
import ru.max.bot.longpolling.LongPollingBotOptions;
import ru.max.botapi.model.Update;

public class EchoBot extends LongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EchoHandler handler;

    EchoBot(String accessToken) {
        super(accessToken, LongPollingBotOptions.DEFAULT);
        this.handler = new EchoHandler(getClient());
    }

    @Nullable
    @Override
    public Object onUpdate(Update update) {
        LOG.info("Received update: {}", update);
        update.visit(handler);
        return null;
    }

    @Override
    public void start() throws MaxBotException {
        super.start();
        LOG.info("Bot started");
    }

    @Override
    public void stop() {
        super.stop();
        LOG.info("Bot stopped");
    }
}
