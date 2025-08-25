import { DataSharingParty } from '../data-sharing-party/data-sharing-party';
import { DataContentPerspective } from '../data-content-perspective/data-content-perspective';
import { DataProcessingActivity } from '../data-processing-activity/data-processing-activity';

export class DataContentDefinition {
  id = 0;
  provider!: DataSharingParty;
  name = '';
  description?: string;
  dataContentType = "";
  retentionPeriod?: string; // ISO 8601 duration string for Period, e.g. 'P5Y'
  perspectives: DataContentPerspective[] = [];
  ownerEmail = '';
  ownerName?: string;
  sourceSystem = '';
  associatedDataProcessing: DataProcessingActivity[] =[];
  isReferenced = false;
}
