package ludo.mentis.aciem.scl.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.model.CounterpartyDTO;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.DocumentRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;

@Service
@Transactional(rollbackFor = Exception.class)
public class CounterpartyServiceImpl implements CounterpartyService {

    private final CounterpartyRepository counterpartyRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final FxTradeRepository fxTradeRepository;

    public CounterpartyServiceImpl(final CounterpartyRepository counterpartyRepository,
            final UserRepository userRepository, final DocumentRepository documentRepository,
            final FxTradeRepository fxTradeRepository) {
        this.counterpartyRepository = counterpartyRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.fxTradeRepository = fxTradeRepository;
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
        counterpartyDTO.setDocuments(counterparty.getDocuments().stream()
                .map(Document::getId)
                .toList());
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

        final var documents = documentRepository
        		.findAllById(counterpartyDTO.getDocuments() == null ? List.of() : counterpartyDTO.getDocuments());
        if (documents.size() != (counterpartyDTO.getDocuments() == null ? 0 : counterpartyDTO.getDocuments().size())) {
            throw new NotFoundException("one of documents not found");
        }
        counterparty.setDocuments(new HashSet<>(documents));
        return counterparty;
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final var referencedWarning = new ReferencedWarning();
        final var counterparty = counterpartyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final var counterpartyFxTrade = fxTradeRepository.findFirstByCounterparty(counterparty);
        if (counterpartyFxTrade != null) {
            referencedWarning.setKey("counterparty.trade.counterparty.referenced");
            referencedWarning.addParam(counterpartyFxTrade.getId());
            return referencedWarning;
        }
        return null;
    }
}