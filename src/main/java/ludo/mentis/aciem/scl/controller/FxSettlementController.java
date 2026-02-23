package ludo.mentis.aciem.scl.controller;

import ludo.mentis.aciem.scl.service.FxSettlementService;
import ludo.mentis.aciem.scl.util.NotFoundException;
import ludo.mentis.aciem.scl.util.UserRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLConnection;
import java.sql.SQLException;
import java.util.UUID;


@Controller
@RequestMapping("/fxSettlements")
public class FxSettlementController {

    private final FxSettlementService fxSettlementService;
    private static final Logger log = LoggerFactory.getLogger(FxSettlementController.class);

    public FxSettlementController(final FxSettlementService fxSettlementService) {
        this.fxSettlementService = fxSettlementService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.SETTLEMENT_READ + "', '" + UserRoles.SETTLEMENT_WRITE + "')")
    public String list() {
        return "fxSettlement/list";
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.SETTLEMENT_READ + "', '" + UserRoles.SETTLEMENT_WRITE + "')")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {
        final var fileContent = fxSettlementService.getFile(id);
        final var blob = fileContent.getContent();

        if (blob == null) {
            throw new NotFoundException("File not found for ID: " + id);
        }

        try {
            final var inputStreamResource = new InputStreamResource(blob.getBinaryStream());
            var contentType = URLConnection.guessContentTypeFromName(fileContent.getFileName());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileContent.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(blob.length())
                    .body(inputStreamResource);
        } catch (final SQLException ex) {
            log.error("Error providing file download for ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
