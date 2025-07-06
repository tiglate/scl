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
import ludo.mentis.aciem.scl.model.CurrencyDTO;
import ludo.mentis.aciem.scl.service.CurrencyService;
import ludo.mentis.aciem.scl.util.FlashMessages;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import ludo.mentis.aciem.scl.util.SortUtils;
import ludo.mentis.aciem.scl.util.UserRoles;
import ludo.mentis.aciem.scl.util.WebUtils;

@Controller
@RequestMapping("/currencies")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class CurrencyController {

    private static final String ENTITY_NAME = "Currency";
    private static final String CONTROLLER_ADD = "currency/add";
    private static final String CONTROLLER_EDIT = "currency/edit";
    private static final String CONTROLLER_VIEW = "currency/view";
    private static final String CONTROLLER_LIST = "currency/list";
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/currencies";
    private final CurrencyService currencyService;
    private final SortUtils sortUtils;

    public CurrencyController(final CurrencyService currencyService) {
        this.currencyService = currencyService;
        this.sortUtils = new SortUtils();
    }

    @GetMapping
    public String list(@ModelAttribute("currencySearch") CurrencyDTO filter,
                       @RequestParam(required = false) String sort,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        if (sort == null) {
            sort = "id,desc";
        }
        final var sortOrder = this.sortUtils.addSortAttributesToModel(model, sort, pageable, Map.ofEntries(
                entry("id", "sortById"),
                entry("name", "sortByName"),
                entry("isoCode", "sortByIsoCode"),
                entry("bacenCode", "sortByBacenCode"),
                entry("endDate", "sortByEndDate")
        ));
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var currencies = currencyService.findAll(filter, pageRequest);
        model.addAttribute("currencies", currencies);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(currencies));
        return CONTROLLER_LIST;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("currency", currencyService.get(id));
        return CONTROLLER_VIEW;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("currency", currencyService.get(id));
        return CONTROLLER_EDIT;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("currency") final CurrencyDTO currencyDTO) {
        return CONTROLLER_ADD;
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("currency") @Valid final CurrencyDTO currencyDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        currencyService.create(currencyDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("currency") @Valid final CurrencyDTO currencyDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        currencyService.update(id, currencyDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = currencyService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(FlashMessages.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            currencyService.delete(id);
            FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        }
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

}
