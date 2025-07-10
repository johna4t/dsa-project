import { DataContentDefinition } from "../data-content-definition/data-content-definition";
import { DataProcessingAction } from "../data-processing-action/data-processing-action";
import { DataProcessor } from "../data-processor/data-processor";

export class DataProcessingActivity {
  id = 0;
  name = '';
  description?: string;
  dataProcessor!: DataProcessor;
  dataContentDefinition!: DataContentDefinition;
  actionsPerformed: DataProcessingAction[] = [];
}
