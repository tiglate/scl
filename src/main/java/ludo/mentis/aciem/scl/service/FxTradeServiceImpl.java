package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.domain.FxSettlement;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.FxTradeDTO;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.FxSettlementRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class FxTradeServiceImpl implements FxTradeService {

    private final FxTradeRepository fxTradeRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;
    private final FxSettlementRepository fxSettlementRepository;

    public FxTradeServiceImpl(final FxTradeRepository fxTradeRepository,
            final CounterpartyRepository counterpartyRepository,
            final CurrencyRepository currencyRepository, final UserRepository userRepository,
            final FxSettlementRepository fxSettlementRepository) {
        this.fxTradeRepository = fxTradeRepository;
        this.counterpartyRepository = counterpartyRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
        this.fxSettlementRepository = fxSettlementRepository;
    }

    @Override
    public Page<FxTradeDTO> findAll(final String filter, final Pageable pageable) {
        Page<FxTrade> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = fxTradeRepository.findAllById(longFilter, pageable);
        } else {
            page = fxTradeRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(fxTrade -> mapToDTO(fxTrade, new FxTradeDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    @Override
    public FxTradeDTO get(final Long id) {
        return fxTradeRepository.findById(id)
                .map(fxTrade -> mapToDTO(fxTrade, new FxTradeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final FxTradeDTO fxTradeDTO) {
        final FxTrade fxTrade = new FxTrade();
        mapToEntity(fxTradeDTO, fxTrade);
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
        fxTradeDTO.setCounterparty(fxTrade.getCounterparty() == null ? null : fxTrade.getCounterparty().getId());
        fxTradeDTO.setBuyCurrency(fxTrade.getBuyCurrency() == null ? null : fxTrade.getBuyCurrency().getId());
        fxTradeDTO.setSellCurrency(fxTrade.getSellCurrency() == null ? null : fxTrade.getSellCurrency().getId());
        fxTradeDTO.setUpdatedBy(fxTrade.getUpdatedBy() == null ? null : fxTrade.getUpdatedBy().getId());
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
        final Counterparty counterparty = fxTradeDTO.getCounterparty() == null ? null : counterpartyRepository.findById(fxTradeDTO.getCounterparty())
                .orElseThrow(() -> new NotFoundException("counterparty not found"));
        fxTrade.setCounterparty(counterparty);
        final Currency buyCurrency = fxTradeDTO.getBuyCurrency() == null ? null : currencyRepository.findById(fxTradeDTO.getBuyCurrency())
                .orElseThrow(() -> new NotFoundException("buyCurrency not found"));
        fxTrade.setBuyCurrency(buyCurrency);
        final Currency sellCurrency = fxTradeDTO.getSellCurrency() == null ? null : currencyRepository.findById(fxTradeDTO.getSellCurrency())
                .orElseThrow(() -> new NotFoundException("sellCurrency not found"));
        fxTrade.setSellCurrency(sellCurrency);
        final User updatedBy = fxTradeDTO.getUpdatedBy() == null ? null : userRepository.findById(fxTradeDTO.getUpdatedBy())
                .orElseThrow(() -> new NotFoundException("updatedBy not found"));
        fxTrade.setUpdatedBy(updatedBy);
        return fxTrade;
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final FxTrade fxTrade = fxTradeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final FxSettlement tradeFxSettlement = fxSettlementRepository.findFirstByTrade(fxTrade);
        if (tradeFxSettlement != null) {
            referencedWarning.setKey("fxTrade.fxSettlement.trade.referenced");
            referencedWarning.addParam(tradeFxSettlement.getId());
            return referencedWarning;
        }
        return null;
    }

}
