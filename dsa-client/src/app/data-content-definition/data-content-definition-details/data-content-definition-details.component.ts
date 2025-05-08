import { Component, OnInit } from '@angular/core';
import { DataContentDefinition } from '../data-content-definition';
import { ActivatedRoute } from '@angular/router';
import { DataContentDefinitionService } from '../data-content-definition.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-data-content-definition-details',
  templateUrl: './data-content-definition-details.component.html',
  styleUrls: ['./data-content-definition-details.component.css']
})
export class DataContentDefinitionDetailsComponent implements OnInit {

  id = 0;
  dcd: DataContentDefinition = new DataContentDefinition();

  constructor(private route: ActivatedRoute,
    private dcdService: DataContentDefinitionService,
    private router: Router) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.dcdService.getDataContentDefinitionById(this.id).subscribe({
        next: (response) => {
          this.dcd = response;
        },
        error: (error: HttpErrorResponse) => {
          console.log(error);
        }
    });
  }

  updateDataContentDefinition(id: number) {
    this.router.navigate(['update-data-content-definition', id]);
  }

  getFormattedRetentionPeriod(): string | undefined {
    const iso = this.dcd?.retentionPeriod;
    if (!iso || typeof iso !== 'string') return undefined;

    const match = iso.match(/^P(\d+)([DWMY])$/);
    if (!match) return iso; // fallback for unexpected formats

    const [_, numberStr, unit] = match;
    const number = parseInt(numberStr, 10);

    const unitMap: Record<'D' | 'W' | 'M' | 'Y', string> = {
      D: 'Day',
      W: 'Week',
      M: 'Month',
      Y: 'Year',
    };

    const label = unitMap[unit as keyof typeof unitMap];
    const pluralLabel = number === 1 ? label : `${label}s`;

    return `${number} ${pluralLabel}`;
  }
}
