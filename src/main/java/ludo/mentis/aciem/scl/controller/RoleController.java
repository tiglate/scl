package ludo.mentis.aciem.scl.controller;


import jakarta.validation.Valid;
import ludo.mentis.aciem.scl.model.RoleDTO;
import ludo.mentis.aciem.scl.service.RoleService;
import ludo.mentis.aciem.scl.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static java.util.Map.entry;

@Controller
@RequestMapping("/roles")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class RoleController {

    private static final String ENTITY_NAME = "Role";
    private static final String CONTROLLER_ADD = "role/add";
    private static final String CONTROLLER_EDIT = "role/edit";
    private static final String CONTROLLER_VIEW = "role/view";
    private static final String CONTROLLER_LIST = "role/list";
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/roles";
    private final RoleService roleService;
    private final SortUtils sortUtils;

    public RoleController(final RoleService roleService) {
        this.roleService = roleService;
        this.sortUtils = new SortUtils();
    }

    @GetMapping
    public String list(@ModelAttribute("roleSearch") RoleDTO filter,
                       @RequestParam(required = false) String sort,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        if (sort == null) {
            sort = "id,desc";
        }
        final var sortOrder = this.sortUtils.addSortAttributesToModel(model, sort, pageable, Map.ofEntries(
                entry("id", "sortById"),
                entry("code", "sortByCode"),
                entry("description", "sortByDescription")
        ));
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var roles = roleService.findAll(filter, pageRequest);
        model.addAttribute("roles", roles);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(roles));
        return CONTROLLER_LIST;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("role", roleService.get(id));
        return CONTROLLER_VIEW;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("role", roleService.get(id));
        return CONTROLLER_EDIT;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("role") final RoleDTO roleDTO) {
        return CONTROLLER_ADD;
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("role") @Valid final RoleDTO roleDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        roleService.create(roleDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("role") @Valid final RoleDTO roleDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        roleService.update(id, roleDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = roleService.getReferencedWarning(id);
        if (referencedWarning != null) {
            FlashMessages.referencedWarning(redirectAttributes, referencedWarning);
        } else {
            roleService.delete(id);
            FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        }
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

}
