package com.sharedsystemshome.dsa.model.evaluator;

import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.model.DataContentPerspective;
import com.sharedsystemshome.dsa.util.BusinessValidationException;

import static com.sharedsystemshome.dsa.util.BusinessValidationException.DATA_CONTENT_PERSPECTIVE;

public class GdprPerspectiveEvaluator {

    private final DataContentPerspective perspective;

    public GdprPerspectiveEvaluator(DataContentPerspective perspective) {
        if (!perspective.getMetadataScheme().equals(MetadataScheme.GDPR)) {
            throw new BusinessValidationException(DATA_CONTENT_PERSPECTIVE
                    + " must be of type "
                    + MetadataScheme.GDPR
            );
        }
        this.perspective = perspective;
    }

    public boolean isPersonal() {
        Object lawfulBasis = perspective.get("lawfulBasis");
        return lawfulBasis != null && !"NOT_PERSONAL_DATA".equals(lawfulBasis.toString());
    }

    public boolean isSpecialCategory() {
        Object category = perspective.get("specialCategory");
        return category != null && !"NOT_SPECIAL_CATEGORY_DATA".equals(category.toString());
    }

    public DataContentPerspective getDataContentPerspective() {
        return this.perspective;
    }
}
