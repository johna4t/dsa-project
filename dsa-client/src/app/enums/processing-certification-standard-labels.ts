import { ProcessingCertificationStandard } from './processing-certification-standard.enum';

export const ProcessingCertificationStandardLabels: Record<ProcessingCertificationStandard, string> = {

  // Infosec & Cyber standards
  [ProcessingCertificationStandard.ISO_IEC_27001]: 'Information Security Management System (ISMS)',
  [ProcessingCertificationStandard.ISO_IEC_27002]: 'Code of Practice for Security Controls',
  [ProcessingCertificationStandard.CYBER_ESSENTIALS]: 'Cyber Essentials / Cyber Essentials Plus (UK)',
  [ProcessingCertificationStandard.NIST_SP]: 'NIST SP 800-53 / SP 800-171',

  // Privacy Standards
  [ProcessingCertificationStandard.ISO_IEC_27701]: 'Privacy Information Management System (PIMS)',
  [ProcessingCertificationStandard.ISO_IEC_27018]: 'Protection of PII in Public Clouds',
  [ProcessingCertificationStandard.NIST_PRIVACY_FRAMEWORK]: 'NIST Privacy Framework',

  // BCDR standards
  [ProcessingCertificationStandard.ISO_IEC_22301]: 'Business Continuity Management System (BCMS)',
  [ProcessingCertificationStandard.ISO_IEC_27031]: 'ICT Readiness for Business Continuity',

  // Governance & Service Management standards
  [ProcessingCertificationStandard.ISO_IEC_20000_1]: 'IT Service Management',
  [ProcessingCertificationStandard.COBIT]: 'Control Objectives for Information and Related Technologies',
};
