import { Component } from '@angular/core';
import { DataSharingParty } from '../data-sharing-party';
import { DataSharingPartyService } from '../data-sharing-party.service';
import { Router } from '@angular/router';
import { Validators, FormControl } from '@angular/forms';

@Component({
  selector: 'app-create-data-sharing-party',
  templateUrl: './create-data-sharing-party.component.html',
  styleUrls: ['./create-data-sharing-party.component.css']
})
export class CreateDataSharingPartyComponent {

  dataSharingParty: DataSharingParty = new DataSharingParty();

  readonly urlRegex = /(^|\s)((https?:\/\/)?[\w-]+(\.[\w-]+)+\.?(:\d+)?(\/\S*)?)/gi;

  nameMinLength: number = 3;

  name = new FormControl('', [Validators.minLength(this.nameMinLength), Validators.required]);
  url = new FormControl('', [Validators.pattern(this.urlRegex), Validators.required ]);


  
  constructor(
    private dataSharingPartyService: DataSharingPartyService,
    private router: Router
  ) { }

  ngOnInit(): void {

  }

  onSubmit() {
    this.dataSharingPartyService.postDataSharingParty(this.dataSharingParty).subscribe(data => {

      this.next();
    },
      error => console.log(error));
  }

  next() {
    this.router.navigate(['/data-sharing-parties']);
  }

  formIsValid(): boolean {
    return this.name.invalid || this.url.invalid
  }

}
