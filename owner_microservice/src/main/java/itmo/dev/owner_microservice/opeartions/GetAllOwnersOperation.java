package itmo.dev.owner_microservice.opeartions;

import itmo.dev.common.dto.OwnerDto;
import itmo.dev.owner_microservice.services.interfaces.OwnerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

public class GetAllOwnersOperation implements Operation {

    private final RabbitTemplate rabbitTemplate;
    private final OwnerService ownerService;

    public GetAllOwnersOperation(RabbitTemplate rabbitTemplate, OwnerService ownerService) {
        this.rabbitTemplate = rabbitTemplate;
        this.ownerService = ownerService;
    }

    @Override
    public void execute(Message message, String nameQueue) {
        List<OwnerDto> resultList = ownerService.getAllOwners();
        rabbitTemplate.convertAndSend(nameQueue, resultList);
    }
}
