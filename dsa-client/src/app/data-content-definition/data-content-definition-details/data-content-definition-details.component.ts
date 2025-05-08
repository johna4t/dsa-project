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
}
