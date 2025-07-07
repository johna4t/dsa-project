import { DataSharingParty } from '../data-sharing-party/data-sharing-party';
import { ProcessingCertificationStandard } from '../enums/processing-certification-standard.enum';
export class DataProcessor {
  id = 0;
  controller!: DataSharingParty;
  name = '';
  description?: string;
  certifications: ProcessingCertificationStandard[] = [];
  email?: string;
  website = '';
  isSelfAsProcessor = false;
  isReferenced = false;

}
