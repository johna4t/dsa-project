import { Component } from '@angular/core';
import { DataSharingParty } from '../data-sharing-party';
import { DataSharingPartyService } from '../data-sharing-party.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { Validators, FormControl } from '@angular/forms';

@Component({
  selector: 'app-update-data-sharing-party',
  templateUrl: './update-data-sharing-party.component.html',
  styleUrls: ['./update-data-sharing-party.component.css']
})
export class UpdateDataSharingPartyComponent {

  dataSharingParty: DataSharingParty = new DataSharingParty();
  id: number = 0;

  readonly urlRegex = /(^|\s)((https?:\/\/)?[\w-]+(\.[\w-]+)+\.?(:\d+)?(\/\S*)?)/gi;

  nameMinLength: number = 3;

  name = new FormControl('', [Validators.minLength(this.nameMinLength), Validators.required]);
  url = new FormControl('', [Validators.pattern(this.urlRegex), Validators.required]);


  constructor(private dataSharingPartyService: DataSharingPartyService,
    private router: Router,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.dataSharingPartyService.getDataSharingPartyById(this.id).subscribe(
      data => { this.dataSharingParty = data; }, error => console.log(error));
  }

  onSubmit() {
    console.log(this.dataSharingParty);

    this.updateDataSharingParty();
  }

  updateDataSharingParty() {
    this.dataSharingPartyService.putDataSharingParty(this.dataSharingParty).subscribe(data => {
      console.log(data);
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
