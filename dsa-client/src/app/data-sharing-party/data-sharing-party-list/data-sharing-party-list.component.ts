import { Component, OnInit } from '@angular/core';
import { DataSharingParty } from '../data-sharing-party';
import { DataSharingPartyService } from '../data-sharing-party.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AccessService } from '../../access/access.service';

@Component({
  selector: 'app-data-sharing-party-list',
  templateUrl: './data-sharing-party-list.component.html',
  styleUrls: ['./data-sharing-party-list.component.css']
})
export class DataSharingPartyListComponent implements OnInit{

  displayedColumns: string[] = ['name', 'description', 'url', 'btnView', 'btnEdit', 'btnDelete'];

  dataSharingParties: DataSharingParty[] = [];

  constructor(
    private dataSharingPartyService: DataSharingPartyService,
    private router: Router,
    private accessService: AccessService) { };

  ngOnInit(): void {
    this.getDataSharingParties();
  };

  private getDataSharingParties() {
    this.dataSharingPartyService.getDataSharingPartyList().subscribe(
      (response) => {
      this.dataSharingParties = response;
      },
      (error: HttpErrorResponse) => {
        console.log(error);
      }
    )
  }

  updateDataSharingParty(id: number) {
    this.router.navigate(['update-data-sharing-party', id]);
  }

  deleteDataSharingParty(id: number) {
    this.dataSharingPartyService.deleteDataSharingParty(id).subscribe(data => {
      console.log(data);
      this.getDataSharingParties();
    })
  }

  viewDataSharingParty(id: number) {
    this.router.navigate(['view-data-sharing-party', id]);
  }

  public getAccessService(): AccessService {
    return this.accessService;
  }




}
