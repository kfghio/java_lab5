package itmo.dev.cat_microservice.operations;

import itmo.dev.cat_microservice.services.interfaces.CatService;
import itmo.dev.common.dto.CatDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public class GetAllCatsOperation implements Operation{

    private final RabbitTemplate rabbitTemplate;
    private final CatService catService;

    public GetAllCatsOperation(RabbitTemplate rabbitTemplate, CatService catService) {
        this.rabbitTemplate = rabbitTemplate;
        this.catService = catService;
    }

    @Override
    public void execute(Message message, String nameQueue) {
        List<CatDto> resultList = catService.getAllCats();
        rabbitTemplate.convertAndSend(nameQueue, resultList);
    }
}
