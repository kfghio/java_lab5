package itmo.dev.cat_microservice.operations;

import org.springframework.amqp.core.Message;

public interface Operation {
    void execute(Message message, String nameQueue);
}
