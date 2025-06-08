import { Article9Condition } from './article-9-condition.enum';

export const Article9ConditionLabels: Record<Article9Condition, string> = {
  [Article9Condition.NOT_APPLICABLE]: 'N/A',
  [Article9Condition.EXPLICIT_CONSENT]: 'Explicit consent',
  [Article9Condition.EMPLOYMENT]: 'Employment, Social Security, or Social Protection Law',
  [Article9Condition.VITAL_INTERESTS]: 'Vital Interests',
  [Article9Condition.NOT_FOR_PROFIT]: 'Not-for-Profit Bodies',
  [Article9Condition.DATA_MADE_PUBLIC]: 'Data Made Public by the Data Subject',
  [Article9Condition.LEGAL]: 'Legal Claims or Judicial Acts',
  [Article9Condition.REASONS_OF_PUBLIC_INTEREST]: 'Substantial Public Interest (UK law)',
  [Article9Condition.HEALTH_OR_OCCUP_CARE]: 'Healthcare and Occupational Medicine',
  [Article9Condition.PUBLIC_HEALTH]:'Public Health',
  [Article9Condition.ARCHIVING]: 'Archiving, Research, or Statistical Purposes'
};

