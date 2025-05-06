
import { DataSharingParty } from '../data-sharing-party/data-sharing-party';
import { DataContentPerspective } from '../data-content-perspective/data-content-perspective';

export class DataContentDefinition {
  id?: number;
  provider!: DataSharingParty;
  name = '';
  description?: string;
  dataContentType = "";
  retentionPeriod?: string;
  perspectives: DataContentPerspective[] = [];
}
