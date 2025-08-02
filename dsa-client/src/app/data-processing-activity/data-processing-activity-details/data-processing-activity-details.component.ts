import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataProcessingActivity } from '../data-processing-activity';
import { DataProcessingActivityService } from '../data-processing-activity.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-data-processing-activity-details',
  templateUrl: './data-processing-activity-details.component.html',
  styleUrls: ['./data-processing-activity-details.component.css'],
})
export class DataProcessingActivityDetailsComponent implements OnInit {
  id = 0;
  activity: DataProcessingActivity = new DataProcessingActivity();

  constructor(
    private route: ActivatedRoute,
    private activityService: DataProcessingActivityService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.activityService.getDataProcessingActivityById(this.id).subscribe({
      next: (response) => {
        this.activity = response;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Failed to fetch data processing activity:', error);
      },
    });
  }

  viewDataContentDefinition(id: number): void {
    this.router.navigate(['view-data-content-definition', id]);
  }

  goBack(): void {
    this.router.navigate(['/data-processing-activities']);
  }

  viewDataProcessingActivity(id: number) {
    this.router.navigate(['view-data-processing-activity', id]);
  }

  editActivity(): void {
    this.router.navigate(['update-data-processing-activity', this.id]);
  }

  goBackToProcessor(): void {
    const processorId = this.activity.dataProcessor?.id;
    if (processorId != null) {
      this.router.navigate(['view-data-processor', processorId]);
    } else {
      this.router.navigate(['/data-processors']);
    }
  }

  toTitleCase(str: string): string {
    return str?.toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase());
  }
}
