import { SpecialCategoryData } from './special-category-data.enum';

export const SpecialCategoryDataLabels: Record<SpecialCategoryData, string> = {
  [SpecialCategoryData.RACIAL]: 'Personal data revealing racial or ethnic origin',
  [SpecialCategoryData.POLITICAL]: 'Personal data revealing political opinions',
  [SpecialCategoryData.RELIGIOUS]: 'Personal data revealing religious or philosophical beliefs',
  [SpecialCategoryData.TRADE_UNION]: 'Personal data revealing trade union membership',
  [SpecialCategoryData.GENETIC]: 'Genetic Data',
  [SpecialCategoryData.BIOMETRIC_ID]: 'Biometric data (where used for identification purposes)',
  [SpecialCategoryData.HEALTH]: 'Data concerning health',
  [SpecialCategoryData.SEX_LIFE]: 'Data concerning a person’s sex life',
  [SpecialCategoryData.SEXUAL_ORIENT]: 'Data concerning a person’s sexual orientation',
  [SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA]: 'Not Special Category Data'
};
