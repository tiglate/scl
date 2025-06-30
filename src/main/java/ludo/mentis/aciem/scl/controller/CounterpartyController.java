package ludo.mentis.aciem.scl.controller;

import jakarta.validation.Valid;
import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.CounterpartyDTO;
import ludo.mentis.aciem.scl.repos.DocumentRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.service.CounterpartyService;
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
@RequestMapping("/counterparties")
public class CounterpartyController {

    private final CounterpartyService counterpartyService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public CounterpartyController(final CounterpartyService counterpartyService,
            final UserRepository userRepository, final DocumentRepository documentRepository) {
        this.counterpartyService = counterpartyService;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("updatedByValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getEmail)));
        model.addAttribute("documentsValues", documentRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Document::getId, Document::getValue)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.COUNTERPARTY_READ + "', '" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<CounterpartyDTO> counterparties = counterpartyService.findAll(filter, pageable);
        model.addAttribute("counterparties", counterparties);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(counterparties));
        return "counterparty/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String add(@ModelAttribute("counterparty") final CounterpartyDTO counterpartyDTO) {
        return "counterparty/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String add(@ModelAttribute("counterparty") @Valid final CounterpartyDTO counterpartyDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "counterparty/add";
        }
        counterpartyService.create(counterpartyDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("counterparty.create.success"));
        return "redirect:/counterparties";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("counterparty", counterpartyService.get(id));
        return "counterparty/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("counterparty") @Valid final CounterpartyDTO counterpartyDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "counterparty/edit";
        }
        counterpartyService.update(id, counterpartyDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("counterparty.update.success"));
        return "redirect:/counterparties";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.COUNTERPARTY_WRITE + "')")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = counterpartyService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            counterpartyService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("counterparty.delete.success"));
        }
        return "redirect:/counterparties";
    }

}
