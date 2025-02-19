package itmo.dev.api_gateway.controllers;

import itmo.dev.common.dto.CatDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class CatController {

    private final String nameQueue;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CatController(RabbitTemplate rabbitTemplate) {
        nameQueue = "catDtoQueue";
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/{ownerId}/cat")
    public ResponseEntity<CatDto> createCat(@PathVariable Integer ownerId, @RequestBody CatDto catDto) {

        rabbitTemplate.convertAndSend(nameQueue, catDto, m -> {
            m.getMessageProperties().setHeader("ownerId", ownerId);
            m.getMessageProperties().setHeader("operationType", "createCat");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            CatDto result = (CatDto) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }

    @GetMapping("/cats")
    public ResponseEntity<List<CatDto>> getAllCats() {

        rabbitTemplate.convertAndSend(nameQueue, new CatDto(), m -> {
            m.getMessageProperties().setHeader("operationType", "getAllCats");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            List<CatDto> result = (List<CatDto>) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }

    @GetMapping("/{ownerId}/cat/{catId}")
    public ResponseEntity<CatDto> getCatById(@PathVariable Integer ownerId, @PathVariable Integer catId) {

        rabbitTemplate.convertAndSend(nameQueue, new CatDto(), m -> {
            m.getMessageProperties().setHeader("ownerId", ownerId);
            m.getMessageProperties().setHeader("catId", catId);
            m.getMessageProperties().setHeader("operationType", "getCatById");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            CatDto result = (CatDto) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }

    @GetMapping("/{ownerId}/cats")
    public ResponseEntity<List<CatDto>> getAllCatsByOwnerId(@PathVariable Integer ownerId) {

        rabbitTemplate.convertAndSend(nameQueue, new CatDto(), m -> {
            m.getMessageProperties().setHeader("ownerId", ownerId);
            m.getMessageProperties().setHeader("operationType", "getCatsByOwnerId");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            List<CatDto> result = (List<CatDto>) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }

    @PutMapping("/{ownerId}/cat/{catId}/update")
    public ResponseEntity<CatDto> updateCat(@PathVariable Integer ownerId,
                                            @PathVariable Integer catId,
                                            @RequestBody CatDto catDto) {

        rabbitTemplate.convertAndSend(nameQueue, catDto, m -> {
            m.getMessageProperties().setHeader("ownerId", ownerId);
            m.getMessageProperties().setHeader("catId", catId);
            m.getMessageProperties().setHeader("operationType", "updateCat");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            CatDto result = (CatDto) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
    }

    @PutMapping("/cat/{catId}/friend/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Integer catId, @PathVariable Integer friendId) {

        rabbitTemplate.convertAndSend(nameQueue, new CatDto(), m -> {
            m.getMessageProperties().setHeader("catId", catId);
            m.getMessageProperties().setHeader("friendId", friendId);
            m.getMessageProperties().setHeader("operationType", "addCatFriend");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            String result = (String) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }

        // return ResponseEntity.ok("Cat(id = " + catId + ") and cat (id = " + friendId + ") becomes friend");
    }

    @DeleteMapping("/{ownerId}/cat/{catId}/delete")
    public ResponseEntity<String> deleteCat(@PathVariable Integer ownerId, @PathVariable Integer catId) {

        rabbitTemplate.convertAndSend(nameQueue, new CatDto(), m -> {
            m.getMessageProperties().setHeader("ownerId", ownerId);
            m.getMessageProperties().setHeader("catId", catId);
            m.getMessageProperties().setHeader("operationType", "deleteCat");
            return m;
        });

        Message message = rabbitTemplate.receive(nameQueue, 10000);
        if (message != null) {
            String result = (String) rabbitTemplate.getMessageConverter().fromMessage(message);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }

        // return ResponseEntity.ok("Deleted Cat");
    }
}
