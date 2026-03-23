package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.model.CurrencyDTO;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.exception.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final FxTradeRepository fxTradeRepository;

    public CurrencyServiceImpl(final CurrencyRepository currencyRepository,
            final FxTradeRepository fxTradeRepository) {
        this.currencyRepository = currencyRepository;
        this.fxTradeRepository = fxTradeRepository;
    }

    @Override
    public Page<CurrencyDTO> findAll(CurrencyDTO searchDTO, Pageable pageable) {
        return currencyRepository.findAllBySearchCriteria(
                searchDTO.getName(),
                searchDTO.getIsoCode(),
                searchDTO.getBacenCode(),
                pageable
        );
    }

    @Override
    public CurrencyDTO get(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null when retrieving an entity.");
        }
        return currencyRepository.findById(id)
                .map(currency -> mapToDTO(currency, new CurrencyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final CurrencyDTO currencyDTO) {
        var currency = mapToEntity(currencyDTO);
        return currencyRepository.save(currency).getId();
    }

    @Override
    public void update(final Long id, final CurrencyDTO currencyDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null when updating an entity.");
        }
        final Currency currency = currencyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(currencyDTO, currency);
        currencyRepository.save(currency);
    }

    @Override
    public void delete(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        currencyRepository.deleteById(id);
    }

    private CurrencyDTO mapToDTO(final Currency currency, final CurrencyDTO currencyDTO) {
        currencyDTO.setId(currency.getId());
        currencyDTO.setName(currency.getName());
        currencyDTO.setIsoCode(currency.getIsoCode());
        currencyDTO.setBacenCode(currency.getBacenCode());
        currencyDTO.setEndDate(currency.getEndDate());
        currencyDTO.setCreatedAt(currency.getCreatedAt());
        currencyDTO.setUpdatedAt(currency.getUpdatedAt());
        return currencyDTO;
    }

    private Currency mapToEntity(final CurrencyDTO currencyDTO) {
        return mapToEntity(currencyDTO, new Currency());
    }

    private Currency mapToEntity(final CurrencyDTO currencyDTO, final Currency currency) {
        currency.setName(currencyDTO.getName());
        currency.setIsoCode(currencyDTO.getIsoCode() != null ? currencyDTO.getIsoCode().toUpperCase() : currencyDTO.getIsoCode());
        currency.setBacenCode(currencyDTO.getBacenCode());
        currency.setEndDate(currencyDTO.getEndDate());
        return currency;
    }

    @Override
    public boolean isoCodeExists(final String isoCode) {
        return currencyRepository.existsByIsoCodeIgnoreCase(isoCode);
    }

    @Override
    public boolean bacenCodeExists(final String bacenCode) {
        return currencyRepository.existsByBacenCodeIgnoreCase(bacenCode);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null when checking for references.");
        }
        final var referencedWarning = new ReferencedWarning();
        final var currency = currencyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final var buyCurrencyFxTrade = fxTradeRepository.findFirstByBuyCurrency(currency);
        if (buyCurrencyFxTrade != null) {
            referencedWarning.setMessage("This entity is still referenced by Fx Trade %d via field Buy Currency.", buyCurrencyFxTrade.getId());
            return referencedWarning;
        }
        final var sellCurrencyFxTrade = fxTradeRepository.findFirstBySellCurrency(currency);
        if (sellCurrencyFxTrade != null) {
            referencedWarning.setMessage("This entity is still referenced by Fx Trade %d via field Sell Currency.", sellCurrencyFxTrade.getId());
            return referencedWarning;
        }
        return null;
    }

}