package com.sharedsystemshome.dsa.model.evaluator;

import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.model.DataContentPerspective;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import org.springframework.stereotype.Component;

@Component
public class PerspectiveEvaluatorFactory {

    public Object getEvaluator(DataContentPerspective perspective) {
       MetadataScheme scheme = perspective.getMetadataScheme();

        return switch (scheme) {
            case GDPR -> new GdprPerspectiveEvaluator(perspective);
            // case UK_GOV_SECURITY -> new SecurityEvaluator(perspective);
            default -> throw new BusinessValidationException("No evaluator defined for scheme: " + scheme);
        };
    }
}
