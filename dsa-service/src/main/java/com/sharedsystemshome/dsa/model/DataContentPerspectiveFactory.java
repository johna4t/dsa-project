package com.sharedsystemshome.dsa.model;

import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.model.evaluator.GdprPerspectiveEvaluator;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import org.springframework.stereotype.Component;

@Component
public class DataContentPerspectiveFactory {

    public Object getPerspective(DataContentPerspective perspective) {
        MetadataScheme scheme = perspective.getMetadataScheme();

        return switch (scheme) {
            case GDPR -> new GdprPerspectiveEvaluator(perspective);
            // case UK_GOV_SECURITY -> new SecurityEvaluator(perspective);
            default -> throw new BusinessValidationException("No evaluator defined for scheme: " + scheme);
        };
    }
}
