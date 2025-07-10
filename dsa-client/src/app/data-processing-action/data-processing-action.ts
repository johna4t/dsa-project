import { DataProcessingActivity } from "../data-processing-activity/data-processing-activity";
import { DataProcessingActionType } from "../enums/data-processing-action-type.enum";

export class DataProcessingAction {
  id = 0;
  actionType!: DataProcessingActionType;
  description?: string;
  processingActivity!: DataProcessingActivity;
}
