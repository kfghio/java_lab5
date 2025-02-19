package itmo.dev.owner_microservice.opeartions;

import itmo.dev.common.dto.OwnerDto;
import itmo.dev.owner_microservice.services.interfaces.OwnerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class CreateOwnerOperation implements Operation {

    private final RabbitTemplate rabbitTemplate;
    private final OwnerService ownerService;

    public CreateOwnerOperation(RabbitTemplate rabbitTemplate, OwnerService ownerService) {
        this.rabbitTemplate = rabbitTemplate;
        this.ownerService = ownerService;
    }

    @Override
    public void execute(Message message, String nameQueue) {
        OwnerDto ownerDto = (OwnerDto) rabbitTemplate.getMessageConverter().fromMessage(message);
        OwnerDto result = ownerService.createOwner(ownerDto);
        rabbitTemplate.convertAndSend(nameQueue, result);
    }
}
