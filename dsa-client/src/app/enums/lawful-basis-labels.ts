import { LawfulBasis } from './lawful-basis.enum';

export const LawfulBasisLabels: Record<LawfulBasis, string> = {
  [LawfulBasis.CONSENT]: 'Consent',
  [LawfulBasis.CONTRACT]: 'Contract',
  [LawfulBasis.LEGAL_OBLIGATION]: 'Legal Obligation',
  [LawfulBasis.VITAL_INTERESTS]: 'Vital Interests',
  [LawfulBasis.PUBLIC_TASK]: 'Public Task',
  [LawfulBasis.LEGITIMATE_INTERESTS]: 'Legitimate Interests',
  [LawfulBasis.NOT_PERSONAL_DATA]: 'Not Personal Data'
};
