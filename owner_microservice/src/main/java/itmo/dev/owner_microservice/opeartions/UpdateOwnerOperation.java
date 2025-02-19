package itmo.dev.owner_microservice.opeartions;

import itmo.dev.common.dto.OwnerDto;
import itmo.dev.owner_microservice.services.interfaces.OwnerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class UpdateOwnerOperation implements Operation {

    private final RabbitTemplate rabbitTemplate;
    private final OwnerService ownerService;

    public UpdateOwnerOperation(RabbitTemplate rabbitTemplate, OwnerService ownerService) {
        this.rabbitTemplate = rabbitTemplate;
        this.ownerService = ownerService;
    }

    @Override
    public void execute(Message message, String nameQueue) {
        OwnerDto ownerDto = (OwnerDto) rabbitTemplate.getMessageConverter().fromMessage(message);
        Integer id = (Integer) message.getMessageProperties().getHeaders().get("ownerId");
        OwnerDto resultUpdate = ownerService.updateOwner(ownerDto, id);
        rabbitTemplate.convertAndSend(nameQueue, resultUpdate);
    }
}
