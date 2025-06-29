package ludo.mentis.aciem.scl.controller;

import jakarta.validation.Valid;
import ludo.mentis.aciem.scl.domain.FxSettlementStep;
import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.FxSettlementDTO;
import ludo.mentis.aciem.scl.model.FxSettlementFailure;
import ludo.mentis.aciem.scl.repos.FxSettlementStepRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.service.FxSettlementService;
import ludo.mentis.aciem.scl.util.CustomCollectors;
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
@RequestMapping("/fxSettlements")
public class FxSettlementController {

    private final FxSettlementService fxSettlementService;
    private final FxSettlementStepRepository fxSettlementStepRepository;
    private final FxTradeRepository fxTradeRepository;
    private final UserRepository userRepository;

    public FxSettlementController(final FxSettlementService fxSettlementService,
            final FxSettlementStepRepository fxSettlementStepRepository,
            final FxTradeRepository fxTradeRepository, final UserRepository userRepository) {
        this.fxSettlementService = fxSettlementService;
        this.fxSettlementStepRepository = fxSettlementStepRepository;
        this.fxTradeRepository = fxTradeRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("failureMotiveValues", FxSettlementFailure.values());
        model.addAttribute("stepsValues", fxSettlementStepRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(FxSettlementStep::getId, FxSettlementStep::getId)));
        model.addAttribute("tradeValues", fxTradeRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(FxTrade::getId, FxTrade::getId)));
        model.addAttribute("completedByValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getEmail)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + UserRoles.SETTLEMENT_READ + "', '" + UserRoles.SETTLEMENT_WRITE + "')")
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<FxSettlementDTO> fxSettlements = fxSettlementService.findAll(filter, pageable);
        model.addAttribute("fxSettlements", fxSettlements);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(fxSettlements));
        return "fxSettlement/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRoles.SETTLEMENT_WRITE + "')")
    public String add(@ModelAttribute("fxSettlement") final FxSettlementDTO fxSettlementDTO) {
        return "fxSettlement/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRoles.SETTLEMENT_WRITE + "')")
    public String add(@ModelAttribute("fxSettlement") @Valid final FxSettlementDTO fxSettlementDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "fxSettlement/add";
        }
        fxSettlementService.create(fxSettlementDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("fxSettlement.create.success"));
        return "redirect:/fxSettlements";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.SETTLEMENT_WRITE + "')")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("fxSettlement", fxSettlementService.get(id));
        return "fxSettlement/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.SETTLEMENT_WRITE + "')")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("fxSettlement") @Valid final FxSettlementDTO fxSettlementDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "fxSettlement/edit";
        }
        fxSettlementService.update(id, fxSettlementDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("fxSettlement.update.success"));
        return "redirect:/fxSettlements";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('" + UserRoles.SETTLEMENT_WRITE + "')")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        fxSettlementService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("fxSettlement.delete.success"));
        return "redirect:/fxSettlements";
    }

}
