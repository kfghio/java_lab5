package itmo.dev.owner_microservice.services.impl;

import itmo.dev.common.models.Cat;
import itmo.dev.common.models.Owner;
import itmo.dev.owner_microservice.opeartions.*;
import itmo.dev.owner_microservice.repositories.OwnerRepository;
import itmo.dev.common.dto.OwnerDto;
import itmo.dev.owner_microservice.exceptions.OwnerNotFoundException;
import itmo.dev.owner_microservice.mapper.CatMapper;
import itmo.dev.owner_microservice.mapper.OwnerMapper;
import itmo.dev.owner_microservice.services.interfaces.OwnerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final CatMapper catMapper;
    private final OwnerMapper ownerMapper;
    private final String nameQueue;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, Operation> actionMap;

    @Autowired
    public OwnerServiceImpl(OwnerRepository ownerRepository, CatMapper catMapper,
                            OwnerMapper ownerMapper, RabbitTemplate rabbitTemplate) {
        this.ownerRepository = ownerRepository;
        this.catMapper = catMapper;
        this.ownerMapper = ownerMapper;
        this.nameQueue = "ownerDtoQueue";
        this.rabbitTemplate = rabbitTemplate;
        this.actionMap = new HashMap<>();
    }

    @RabbitListener(queues = "ownerDtoQueue")
    public void handleMessage(Message message) {
        String operation = message.getMessageProperties().getHeaders().get("operationType").toString();

        actionMap.put("createOwner", new CreateOwnerOperation(rabbitTemplate, this));
        actionMap.put("getAllOwners", new GetAllOwnersOperation(rabbitTemplate, this));
        actionMap.put("getOwnerById", new GetOwnerByIdOperation(rabbitTemplate, this));
        actionMap.put("updateOwner", new UpdateOwnerOperation(rabbitTemplate, this));
        actionMap.put("deleteOwner", new DeleteOwnerOperation(rabbitTemplate, this));

        actionMap.get(operation).execute(message, nameQueue);
    }

    @Override
    public OwnerDto createOwner(OwnerDto ownerDto) {

        Owner owner = ownerMapper.ownerToModel(ownerDto);

        return ownerMapper.ownerToDto(ownerRepository.save(owner));
    }

    @Override
    public List<OwnerDto> getAllOwners() {

        List<Owner> ownerList = ownerRepository.findAll();

        return ownerList.stream()
                .map(ownerMapper::ownerToDto)
                .toList();
    }

    @Override
    public OwnerDto getOwnerById(Integer id) throws OwnerNotFoundException {

        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException("Owner could not be found"));

        return ownerMapper.ownerToDto(owner);
    }

    @Override
    public OwnerDto updateOwner(OwnerDto ownerDto, Integer id) throws OwnerNotFoundException {

        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException("Owner could not be found"));
        owner.setName(ownerDto.getName());
        owner.setBirthDate(ownerDto.getBirthDate());

        List<Cat> tmpList = ownerDto.getCatsList() == null ? null : ownerDto.getCatsList().stream()
                .map(catMapper::catToModel)
                .toList();
        owner.setCatsList(tmpList);

        return ownerMapper.ownerToDto(ownerRepository.save(owner));
    }

    @Override
    public void deleteOwner(Integer id) throws OwnerNotFoundException {

        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException("Owner could not be found"));

        ownerRepository.delete(owner);
    }
}