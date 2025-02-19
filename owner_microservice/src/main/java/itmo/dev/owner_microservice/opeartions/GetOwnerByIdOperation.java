package itmo.dev.owner_microservice.opeartions;

import itmo.dev.common.dto.OwnerDto;
import itmo.dev.owner_microservice.services.interfaces.OwnerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class GetOwnerByIdOperation implements Operation {

    private final RabbitTemplate rabbitTemplate;
    private final OwnerService ownerService;

    public GetOwnerByIdOperation(RabbitTemplate rabbitTemplate, OwnerService ownerService) {
        this.rabbitTemplate = rabbitTemplate;
        this.ownerService = ownerService;
    }

    @Override
    public void execute(Message message, String nameQueue) {
        Integer id = (Integer) message.getMessageProperties().getHeaders().get("ownerId");
        OwnerDto resultById = ownerService.getOwnerById(id);
        rabbitTemplate.convertAndSend(nameQueue, resultById);
    }
}
