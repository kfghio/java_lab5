package itmo.dev.cat_microservice.services.impl;

import itmo.dev.cat_microservice.exceptions.CatNotFoundException;
import itmo.dev.cat_microservice.exceptions.OwnerNotFoundException;
import itmo.dev.cat_microservice.mapper.CatMapper;
import itmo.dev.cat_microservice.operations.*;
import itmo.dev.cat_microservice.repositories.CatRepository;
import itmo.dev.cat_microservice.services.interfaces.CatService;
import itmo.dev.common.dto.CatDto;
import itmo.dev.common.models.Cat;
import itmo.dev.common.models.Owner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatServiceImpl implements CatService {

    private final CatRepository catRepository;
    private final CatMapper catMapper;
    private final String nameQueue;
    private final String ownerModelToCatServiceQueue;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, Operation> actionMap;

    @Autowired
    public CatServiceImpl(CatRepository catRepository, CatMapper catMapper, RabbitTemplate rabbitTemplate) {
        this.catRepository = catRepository;
        this.catMapper = catMapper;
        this.nameQueue = "catDtoQueue";
        this.ownerModelToCatServiceQueue = "ownerModelToCatService";
        this.rabbitTemplate = rabbitTemplate;
        this.actionMap = new HashMap<>();
    }

    @RabbitListener(queues = "catDtoQueue")
    public void handleMessage(Message message) {
        String operation = message.getMessageProperties().getHeaders().get("operationType").toString();

        actionMap.put("createCat", new CreateCatOperation(rabbitTemplate, this));
        actionMap.put("getAllCats", new GetAllCatsOperation(rabbitTemplate, this));
        actionMap.put("getCatById", new GetCatByIdOperation(rabbitTemplate, this));
        actionMap.put("updateCat", new UpdateCatOperation(rabbitTemplate, this));
        actionMap.put("deleteCat", new DeleteCatOperation(rabbitTemplate, this));

        actionMap.get(operation).execute(message, nameQueue);
    }

    @Override
    public CatDto createCat(CatDto catDto, Integer ownerId) throws OwnerNotFoundException {

        Owner owner;
        rabbitTemplate.convertAndSend(ownerModelToCatServiceQueue, ownerId);
        Message message = rabbitTemplate.receive(ownerModelToCatServiceQueue, 10000);
        if (message != null) {
            owner = (Owner) rabbitTemplate.getMessageConverter().fromMessage(message);
        }
        else {
            throw  new OwnerNotFoundException("Owner could not be found");
        }
        Cat cat = catMapper.catToModel(catDto);
        cat.setOwner(owner);

        return catMapper.catToDto(catRepository.save(cat));
    }

    @Override
    public List<CatDto> getAllCats() {

        List<Cat> cats = catRepository.findAll();

        return cats.stream()
                .map(cat -> catMapper.catToDto(cat))
                .toList();
    }

    @Override
    public CatDto getCatById(Integer ownerId, Integer catId) throws OwnerNotFoundException, CatNotFoundException {

        Owner owner;
        rabbitTemplate.convertAndSend(ownerModelToCatServiceQueue, ownerId);
        Message message = rabbitTemplate.receive(ownerModelToCatServiceQueue, 10000);
        if (message != null) {
            owner = (Owner) rabbitTemplate.getMessageConverter().fromMessage(message);
        }
        else {
            throw  new OwnerNotFoundException("Owner could not be found");
        }

        Cat tmpCat = catRepository.findById(catId)
                .orElseThrow(() -> new CatNotFoundException("Cat could not be found"));

        if (!tmpCat.getOwner().equals(owner)) {

            throw new CatNotFoundException("This cat doesnt belong to the owner");
        }

        return catMapper.catToDto(tmpCat);
    }

    @Override
    public List<CatDto> getCatsByOwnerId(Integer ownerId) {

        List<Cat> cats = catRepository.findAllByOwnerId(ownerId);

        return cats.stream()
                .map(cat -> catMapper.catToDto(cat))
                .toList();
    }

    @Override
    public CatDto updateCat(Integer ownerId, Integer catId, CatDto catDto) throws OwnerNotFoundException, CatNotFoundException {

        Owner owner;
        rabbitTemplate.convertAndSend(ownerModelToCatServiceQueue, ownerId);
        Message message = rabbitTemplate.receive(ownerModelToCatServiceQueue, 10000);
        if (message != null) {
            owner = (Owner) rabbitTemplate.getMessageConverter().fromMessage(message);
        }
        else {
            throw new OwnerNotFoundException("Owner could not be found");
        }

        Cat tmpCat = catRepository.findById(catId)
                .orElseThrow(() -> new CatNotFoundException("Cat could not be found"));

        if (!tmpCat.getOwner().equals(owner)) {

            throw new CatNotFoundException("This cat doesnt belong to the owner");
        }

        tmpCat.setName(catDto.getName());
        tmpCat.setBirthDate(catDto.getBirthDate());
        tmpCat.setBreed(catDto.getBreed());
        tmpCat.setColor(catDto.getColor());
        tmpCat.setCatFriends(catDto.getCatFriends() == null ? null : catDto.getCatFriends().stream()
                .map(tmp -> catMapper.catToModel(tmp)).toList());

        return catMapper.catToDto(catRepository.save(tmpCat));
    }

    @Override
    public void addCatFriend(Integer catId, Integer friendId) throws CatNotFoundException{

        Cat firstCat = catRepository.findById(catId)
                .orElseThrow(() -> new CatNotFoundException("First cat could not be found"));
        Cat secondCat = catRepository.findById(friendId)
                .orElseThrow(() -> new CatNotFoundException("Second cat could not be found"));

        firstCat.getCatFriends().add(secondCat);
        secondCat.getCatFriends().add(firstCat);

        catRepository.save(firstCat);
        catRepository.save(secondCat);
    }

    @Override
    public void deleteCat(Integer ownerId, Integer catId) throws OwnerNotFoundException, CatNotFoundException {

        Owner owner;
        rabbitTemplate.convertAndSend(ownerModelToCatServiceQueue, ownerId);
        Message message = rabbitTemplate.receive(ownerModelToCatServiceQueue, 10000);
        if (message != null) {
            owner = (Owner) rabbitTemplate.getMessageConverter().fromMessage(message);
        }
        else {
            throw  new OwnerNotFoundException("Owner could not be found");
        }

        Cat tmpCat = catRepository.findById(catId)
                .orElseThrow(() -> new CatNotFoundException("Cat could not be found"));

        if (!tmpCat.getOwner().equals(owner)) {

            throw new CatNotFoundException("This cat doesnt belong to the owner");
        }

        catRepository.delete(tmpCat);
    }
}