import { Component } from '@angular/core';
import { DataSharingParty } from '../data-sharing-party';
import { ActivatedRoute } from '@angular/router';
import { DataSharingPartyService } from '../data-sharing-party.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-data-sharing-party-details',
  templateUrl: './data-sharing-party-details.component.html',
  styleUrls: ['./data-sharing-party-details.component.css']
})
export class DataSharingPartyDetailsComponent {

  id: number = 0;
  dataSharingParty: DataSharingParty = new DataSharingParty();

  constructor(private route: ActivatedRoute, private dataSharingPartyService: DataSharingPartyService, private router: Router) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.dataSharingPartyService.getDataSharingPartyById(this.id).subscribe(
      data => {
        this.dataSharingParty = data;
      }
    )
  }

  updateDataSharingParty(id: number) {
    this.router.navigate(['update-data-sharing-party', id]);
  }


}
