package itmo.dev.api_gateway.controllers;

import itmo.dev.common.dto.OwnerDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    private final String nameQueue;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OwnerController(RabbitTemplate rabbitTemplate) {
        this.nameQueue = "ownerDtoQueue";
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OwnerDto> createOwner(@RequestBody OwnerDto ownerDto) {

        rabbitTemplate.convertAndSend(nameQueue, ownerDto, m -> {
            m.getMessageProperties().setHeader("operationType", "createOwner");
            return m;
        });


        return null;
    }

    @GetMapping
    public ResponseEntity<List<OwnerDto>> getAllOwners() {

        rabbitTemplate.convertAndSend(nameQueue, new OwnerDto(), m -> {
            m.getMessageProperties().setHeader("operationType", "getAllOwners");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            List<OwnerDto> result = (List<OwnerDto>) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerDto> getOwnerById(@PathVariable Integer ownerId) {

        rabbitTemplate.convertAndSend(nameQueue, new OwnerDto(), m -> {
            m.getMessageProperties().setHeader("ownerId", ownerId);
            m.getMessageProperties().setHeader("operationType", "getOwnerById");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            OwnerDto result = (OwnerDto) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<OwnerDto> updateOwner(@PathVariable Integer ownerId, @RequestBody OwnerDto ownerDto) {

        rabbitTemplate.convertAndSend(nameQueue, ownerDto, m -> {
            m.getMessageProperties().setHeader("ownerId", ownerId);
            m.getMessageProperties().setHeader("operationType", "updateOwner");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            OwnerDto result = (OwnerDto) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<String> deleteOwner(@PathVariable Integer ownerId) {

        rabbitTemplate.convertAndSend(nameQueue, new OwnerDto(), m -> {
            m.getMessageProperties().setHeader("ownerId", ownerId);
            m.getMessageProperties().setHeader("operationType", "deleteOwner");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            String result = (String) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }
}
