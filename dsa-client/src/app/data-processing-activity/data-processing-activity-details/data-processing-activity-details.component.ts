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
  from: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private activityService: DataProcessingActivityService,
    private router: Router,
  ) {}

ngOnInit(): void {
  // Get the 'id' route param
  this.id = this.route.snapshot.params['id'];

  // Get the 'from' query param (e.g., 'processor' or 'dcd')
  this.route.queryParamMap.subscribe(params => {
    this.from = params.get('from');
  });

  // Fetch the data processing activity
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

  viewDataProcessor(id: number): void {
    this.router.navigate(['view-data-processor', id]);
  }

goBack(): void {
  if (this.from === 'processor') {
    const processorId = this.activity.dataProcessor?.id;
    if (processorId != null) {
      this.router.navigate(['view-data-processor', processorId]);
    } else {
      this.router.navigate(['/data-processors']);
    }
  } else if (this.from === 'dcd') {
    const dcdId = this.activity.dataContentDefinition?.id;
    if (dcdId != null) {
      this.router.navigate(['view-data-content-definition', dcdId]);
    } else {
      this.router.navigate(['/data-content-definitions']);
    }
  } else {
    this.router.navigate(['/']); // fallback
  }
}


  viewDataProcessingActivity(id: number) {
    this.router.navigate(['view-data-processing-activity', id]);
  }

  editDataProcessingActivity(): void {
    this.router.navigate(['update-data-processing-activity', this.id]);
  }

  toTitleCase(str: string): string {
    return str?.toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase());
  }
}
