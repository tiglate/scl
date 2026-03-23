package ludo.mentis.aciem.scl.util;

import jakarta.servlet.http.HttpServletRequest;
import ludo.mentis.aciem.scl.model.PaginationModel;
import ludo.mentis.aciem.scl.model.PaginationStep;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Map;


@Component
public class WebUtils {

    public static final String EMAIL_PATTERN = "([a-zA-Z0-9][\\-\\.\\+_]?)*[a-zA-Z0-9]+@([a-zA-Z0-9][\\-\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]+";
    public static final String MSG_SUCCESS = "MSG_SUCCESS";
    public static final String MSG_INFO = "MSG_INFO";
    public static final String MSG_ERROR = "MSG_ERROR";
    private static TemplateEngine templateEngine;
    private static String baseHost;

    public WebUtils(final TemplateEngine templateEngine, @Value("${app.baseHost}") final String baseHost) {
        WebUtils.templateEngine = templateEngine;
        WebUtils.baseHost = baseHost;
    }

    public static HttpServletRequest getRequest() {
        final var requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new IllegalStateException("No request attributes found");
        }
        return ((ServletRequestAttributes)requestAttributes).getRequest();
    }


    public static String renderTemplate(final String templateName,
            final Map<String, Object> templateModel) {
        final Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        thymeleafContext.setVariable("baseHost", baseHost);
        return templateEngine.process(templateName, thymeleafContext);
    }

    private static String getStepUrl(final Page<?> page, final int targetPage) {
        String stepUrl = "?page=" + targetPage + "&size=" + page.getSize();
        if (getRequest().getParameter("sort") != null) {
            stepUrl += "&sort=" + getRequest().getParameter("sort");
        }
        if (getRequest().getParameter("filter") != null) {
            stepUrl += "&filter=" + getRequest().getParameter("filter");
        }
        return stepUrl;
    }

    public static PaginationModel getPaginationModel(final Page<?> page) {
        if (page.isEmpty()) {
            return null;
        }

        final ArrayList<PaginationStep> steps = new ArrayList<>();
        final PaginationStep previous = new PaginationStep();
        previous.setDisabled(!page.hasPrevious());
        previous.setLabel("Previous");
        previous.setUrl(getStepUrl(page, page.previousOrFirstPageable().getPageNumber()));
        steps.add(previous);
        // find a range of up to 5 pages around the current active page
        final int startAt = Math.max(0, Math.min(page.getNumber() - 2, page.getTotalPages() - 5));
        final int endAt = Math.min(startAt + 5, page.getTotalPages());
        for (int i = startAt; i < endAt; i++) {
            final PaginationStep step = new PaginationStep();
            step.setActive(i == page.getNumber());
            step.setLabel("" + (i + 1));
            step.setUrl(getStepUrl(page, i));
            steps.add(step);
        }
        final PaginationStep next = new PaginationStep();
        next.setDisabled(!page.hasNext());
        next.setLabel("Next");
        next.setUrl(getStepUrl(page, page.nextOrLastPageable().getPageNumber()));
        steps.add(next);

        final long rangeStart = (long) page.getNumber() * page.getSize() + 1L;
        final long rangeEnd = Math.min(rangeStart + page.getSize() - 1, page.getTotalElements());
        final String range = rangeStart == rangeEnd ? "" + rangeStart : rangeStart + " - " + rangeEnd;
        final PaginationModel paginationModel = new PaginationModel();
        paginationModel.setSteps(steps);
        paginationModel.setElements(range + " of " + page.getTotalElements());
        return paginationModel;
    }

}
