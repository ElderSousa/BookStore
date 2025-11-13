package com.bookstore.jpa.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bookstore.jpa.Mappings.interfaces.PublisherMapper;
import com.bookstore.jpa.dtos.records.Requests.PublisherResquest.PublisherCreateRequest;
import com.bookstore.jpa.dtos.records.Responses.PublisherResponse;
import com.bookstore.jpa.models.PublisherModel;
import com.bookstore.jpa.repositories.PublisherRepository;
import com.bookstore.jpa.services.interfaces.PublisherService;

import jakarta.persistence.EntityNotFoundException;

@Service
@SuppressWarnings("null")
public class PublisherServiceImp implements PublisherService{

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    private static final Logger log = LoggerFactory.getLogger(PublisherServiceImp.class);

    
    public PublisherServiceImp(PublisherRepository publisherRepository, PublisherMapper publisherMapper){
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
    }

    @Override
    public PublisherResponse savePublisher(PublisherCreateRequest publisherRequest){
        log.info("Iniciando tentativa de salvar publisher como nome {}", publisherRequest.name());

        var publisher = new PublisherModel();
        publisher.setName(publisherRequest.name());

        var savedPublisher = publisherRepository.save(publisher);

        log.info("Publisher com ID: {} salvo com sucesso", savedPublisher.getId());

        return publisherMapper.toDto(savedPublisher);
    }

    @Override
    public List<PublisherResponse> getAllPublisher(){
        log.info("Iniando a busca por todas as Publishers...");

        var publishers = publisherRepository.findAll();

        log.info("Encontrado todas as Publishers {}", publishers.size());

        return publishers
        .stream()
        .map(publisherMapper::toDto)
        .collect(Collectors.toList());
    }

    @Override
    public PublisherResponse getPublisherById(UUID id) {
        log.info("Iniciando busca de Publisher com ID: {}", id);

        var publisher = publisherRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Publisher com ID: " + id + " não encontrada"));
        
        log.info("Encontrado Publisher com o ID: {}", id);
        
        return publisherMapper.toDto(publisher);
    }

 
}
