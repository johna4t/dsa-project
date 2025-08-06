import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DataProcessingActivityService } from '../data-processing-activity.service';
import { DataProcessingActivity } from '../data-processing-activity';

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

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private activityService: DataProcessingActivityService
  ) {}

  ngOnInit(): void {
    this.activityId = +this.route.snapshot.paramMap.get('id')!;
    this.sourcePage = this.route.snapshot.queryParamMap.get('from') as 'data-processor' | 'data-content-definition';

    this.activityService.getDataProcessingActivityById(this.activityId).subscribe((activity) => {
      this.activity = activity;

      this.form = this.fb.group({
        name: [activity.name, Validators.required],
        description: [activity.description]
      });

      this.initialFormValue = this.form.getRawValue();
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

    const updated = { ...this.activity, ...this.form.value };

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
