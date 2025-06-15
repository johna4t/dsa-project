package com.sharedsystemshome.dsa.enums;

import lombok.Getter;

@Getter
public enum ProcessingCertificationStandard {

    NONE("No Accreditations"),

    // Infosec & Cyber standards
    ISO_IEC_27001("Information Security Management System (ISMS)"),
    ISO_IEC_27002("Code of Practice for Security Controls"),
    CYBER_ESSENTIALS("Cyber Essentials / Cyber Essentials Plus (UK)"),
    NIST_SP("NIST SP 800-53 / SP 800-171"),

    // Privacy Standards
    ISO_IEC_27701("Privacy Information Management System (PIMS)"),
    ISO_IEC_27018("Protection of PII in Public Clouds"),
    NIST_Privacy_Framework("NIST Privacy Framework"),

    // BCDR standards
    ISO_IEC_22301("Business Continuity Management System (BCMS)"),
    ISO_IEC_27031("ICT Readiness for Business Continuity"),

    // Governance & Service Management standards
    ISO_IEC_20000_1("IT Service Management"),
    COBIT("Control Objectives for Information and Related Technologies");

    // constructor
    private ProcessingCertificationStandard(String processingAccreditationStandard) {
        this.processingAccreditationStandard = processingAccreditationStandard;
    }

    // internal state
    private final String processingAccreditationStandard;

}

