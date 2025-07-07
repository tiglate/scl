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
import ludo.mentis.aciem.scl.domain.DocumentType;
import ludo.mentis.aciem.scl.model.CounterpartyDTO;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.service.CounterpartyService;
import ludo.mentis.aciem.scl.util.CustomCollectors;
import ludo.mentis.aciem.scl.util.FlashMessages;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import ludo.mentis.aciem.scl.util.SortUtils;
import ludo.mentis.aciem.scl.util.UserRoles;
import ludo.mentis.aciem.scl.util.WebUtils;

@Controller
@RequestMapping("/counterparties")
public class CounterpartyController {

    private static final String ENTITY_NAME = "Counterparty";
    private static final String CONTROLLER_ADD = "counterparty/add";
    private static final String CONTROLLER_EDIT = "counterparty/edit";
    private static final String CONTROLLER_VIEW = "counterparty/view";
    private static final String CONTROLLER_LIST = "counterparty/list";
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/counterparties";
    private final SortUtils sortUtils;
    private final CounterpartyService counterpartyService;
    private final DocumentTypeRepository documentTypeRepository;

    public CounterpartyController(final CounterpartyService counterpartyService,
    		                      final DocumentTypeRepository documentTypeRepository) {
    	this.sortUtils = new SortUtils();
    	this.counterpartyService = counterpartyService;
        this.documentTypeRepository = documentTypeRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("documentTypeValues", documentTypeRepository
        		.findAll(Sort.by("id"))
        		.stream()
        		.collect(CustomCollectors.toSortedMap(DocumentType::getId, DocumentType::getName)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.COUNTERPARTY_READ + "', '" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String list(@ModelAttribute("counterpartySearch") CounterpartyDTO filter,
                       @RequestParam(required = false) String sort,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        if (sort == null) {
            sort = "id,desc";
        }
        final var sortOrder = this.sortUtils.addSortAttributesToModel(model, sort, pageable, Map.ofEntries(
                entry("id", "sortById"),
                entry("originId", "sortByCode"),
                entry("shortName", "sortByShortName"),
                entry("longName", "sortByLongName"),
                entry("isActive", "sortByIsActive")
        ));
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var counterparties = counterpartyService.findAll(filter, pageRequest);
        model.addAttribute("counterparties", counterparties);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(counterparties));
        return CONTROLLER_LIST;
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.COUNTERPARTY_READ + "', '" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("counterparty", counterpartyService.get(id));
        return CONTROLLER_VIEW;
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("counterparty", counterpartyService.get(id));
        return CONTROLLER_EDIT;
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String add(@ModelAttribute("counterparty") final CounterpartyDTO counterpartyDTO) {
    	counterpartyDTO.setIsActive(true);
        return CONTROLLER_ADD;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String add(@ModelAttribute("counterparty") @Valid final CounterpartyDTO counterpartyDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        counterpartyService.create(counterpartyDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("counterparty") @Valid final CounterpartyDTO counterpartyDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        counterpartyService.update(id, counterpartyDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = counterpartyService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(FlashMessages.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            counterpartyService.delete(id);
            FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        }
        return REDIRECT_TO_CONTROLLER_INDEX;
    }
}
