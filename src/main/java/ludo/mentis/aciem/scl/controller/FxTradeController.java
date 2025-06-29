package ludo.mentis.aciem.scl.controller;

import jakarta.validation.Valid;
import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Currency;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.FxTradeDTO;
import ludo.mentis.aciem.scl.model.FxTradePurpose;
import ludo.mentis.aciem.scl.model.Product;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.service.FxTradeService;
import ludo.mentis.aciem.scl.util.CustomCollectors;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import ludo.mentis.aciem.scl.util.UserRoles;
import ludo.mentis.aciem.scl.util.WebUtils;
import org.springframework.data.domain.Page;
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


@Controller
@RequestMapping("/fxTrades")
public class FxTradeController {

    private final FxTradeService fxTradeService;
    private final CounterpartyRepository counterpartyRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    public FxTradeController(final FxTradeService fxTradeService,
            final CounterpartyRepository counterpartyRepository,
            final CurrencyRepository currencyRepository, final UserRepository userRepository) {
        this.fxTradeService = fxTradeService;
        this.counterpartyRepository = counterpartyRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("productValues", Product.values());
        model.addAttribute("purposeValues", FxTradePurpose.values());
        model.addAttribute("counterpartyValues", counterpartyRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Counterparty::getId, Counterparty::getLongName)));
        model.addAttribute("buyCurrencyValues", currencyRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Currency::getId, Currency::getIsoCode)));
        model.addAttribute("sellCurrencyValues", currencyRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Currency::getId, Currency::getIsoCode)));
        model.addAttribute("updatedByValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getEmail)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + UserRoles.TRADE_READ + "', '" + UserRoles.TRADE_WRITE + "')")
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<FxTradeDTO> fxTrades = fxTradeService.findAll(filter, pageable);
        model.addAttribute("fxTrades", fxTrades);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(fxTrades));
        return "fxTrade/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String add(@ModelAttribute("fxTrade") final FxTradeDTO fxTradeDTO) {
        return "fxTrade/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String add(@ModelAttribute("fxTrade") @Valid final FxTradeDTO fxTradeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "fxTrade/add";
        }
        fxTradeService.create(fxTradeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("fxTrade.create.success"));
        return "redirect:/fxTrades";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("fxTrade", fxTradeService.get(id));
        return "fxTrade/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("fxTrade") @Valid final FxTradeDTO fxTradeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "fxTrade/edit";
        }
        fxTradeService.update(id, fxTradeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("fxTrade.update.success"));
        return "redirect:/fxTrades";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.TRADE_WRITE + "')")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = fxTradeService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            fxTradeService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("fxTrade.delete.success"));
        }
        return "redirect:/fxTrades";
    }

}
