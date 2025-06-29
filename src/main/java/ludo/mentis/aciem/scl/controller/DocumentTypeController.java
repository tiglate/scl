package ludo.mentis.aciem.scl.controller;

import jakarta.validation.Valid;
import ludo.mentis.aciem.scl.model.DocumentTypeDTO;
import ludo.mentis.aciem.scl.service.DocumentTypeService;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import ludo.mentis.aciem.scl.util.UserRoles;
import ludo.mentis.aciem.scl.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/documentTypes")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    public DocumentTypeController(final DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<DocumentTypeDTO> documentTypes = documentTypeService.findAll(filter, pageable);
        model.addAttribute("documentTypes", documentTypes);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(documentTypes));
        return "documentType/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("documentType") final DocumentTypeDTO documentTypeDTO) {
        return "documentType/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("documentType") @Valid final DocumentTypeDTO documentTypeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "documentType/add";
        }
        documentTypeService.create(documentTypeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("documentType.create.success"));
        return "redirect:/documentTypes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("documentType", documentTypeService.get(id));
        return "documentType/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("documentType") @Valid final DocumentTypeDTO documentTypeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "documentType/edit";
        }
        documentTypeService.update(id, documentTypeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("documentType.update.success"));
        return "redirect:/documentTypes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = documentTypeService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            documentTypeService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("documentType.delete.success"));
        }
        return "redirect:/documentTypes";
    }

}
