import { DataContentDefinition } from '../data-content-definition/data-content-definition';

export class DataContentPerspective {
  id?: number;
  dataContentDefinition!: DataContentDefinition;
  metadataScheme: 'GDPR' | undefined;
  metadata: Record<string, unknown> = {
    lawfulBasis: 'NOT_PERSONAL_DATA',
    specialCategory: 'NOT_SPECIAL_CATEGORY_DATA'
  };
}
