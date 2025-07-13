package ludo.mentis.aciem.scl.controller;


import static java.util.Map.entry;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.FxTradeDTO;
import ludo.mentis.aciem.scl.model.FxTradePurpose;
import ludo.mentis.aciem.scl.model.FxTradeSearchDTO;
import ludo.mentis.aciem.scl.model.Product;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.service.FxTradeService;
import ludo.mentis.aciem.scl.util.CustomCollectors;
import ludo.mentis.aciem.scl.util.FlashMessages;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import ludo.mentis.aciem.scl.util.SortUtils;
import ludo.mentis.aciem.scl.util.UserRoles;
import ludo.mentis.aciem.scl.util.WebUtils;

@Controller
@RequestMapping("/fxTrades")
public class FxTradeController {

    private static final String ENTITY_NAME = "FxTrade";
    private static final String CONTROLLER_ADD = "fxtrade/add";
    private static final String CONTROLLER_EDIT = "fxtrade/edit";
    private static final String CONTROLLER_VIEW = "fxtrade/view";
    private static final String CONTROLLER_LIST = "fxtrade/list";
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/fxtrades";
    
    private final SortUtils sortUtils;
    private final FxTradeService fxTradeService;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final CounterpartyRepository counterpartyRepository;

    public FxTradeController(final FxTradeService fxTradeService,
                             final UserRepository userRepository,
                             final CurrencyRepository currencyRepository,
                             final CounterpartyRepository counterpartyRepository) {
        this.sortUtils = new SortUtils();
        this.fxTradeService = fxTradeService;
        this.userRepository = userRepository;
        this.currencyRepository = currencyRepository;
        this.counterpartyRepository = counterpartyRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("productValues", Product.values());
        model.addAttribute("purposeValues", FxTradePurpose.values());
        model.addAttribute("counterpartyValues", counterpartyRepository.findAll(Sort.by("shortName"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Counterparty::getId, Counterparty::getLongName)));
        model.addAttribute("currencyValues", currencyRepository.findAll(Sort.by("isoCode"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Currency::getId, Currency::getIsoCode)));
        model.addAttribute("userValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getName)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.TRADE_READ + "', '" + UserRoles.TRADE_WRITE + "')")
    public String list(@ModelAttribute("fxtradeSearch") FxTradeSearchDTO filter,
                       @RequestParam(required = false) String sort,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 18) final Pageable pageable,
                       final Model model) {
        if (sort == null) {
            sort = "id,desc";
        }
        final var sortOrder = this.sortUtils.addSortAttributesToModel(model, sort, pageable, Map.ofEntries(
            entry("id"                    , "sortById"),
            entry("tradeId"               , "sortByTradeId"),
            entry("product"               , "sortByProduct"),
            entry("tradeDate"             , "sortByTradeDate"),
            entry("valueDate"             , "sortByValueDate"),
            entry("buyCurrencyIso"        , "sortByCurrencyBought"),
            entry("buyAmount"             , "sortByAmountBought"),
            entry("sellCurrencyIso"       , "sortByCurrencySold"),
            entry("sellAmount"            , "sortByAmountSold"),
            entry("purpose"               , "sortByPurpose"),
            entry("exchangeRate"          , "sortByExchangeRate"),
            entry("counterpartyShortName" , "sortByCounterparty"),
            entry("investorManager"       , "sortByInvestorManager"),
            entry("beneficiary"           , "sortByBeneficiary"),
            entry("updatedByName"         , "sortByUpdatedBy")
        ));
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var fxTrades = fxTradeService.findAll(filter, pageRequest);
        model.addAttribute("fxTrades", fxTrades);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(fxTrades));
        return CONTROLLER_LIST;
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.TRADE_READ + "', '" + UserRoles.TRADE_WRITE + "')")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("fxTrade", fxTradeService.get(id));
        return CONTROLLER_VIEW;
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.TRADE_READ + "')")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("fxTrade", fxTradeService.get(id));
        return CONTROLLER_EDIT;
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String add(@ModelAttribute("fxTrade") final FxTradeDTO fxtradeDTO) {
        return CONTROLLER_ADD;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String add(@ModelAttribute("fxTrade") @Valid final FxTradeDTO fxtradeDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        fxTradeService.create(fxtradeDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("fxTrade") @Valid final FxTradeDTO fxtradeDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        fxTradeService.update(id, fxtradeDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = fxTradeService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(FlashMessages.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            fxTradeService.delete(id);
            FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        }
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

}
