package ludo.mentis.aciem.scl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.FxTradeView;
import ludo.mentis.aciem.scl.model.FxTradeDTO;
import ludo.mentis.aciem.scl.model.FxTradeSearchDTO;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;

@Service
@Transactional(rollbackFor = Exception.class)
public class FxTradeServiceImpl implements FxTradeService {

    private final UserRepository userRepository;
    private final FxTradeRepository fxTradeRepository;
    private final CurrencyRepository currencyRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final FxSettlementRepository fxSettlementRepository;

    public FxTradeServiceImpl(final UserRepository userRepository,
                              final FxTradeRepository fxTradeRepository,
                              final CurrencyRepository currencyRepository,
                              final CounterpartyRepository counterpartyRepository,
                              final FxSettlementRepository fxSettlementRepository) {
        this.userRepository = userRepository;
        this.fxTradeRepository = fxTradeRepository;
        this.currencyRepository = currencyRepository;
        this.counterpartyRepository = counterpartyRepository;
        this.fxSettlementRepository = fxSettlementRepository;
    }

    @Override
    public Page<FxTradeView> findAll(FxTradeSearchDTO criteria, Pageable pageable) {
        return fxTradeRepository.findAllBySearchCriteria(criteria, pageable);
    }

    @Override
    public FxTradeDTO get(final Long id) {
        return fxTradeRepository.findById(id)
                .map(fxTrade -> mapToDTO(fxTrade, new FxTradeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final FxTradeDTO fxTradeDTO) {
        var fxTrade = mapToEntity(fxTradeDTO);
        return fxTradeRepository.save(fxTrade).getId();
    }

    @Override
    public void update(final Long id, final FxTradeDTO fxTradeDTO) {
        final FxTrade fxTrade = fxTradeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(fxTradeDTO, fxTrade);
        fxTradeRepository.save(fxTrade);
    }

    @Override
    public void delete(final Long id) {
        fxTradeRepository.deleteById(id);
    }

    private FxTrade mapToEntity(final FxTradeDTO fxTradeDTO) {
        return mapToEntity(fxTradeDTO, new FxTrade());
    }

    private FxTradeDTO mapToDTO(final FxTrade fxTrade, final FxTradeDTO fxTradeDTO) {
        fxTradeDTO.setId(fxTrade.getId());
        fxTradeDTO.setTradeId(fxTrade.getTradeId());
        fxTradeDTO.setTradeDate(fxTrade.getTradeDate());
        fxTradeDTO.setValueDate(fxTrade.getValueDate());
        fxTradeDTO.setProduct(fxTrade.getProduct());
        fxTradeDTO.setBuyAmount(fxTrade.getBuyAmount());
        fxTradeDTO.setSellAmount(fxTrade.getSellAmount());
        fxTradeDTO.setInvestorManager(fxTrade.getInvestorManager());
        fxTradeDTO.setBeneficiary(fxTrade.getBeneficiary());
        fxTradeDTO.setPurpose(fxTrade.getPurpose());
        fxTradeDTO.setCreatedAt(fxTrade.getCreatedAt());
        fxTradeDTO.setUpdatedAt(fxTrade.getUpdatedAt());
        fxTradeDTO.setExchangeRate(fxTrade.getExchangeRate());
        fxTradeDTO.setCounterpartyId(fxTrade.getCounterparty() == null ? null : fxTrade.getCounterparty().getId());
        fxTradeDTO.setBuyCurrencyId(fxTrade.getBuyCurrency() == null ? null : fxTrade.getBuyCurrency().getId());
        fxTradeDTO.setSellCurrencyId(fxTrade.getSellCurrency() == null ? null : fxTrade.getSellCurrency().getId());
        fxTradeDTO.setUpdatedById(fxTrade.getUpdatedBy() == null ? null : fxTrade.getUpdatedBy().getId());
        fxTradeDTO.setUpdatedByName(fxTrade.getUpdatedBy() == null ? null : fxTrade.getUpdatedBy().getName());
        return fxTradeDTO;
    }

    private FxTrade mapToEntity(final FxTradeDTO fxTradeDTO, final FxTrade fxTrade) {
        fxTrade.setTradeId(fxTradeDTO.getTradeId());
        fxTrade.setTradeDate(fxTradeDTO.getTradeDate());
        fxTrade.setValueDate(fxTradeDTO.getValueDate());
        fxTrade.setProduct(fxTradeDTO.getProduct());
        fxTrade.setBuyAmount(fxTradeDTO.getBuyAmount());
        fxTrade.setSellAmount(fxTradeDTO.getSellAmount());
        fxTrade.setInvestorManager(fxTradeDTO.getInvestorManager());
        fxTrade.setBeneficiary(fxTradeDTO.getBeneficiary());
        fxTrade.setPurpose(fxTradeDTO.getPurpose());
        fxTrade.setCreatedAt(fxTradeDTO.getCreatedAt());
        fxTrade.setUpdatedAt(fxTradeDTO.getUpdatedAt());
        fxTrade.setExchangeRate(fxTradeDTO.getExchangeRate());
        final var counterparty = fxTradeDTO.getCounterpartyId() == null ? null : counterpartyRepository.findById(fxTradeDTO.getCounterpartyId())
                .orElseThrow(() -> new NotFoundException("counterparty not found"));
        fxTrade.setCounterparty(counterparty);
        final var buyCurrency = fxTradeDTO.getBuyCurrencyId() == null ? null : currencyRepository.findById(fxTradeDTO.getBuyCurrencyId())
                .orElseThrow(() -> new NotFoundException("buyCurrency not found"));
        fxTrade.setBuyCurrency(buyCurrency);
        final var sellCurrency = fxTradeDTO.getSellCurrencyId() == null ? null : currencyRepository.findById(fxTradeDTO.getSellCurrencyId())
                .orElseThrow(() -> new NotFoundException("sellCurrency not found"));
        fxTrade.setSellCurrency(sellCurrency);
        final var updatedBy = fxTradeDTO.getUpdatedById() == null ? null : userRepository.findById(fxTradeDTO.getUpdatedById())
                .orElseThrow(() -> new NotFoundException("updatedBy not found"));
        fxTrade.setUpdatedBy(updatedBy);
        return fxTrade;
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final var referencedWarning = new ReferencedWarning();
        final var fxTrade = fxTradeRepository.findById(id).orElseThrow(NotFoundException::new);
        final var tradeFxSettlement = fxSettlementRepository.findFirstByTrade(fxTrade);
        if (tradeFxSettlement != null) {
            referencedWarning.setMessage("This entity is still referenced by Fx Settlement %d via field Trade.", tradeFxSettlement.getId());
            return referencedWarning;
        }
        return null;
    }

}