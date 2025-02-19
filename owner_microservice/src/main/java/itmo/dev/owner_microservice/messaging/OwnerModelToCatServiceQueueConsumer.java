package itmo.dev.owner_microservice.messaging;

import itmo.dev.common.models.Owner;
import itmo.dev.owner_microservice.exceptions.OwnerNotFoundException;
import itmo.dev.owner_microservice.repositories.OwnerRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerModelToCatServiceQueueConsumer {

    private final OwnerRepository ownerRepository;
    private final String ownerModelToCatServiceQueue;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OwnerModelToCatServiceQueueConsumer(OwnerRepository ownerRepository, RabbitTemplate rabbitTemplate) {
        this.ownerRepository = ownerRepository;
        this.ownerModelToCatServiceQueue = "ownerModelToCatService";
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "ownerModelToCatService")
    public void handle(Message message) {
        Integer ownerId = (Integer) rabbitTemplate.getMessageConverter().fromMessage(message);
        Owner tmpOwner = ownerRepository.findById(ownerId).orElseThrow(() -> new OwnerNotFoundException("Owner could not be found"));
        rabbitTemplate.convertAndSend(ownerModelToCatServiceQueue, tmpOwner);
    }
}
