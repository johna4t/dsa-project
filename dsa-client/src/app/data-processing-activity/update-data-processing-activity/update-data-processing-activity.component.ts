// update-data-processing-activity.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DataProcessingActivityService } from '../data-processing-activity.service';
import { DataProcessorService } from '../../data-processor/data-processor.service';
import { DataContentDefinitionService } from '../../data-content-definition/data-content-definition.service';
import { DataProcessingActivity } from '../data-processing-activity';
import { DataProcessor } from '../../data-processor/data-processor';
import { DataContentDefinition } from '../../data-content-definition/data-content-definition';

@Component({
  selector: 'app-update-data-processing-activity',
  templateUrl: './update-data-processing-activity.component.html',
  styleUrls: ['./update-data-processing-activity.component.css']
})
export class UpdateDataProcessingActivityComponent implements OnInit {
  form!: FormGroup;
  initialFormValue!: any;
  activityId!: number;
  sourcePage!: 'data-processor' | 'data-content-definition';
  activity!: DataProcessingActivity;

  dataProcessors: DataProcessor[] = [];
  dataContentDefinitions: DataContentDefinition[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private activityService: DataProcessingActivityService,
    private dataProcessorService: DataProcessorService,
    private dataContentDefinitionService: DataContentDefinitionService
  ) {}

  ngOnInit(): void {
    this.activityId = +this.route.snapshot.paramMap.get('id')!;
    this.sourcePage = this.route.snapshot.queryParamMap.get('from') as 'data-processor' | 'data-content-definition';

    this.activityService.getDataProcessingActivityById(this.activityId).subscribe((activity) => {
      this.activity = activity;

      // Load dropdown options
      this.loadDropdownOptions();

      // Build the form
      this.form = this.fb.group({
        name: [activity.name, Validators.required],
        description: [activity.description],
        dataProcessorId: [activity.dataProcessor?.id, Validators.required],
        dataContentDefinitionId: [activity.dataContentDefinition?.id, Validators.required]
      });

      this.initialFormValue = this.form.getRawValue();
    });
  }

  private loadDropdownOptions(): void {
    this.dataProcessorService.getDataProcessorList().subscribe((processors) => {
      this.dataProcessors = processors;
    });

    this.dataContentDefinitionService.getDataContentDefinitionList().subscribe((dcds) => {
      this.dataContentDefinitions = dcds;
    });
  }

  isFormChanged(): boolean {
    return (
      this.form.valid &&
      JSON.stringify(this.form.getRawValue()) !== JSON.stringify(this.initialFormValue)
    );
  }

  onSubmit(): void {
    if (!this.isFormChanged()) return;

    const updated: DataProcessingActivity = {
      ...this.activity,
      ...this.form.value,
      dataProcessor: { id: this.form.value.dataProcessorId } as DataProcessor,
      dataContentDefinition: { id: this.form.value.dataContentDefinitionId } as DataContentDefinition
    };

    this.activityService.putDataProcessingActivity(this.activityId, updated).subscribe(() => {
      this.initialFormValue = this.form.getRawValue();
      this.form.markAsPristine();
    });
  }

  goBack(): void {
    const idToReturn = this.sourcePage === 'data-processor'
      ? this.activity.dataProcessor?.id
      : this.activity.dataContentDefinition?.id;

    if (this.sourcePage === 'data-processor') {
      this.router.navigate(['/update-data-processor', idToReturn]);
    } else {
      this.router.navigate(['/update-data-content-definition', idToReturn]);
    }
  }
}
