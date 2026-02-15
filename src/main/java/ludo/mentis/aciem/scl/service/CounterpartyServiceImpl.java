package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.model.CounterpartyDTO;
import ludo.mentis.aciem.scl.model.DocumentDTO;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.DocumentRepository;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;

@Service
@Transactional(rollbackFor = Exception.class)
public class CounterpartyServiceImpl implements CounterpartyService {

    private final FxTradeRepository fxTradeRepository;
    private final DocumentRepository documentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final CounterpartyRepository counterpartyRepository;

    public CounterpartyServiceImpl(final FxTradeRepository fxTradeRepository,
    		                       final DocumentRepository documentRepository,
    		                       final DocumentTypeRepository documentTypeRepository,
    		                       final CounterpartyRepository counterpartyRepository) {
    	this.fxTradeRepository = fxTradeRepository;
    	this.documentRepository = documentRepository;
    	this.documentTypeRepository = documentTypeRepository;
    	this.counterpartyRepository = counterpartyRepository;
    }

    @Override
    public Page<CounterpartyDTO> findAll(CounterpartyDTO searchDTO, Pageable pageable) {
        return counterpartyRepository.findAllBySearchCriteria(
                searchDTO.getOriginId(),
                searchDTO.getShortName(),
                searchDTO.getLongName(),
                searchDTO.getIsActive(),
                pageable
        );
    }

    @Override
    public CounterpartyDTO get(final Long id) {
        return counterpartyRepository.findById(id)
                .map(counterparty -> mapToDTO(counterparty, new CounterpartyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final CounterpartyDTO counterpartyDTO) {
        var counterparty = mapToEntity(counterpartyDTO);
        return counterpartyRepository.save(counterparty).getId();
    }

    @Override
    public void update(final Long id, final CounterpartyDTO counterpartyDTO) {
        final Counterparty counterparty = counterpartyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(counterpartyDTO, counterparty);
        counterpartyRepository.save(counterparty);
    }

    @Override
    public void delete(final Long id) {
        counterpartyRepository.deleteById(id);
    }

    private CounterpartyDTO mapToDTO(final Counterparty counterparty, final CounterpartyDTO counterpartyDTO) {
        counterpartyDTO.setId(counterparty.getId());
        counterpartyDTO.setOriginId(counterparty.getOriginId());
        counterpartyDTO.setLongName(counterparty.getLongName());
        counterpartyDTO.setShortName(counterparty.getShortName());
        counterpartyDTO.setIsActive(counterparty.getIsActive());
        counterpartyDTO.setCreatedAt(counterparty.getCreatedAt());
        counterpartyDTO.setUpdatedAt(counterparty.getUpdatedAt());
        if (counterparty.getUpdatedBy() != null) {
            counterpartyDTO.setUpdatedById(counterparty.getUpdatedBy().getId());
            counterpartyDTO.setUpdatedByName(counterparty.getUpdatedBy().getName());
        }
        counterpartyDTO.setDocuments(new ArrayList<>());
        for (var doc : counterparty.getDocuments()) {
        	var docDTO = new DocumentDTO();
        	docDTO.setId(doc.getId());
        	docDTO.setAction("update");
        	docDTO.setValue(doc.getValue());
        	docDTO.setExpiration(doc.getExpiration());
        	docDTO.setDocumentTypeId(doc.getDocumentType().getId());
        	counterpartyDTO.getDocuments().add(docDTO);
        }
        return counterpartyDTO;
    }

    private Counterparty mapToEntity(final CounterpartyDTO counterpartyDTO) {
        return mapToEntity(counterpartyDTO, new Counterparty());
    }

    private Counterparty mapToEntity(final CounterpartyDTO counterpartyDTO, final Counterparty counterparty) {
        counterparty.setOriginId(counterpartyDTO.getOriginId());
        counterparty.setLongName(counterpartyDTO.getLongName());
        counterparty.setShortName(counterpartyDTO.getShortName());
        counterparty.setIsActive(counterpartyDTO.getIsActive());
        counterparty.setCreatedAt(counterpartyDTO.getCreatedAt());
        counterparty.setUpdatedAt(counterpartyDTO.getUpdatedAt());
        counterparty.setDocuments(new HashSet<>());
        
        for (DocumentDTO docDTO : counterpartyDTO.getDocuments()) {
        	if ("delete".equals(docDTO.getAction())) {
        		documentRepository.deleteById(docDTO.getId());
        		continue;
        	}
        	var doc = "new".equals(docDTO.getAction())
        			? new Document()
        			: documentRepository.findById(docDTO.getId()).orElseThrow(NotFoundException::new);
        	var documentType = documentTypeRepository
        			.findById(docDTO.getDocumentTypeId())
        			.orElseThrow(NotFoundException::new);
        	doc.setValue(docDTO.getValue());
        	doc.setDocumentType(documentType);
        	doc.setCounterparty(counterparty);
        	doc.setExpiration(docDTO.getExpiration());
        	counterparty.getDocuments().add(doc);
        }
        
        return counterparty;
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final var referencedWarning = new ReferencedWarning();
        final var counterparty = counterpartyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final var counterpartyFxTrade = fxTradeRepository.findFirstByCounterparty(counterparty);
        if (counterpartyFxTrade != null) {
            referencedWarning.setMessage("This entity is still referenced by Fx Trade %d via field Counterparty.", counterpartyFxTrade.getId());
            return referencedWarning;
        }
        return null;
    }
}