package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.model.FxSettlementSearchDTO;
import ludo.mentis.aciem.scl.service.FxSettlementService;
import ludo.mentis.aciem.scl.util.UserRoles;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;


@Controller
@RequestMapping("/fxSettlements")
public class FxSettlementController {

    private final FxSettlementService fxSettlementService;

    public FxSettlementController(final FxSettlementService fxSettlementService) {
        this.fxSettlementService = fxSettlementService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.SETTLEMENT_READ + "', '" + UserRoles.SETTLEMENT_WRITE + "')")
    public String list(@ModelAttribute("fxSettlementSearch") FxSettlementSearchDTO filter, final Model model) {
        if (filter == null) {
            filter = new FxSettlementSearchDTO();
        }
        if (filter.getStartDate() == null) {
            filter.setStartDate(fxSettlementService.getLastTradeDate());
        }
        if (filter.getEndDate() == null) {
            filter.setEndDate(LocalDate.now());
        }
        final var fxSettlements = fxSettlementService.findAllBySearchCriteria(filter.getStartDate(), filter.getEndDate());
        model.addAttribute("fxSettlements", fxSettlements);
        return "fxSettlement/list";
    }
}
