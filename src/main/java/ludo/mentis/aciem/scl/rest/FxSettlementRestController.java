package ludo.mentis.aciem.scl.rest;

import jakarta.validation.Valid;
import ludo.mentis.aciem.scl.domain.FxSettlementView;
import ludo.mentis.aciem.scl.model.CustomUserDetails;
import ludo.mentis.aciem.scl.model.FxSettlementHistoryDTO;
import ludo.mentis.aciem.scl.model.FxSettlementStepDTO;
import ludo.mentis.aciem.scl.service.FxSettlementService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/fxSettlements")
public class FxSettlementRestController {

    private final FxSettlementService fxSettlementService;

    public FxSettlementRestController(FxSettlementService fxSettlementService) {
        this.fxSettlementService = fxSettlementService;
    }

    @GetMapping("/steps")
    public ResponseEntity<Iterable<FxSettlementView>> list(
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate) {
        if (startDate == null) {
            startDate = fxSettlementService.getLastTradeDate();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return ResponseEntity.ok(fxSettlementService.findAllBySearchCriteria(startDate, endDate));
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<FxSettlementHistoryDTO> viewStep(@PathVariable final Long id) {
        return null;
    }

    @GetMapping("/lastTradeDate")
    public LocalDate getLastTradeDate() {
        return fxSettlementService.getLastTradeDate();
    }

    @PostMapping(value = "/step", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> save(@RequestPart(value = "file", required = false) MultipartFile file,
                                       @RequestPart("details") @Valid final FxSettlementStepDTO dto,
                                       Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication is required");
        }
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(401).body("Invalid Principal. Authentication is required");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        dto.setUserId(userDetails.getId());

        try {
            this.fxSettlementService.save(dto);
            return ResponseEntity.ok("Settlement step processed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing settlement step: " + e.getMessage());
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<Iterable<FxSettlementHistoryDTO>> getHistory(@PathVariable final Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(fxSettlementService.getHistoryByFxTradeId(id));
    }
}
