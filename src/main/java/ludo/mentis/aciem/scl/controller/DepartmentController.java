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
import ludo.mentis.aciem.scl.model.DepartmentDTO;
import ludo.mentis.aciem.scl.service.DepartmentService;
import ludo.mentis.aciem.scl.util.FlashMessages;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import ludo.mentis.aciem.scl.util.SortUtils;
import ludo.mentis.aciem.scl.util.UserRoles;
import ludo.mentis.aciem.scl.util.WebUtils;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private static final String ENTITY_NAME = "Department";
    private static final String CONTROLLER_ADD = "department/add";
    private static final String CONTROLLER_EDIT = "department/edit";
    private static final String CONTROLLER_VIEW = "department/view";
    private static final String CONTROLLER_LIST = "department/list";
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/departments";
    private final DepartmentService departmentService;
    private final SortUtils sortUtils;

    public DepartmentController(final DepartmentService departmentService) {
        this.departmentService = departmentService;
        this.sortUtils = new SortUtils();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.DEPARTMENT_READ + "', '" + UserRoles.DEPARTMENT_WRITE + "')")
    public String list(@ModelAttribute("departmentSearch") DepartmentDTO filter,
                       @RequestParam(required = false) String sort,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        if (sort == null) {
            sort = "id,desc";
        }
        final var sortOrder = this.sortUtils.addSortAttributesToModel(model, sort, pageable, Map.ofEntries(
                entry("id", "sortById"),
                entry("name", "sortByName"),
                entry("email", "sortByEmail")
        ));
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var departments = departmentService.findAll(filter, pageRequest);
        model.addAttribute("departments", departments);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(departments));
        return CONTROLLER_LIST;
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.DEPARTMENT_READ + "', '" + UserRoles.DEPARTMENT_WRITE + "')")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("department", departmentService.get(id));
        return CONTROLLER_VIEW;
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.DEPARTMENT_READ + "')")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("department", departmentService.get(id));
        return CONTROLLER_EDIT;
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.DEPARTMENT_WRITE + "')")
    public String add(@ModelAttribute("department") final DepartmentDTO departmentDTO) {
        return CONTROLLER_ADD;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.DEPARTMENT_WRITE + "')")
    public String add(@ModelAttribute("department") @Valid final DepartmentDTO departmentDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        departmentService.create(departmentDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.DEPARTMENT_WRITE + "')")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("department") @Valid final DepartmentDTO departmentDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        departmentService.update(id, departmentDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRoles.DEPARTMENT_WRITE + "')")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = departmentService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(FlashMessages.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            departmentService.delete(id);
            FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        }
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

}
