package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.model.CurrencyDTO;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public Page<CurrencyDTO> findAll(final String filter, final Pageable pageable) {
        Page<Currency> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = currencyRepository.findAllById(longFilter, pageable);
        } else {
            page = currencyRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(currency -> mapToDTO(currency, new CurrencyDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    @Override
    public CurrencyDTO get(final Long id) {
        return currencyRepository.findById(id)
                .map(currency -> mapToDTO(currency, new CurrencyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final CurrencyDTO currencyDTO) {
        final Currency currency = new Currency();
        mapToEntity(currencyDTO, currency);
        return currencyRepository.save(currency).getId();
    }

    @Override
    public void update(final Long id, final CurrencyDTO currencyDTO) {
        final Currency currency = currencyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(currencyDTO, currency);
        currencyRepository.save(currency);
    }

    @Override
    public void delete(final Long id) {
        currencyRepository.deleteById(id);
    }

    private CurrencyDTO mapToDTO(final Currency currency, final CurrencyDTO currencyDTO) {
        currencyDTO.setId(currency.getId());
        currencyDTO.setIsoCode(currency.getIsoCode());
        currencyDTO.setBacenCode(currency.getBacenCode());
        currencyDTO.setName(currency.getName());
        return currencyDTO;
    }

    private Currency mapToEntity(final CurrencyDTO currencyDTO, final Currency currency) {
        currency.setIsoCode(currencyDTO.getIsoCode());
        currency.setBacenCode(currencyDTO.getBacenCode());
        currency.setName(currencyDTO.getName());
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
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Currency currency = currencyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final FxTrade buyCurrencyFxTrade = fxTradeRepository.findFirstByBuyCurrency(currency);
        if (buyCurrencyFxTrade != null) {
            referencedWarning.setKey("currency.fxTrade.buyCurrency.referenced");
            referencedWarning.addParam(buyCurrencyFxTrade.getId());
            return referencedWarning;
        }
        final FxTrade sellCurrencyFxTrade = fxTradeRepository.findFirstBySellCurrency(currency);
        if (sellCurrencyFxTrade != null) {
            referencedWarning.setKey("currency.fxTrade.sellCurrency.referenced");
            referencedWarning.addParam(sellCurrencyFxTrade.getId());
            return referencedWarning;
        }
        return null;
    }

}
