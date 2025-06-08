import { DataContentType } from './data-content-type.enum';

export const DataContentTypeLabels: Record<DataContentType, string> = {
  [DataContentType.NOT_SPECIFIED]: "Not specified",
  [DataContentType.STRUCTURED_ELECTRONIC_DATA]: "Electronic file or message containing structured data",
  [DataContentType.DATABASE_RECORD]: "Record stored in a structured database",
  [DataContentType.UNSTRUCTURED_ELECTRONIC_DATA]: "Unstructured or free-text electronic content (e.g. email body, chat)",
  [DataContentType.ELECTRONIC_DOCUMENT]: "Electronic document (e.g. PDF, DOCX)",
  [DataContentType.IMAGE_OR_BIOMETRIC_FILE]: "Image or biometric capture (e.g. facial scan, fingerprint)",
  [DataContentType.AUDIO_VISUAL_RECORDING]: "Audio or video recording",
  [DataContentType.LOG_OR_TELEMETRY_DATA]: "Machine-generated logs or telemetry",
  [DataContentType.PAPER_DOCUMENT]: "Paper or other hardcopy document",
  [DataContentType.OTHER_MEDIUM]: "Other - see Description"
};
