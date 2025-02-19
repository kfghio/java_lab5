package itmo.dev.cat_microservice.operations;

import itmo.dev.cat_microservice.services.interfaces.CatService;
import itmo.dev.common.dto.CatDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class CreateCatOperation implements Operation{

    private final RabbitTemplate rabbitTemplate;
    private final CatService catService;

    public CreateCatOperation(RabbitTemplate rabbitTemplate, CatService catService) {
        this.rabbitTemplate = rabbitTemplate;
        this.catService = catService;
    }

    @Override
    public void execute(Message message, String nameQueue) {
        Integer ownerId = (Integer) message.getMessageProperties().getHeaders().get("ownerId");
        CatDto catDto = (CatDto) rabbitTemplate.getMessageConverter().fromMessage(message);
        CatDto result = catService.createCat(catDto, ownerId);
        rabbitTemplate.convertAndSend(nameQueue, result);
    }
}
