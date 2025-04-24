import { Component } from '@angular/core';
import { UserAccount } from '../../user-account/user-account';
import { ActivatedRoute } from '@angular/router';
import { UserProfileService } from '../user-profile.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-profile-details',
  templateUrl: './user-profile-details.component.html',
  styleUrls: ['./user-profile-details.component.css']
})
export class UserProfileDetailsComponent {

  userProfile: UserAccount = new UserAccount();
  rolesDisplay: string = ''; 
  constructor(private route: ActivatedRoute,
    private userProfileService: UserProfileService,
    private router: Router) { }

  ngOnInit(): void {

      this.userProfileService.getUserProfile().subscribe(
      data => {
        this.userProfile = data;
        this.formatRoles(); 
      },
      error => {
        console.error('Error fetching user account:', error);
      }
    )

  }

  private formatRoles(): void {
    if (this.userProfile.roles) {
      this.rolesDisplay = this.userProfile.roles.map(role => role.name).join(', ');
    }
  }

  updateUserProfile() {
    this.router.navigate(['update-user-profile']);
  }

}
