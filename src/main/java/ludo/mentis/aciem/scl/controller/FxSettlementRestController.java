package ludo.mentis.aciem.scl.controller;

import jakarta.validation.Valid;
import ludo.mentis.aciem.scl.model.FxSettlementStepDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fxSettlements")
public class FxSettlementRestController {

    @PostMapping("/step")
    public ResponseEntity<String> processSettlementStep(@RequestBody @Valid final FxSettlementStepDTO fxSettlementStepDTO) {
        try {
            // Logic will be added later
            return ResponseEntity.ok("Settlement step processed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing settlement step: " + e.getMessage());
        }
    }
}
