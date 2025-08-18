import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormArray,
  AbstractControl,
} from '@angular/forms';
import { DataProcessingActivityService } from '../data-processing-activity.service';
import { DataProcessorService } from '../../data-processor/data-processor.service';
import { DataContentDefinitionService } from '../../data-content-definition/data-content-definition.service';
import { DataProcessingActivity } from '../data-processing-activity';
import { DataProcessor } from '../../data-processor/data-processor';
import { DataContentDefinition } from '../../data-content-definition/data-content-definition';
import { DataProcessingActionType } from '../../enums/data-processing-action-type.enum';
import { NavigationService } from '../../access/navigation.service';

@Component({
  selector: 'app-update-data-processing-activity',
  templateUrl: './update-data-processing-activity.component.html',
  styleUrls: ['./update-data-processing-activity.component.css'],
})
export class UpdateDataProcessingActivityComponent implements OnInit {
  form!: FormGroup;
  initialFormValue!: any;
  activityId!: number;
  sourcePage!: 'data-processor' | 'data-content-definition';
  activity!: DataProcessingActivity;

  dataProcessors: DataProcessor[] = [];
  dataContentDefinitions: DataContentDefinition[] = [];

  // Use { value, label } so the UI shows Sentence case but we keep UPPERCASE values for saving.
  actionTypes!: { value: string; label: string }[];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private activityService: DataProcessingActivityService,
    private dataProcessorService: DataProcessorService,
    private dataContentDefinitionService: DataContentDefinitionService,
    private navigation: NavigationService
  ) {}

  ngOnInit(): void {
    this.activityId = +this.route.snapshot.paramMap.get('id')!;
    this.sourcePage = this.route.snapshot.queryParamMap.get('from') as
      | 'data-processor'
      | 'data-content-definition';

    // Build the label list once (enum remains UPPERCASE)
    this.actionTypes = Object.values(DataProcessingActionType).map((value) => ({
      value,
      label: this.formatSentenceCase(value),
    }));

    this.activityService
      .getDataProcessingActivityById(this.activityId)
      .subscribe((activity) => {
        this.activity = activity;
        this.loadDropdownOptions();

        this.form = this.fb.group({
          name: [activity.name, Validators.required],
          description: [activity.description],
          dataProcessorId: [activity.dataProcessor?.id, Validators.required],
          dataContentDefinitionId: [
            activity.dataContentDefinition?.id,
            Validators.required,
          ],
          actionsPerformed: this.fb.array([]),
        });

        // Load existing actions EXACTLY as returned by backend (UPPERCASE),
        // so mat-select binds and shows the correct preselected option.
        activity.actionsPerformed?.forEach((action) => {
          this.actionsPerformed.push(
            this.fb.group({
              actionType: [action.actionType ?? null, Validators.required], // keep backend value
              description: [action.description || ''],
            })
          );
        });

        this.initialFormValue = this.form.getRawValue();
      });
  }

  get actionsPerformed(): FormArray {
    return this.form.get('actionsPerformed') as FormArray;
  }

  addAction(): void {
    this.actionsPerformed.push(
      this.fb.group({
        actionType: [null, Validators.required], // null so no default selection
        description: [''],
      })
    );
  }

  removeAction(index: number): void {
    this.actionsPerformed.removeAt(index);
  }

  // ✅ Track by the control instance to prevent DOM reuse bugs
  trackByControl = (_: number, ctrl: AbstractControl) => ctrl;

  isFormChanged(): boolean {
    return (
      this.form.valid &&
      JSON.stringify(this.form.getRawValue()) !==
        JSON.stringify(this.initialFormValue)
    );
  }

  onSubmit(): void {
    if (!this.isFormChanged()) return;

    const updated: DataProcessingActivity = {
      ...this.activity,
      ...this.form.value,
      dataProcessor: { id: this.form.value.dataProcessorId } as DataProcessor,
      dataContentDefinition: {
        id: this.form.value.dataContentDefinitionId,
      } as DataContentDefinition,
      // Values already UPPERCASE — send through as-is.
      actionsPerformed: this.form.value.actionsPerformed,
    };

    this.activityService
      .putDataProcessingActivity(this.activityId, updated)
      .subscribe(() => {
        this.initialFormValue = this.form.getRawValue();
        this.router.navigate([
          '/view-data-processing-activity',
          this.activityId,
        ]);
      });
  }

  goBack(): void {
    this.navigation.goBackOr(['/']);
  }

  private loadDropdownOptions(): void {
    this.dataProcessorService.getDataProcessorList().subscribe((processors) => {
      this.dataProcessors = processors;
    });

    this.dataContentDefinitionService
      .getDataContentDefinitionList()
      .subscribe((dcds) => {
        this.dataContentDefinitions = dcds;
      });
  }

  // Converts ENUM_STYLE or "POST-PROCESSING" to "Sentence case" / "Post-processing"
  private formatSentenceCase(value: string): string {
    if (!value) return '';
    const base = value.replace(/_/g, ' ').toLowerCase();
    return base.charAt(0).toUpperCase() + base.slice(1);
  }
}
