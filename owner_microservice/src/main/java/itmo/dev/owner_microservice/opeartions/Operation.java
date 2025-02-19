package itmo.dev.owner_microservice.opeartions;

import org.springframework.amqp.core.Message;

public interface Operation {
    void execute(Message message, String nameQueue);
}
