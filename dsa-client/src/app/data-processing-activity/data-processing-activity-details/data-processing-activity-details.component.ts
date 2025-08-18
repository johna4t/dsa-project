import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataProcessingActivity } from '../data-processing-activity';
import { DataProcessingActivityService } from '../data-processing-activity.service';
import { HttpErrorResponse } from '@angular/common/http';
import { NavigationService } from '../../access/navigation.service';

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
    private navigation: NavigationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    // Get the 'id' route param
    this.id = +this.route.snapshot.params['id'];

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
    this.navigation.goBackOr(['/']);
  }

  viewDataProcessingActivity(id: number) {
    this.router.navigate(['view-data-processing-activity', id]);
  }

  updateDataProcessingActivity(id: number): void {
    this.navigation.navigateWithReturnTo(['update-data-processing-activity', id]);
  }

  toTitleCase(str: string): string {
    return str?.toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase());
  }
}
