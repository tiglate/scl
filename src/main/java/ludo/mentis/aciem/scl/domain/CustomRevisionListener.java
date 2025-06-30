package ludo.mentis.aciem.scl.domain;

import ludo.mentis.aciem.scl.model.FormsUserDetails;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        var customRevisionEntity = (CustomRevisionEntity) revisionEntity;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof FormsUserDetails principal) {
            customRevisionEntity.setUserId(principal.getId());
        }
    }
}
