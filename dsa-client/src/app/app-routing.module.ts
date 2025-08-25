import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './access/home/home.component';
import { AdminComponent } from './access/admin/admin.component';
import { UserComponent } from './access/user/user.component';
import { LoginComponent } from './access/login/login.component';
import { ForbiddenComponent } from './access/forbidden/forbidden.component';
import { PageNotFoundComponent } from './access/page-not-found/page-not-found.component';
import { DataSharingPartyListComponent } from './data-sharing-party/data-sharing-party-list/data-sharing-party-list.component';
import { CustomerAccountListComponent } from './customer-account/customer-account-list/customer-account-list.component';
import { CreateDataSharingPartyComponent } from './data-sharing-party/create-data-sharing-party/create-data-sharing-party.component';
import { UpdateDataSharingPartyComponent } from './data-sharing-party/update-data-sharing-party/update-data-sharing-party.component';
import { DataSharingPartyDetailsComponent } from './data-sharing-party/data-sharing-party-details/data-sharing-party-details.component';
import { DataSharingAgreementListComponent } from './data-sharing-agreement/data-sharing-agreement-list/data-sharing-agreement-list.component';
import { CreateUserAccountComponent } from './user-account/create-user-account/create-user-account.component';
import { UpdateUserAccountComponent } from './user-account/update-user-account/update-user-account.component';
import { UserAccountListComponent } from './user-account/user-account-list/user-account-list.component';
import { UserAccountDetailsComponent } from './user-account/user-account-details/user-account-details.component';
import { UserProfileDetailsComponent } from './user-profile/user-profile-details/user-profile-details.component';
import { UpdateUserProfileComponent } from './user-profile/update-user-profile/update-user-profile.component';
import { CreateCustomerAccountComponent } from './customer-account/create-customer-account/create-customer-account.component';
import { EnterAdminUserDetailsComponent } from './customer-account/create-customer-account/enter-admin-user-details/enter-admin-user-details.component';
import { EnterCustomerDetailsComponent } from './customer-account/create-customer-account/enter-customer-details/enter-customer-details.component';
import { CustomerAccountDetailsComponent } from './customer-account/customer-account-details/customer-account-details.component';
import { DataContentDefinitionListComponent } from './data-content-definition/data-content-definition-list/data-content-definition-list.component';
import { CreateDataContentDefinitionComponent } from './data-content-definition/create-data-content-definition/create-data-content-definition.component';
import { UpdateDataContentDefinitionComponent } from './data-content-definition/update-data-content-definition/update-data-content-definition.component';
import { DataContentDefinitionDetailsComponent } from './data-content-definition/data-content-definition-details/data-content-definition-details.component';
import { CreateDataProcessorComponent } from './data-processor/create-data-processor/create-data-processor.component';
import { UpdateDataProcessorComponent } from './data-processor/update-data-processor/update-data-processor.component';
import { DataProcessorDetailsComponent } from './data-processor/data-processor-details/data-processor-details.component';
import { DataProcessorListComponent } from './data-processor/data-processor-list/data-processor-list.component';
import { CreateDataProcessingActivityComponent } from './data-processing-activity/create-data-processing-activity/create-data-processing-activity.component';
import { UpdateDataProcessingActivityComponent } from './data-processing-activity/update-data-processing-activity/update-data-processing-activity.component';
import { DataProcessingActivityDetailsComponent } from './data-processing-activity/data-processing-activity-details/data-processing-activity-details.component';
import { DataProcessingActivityListComponent } from './data-processing-activity/data-processing-activity-list/data-processing-activity-list.component';
import { authGuard } from './access/auth/auth.guard';

const routes: Routes = [
  //{ path: '', redirectTo: 'home', pathMatch: 'full' },
  {
    path: '',
    title: 'Home',
    component: HomeComponent
  },
  {
    path: 'admin',
    title: 'Admin',
    component: AdminComponent,
    canActivate: [authGuard], data: { roles: ['ACCOUNT_ADMIN', 'SUPER_ADMIN']
    }
  },
  {
    path: 'user',
    title: 'Member',
    component: UserComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER'] }
  },
  {
    path: 'customer-account-details',
    title: 'View Customer Account',
    component: CustomerAccountDetailsComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'customer-accounts',
    title: 'View customers',
    component: CustomerAccountListComponent,
    canActivate: [authGuard],
    data: { roles: ['SUPER_ADMIN'] }
  },
  {
    path: 'data-sharing-parties',
    title: 'View partners',
    component: DataSharingPartyListComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'create-user-account',
    title: 'Add user',
    component: CreateUserAccountComponent,
    canActivate: [authGuard],
    data: { roles: ['ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'create-data-sharing-party',
    title: 'Add partner',
    component: CreateDataSharingPartyComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'update-data-sharing-party/:id',
    title: 'Update partner details',
    component: UpdateDataSharingPartyComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'view-data-sharing-party/:id',
    title: 'View partner details',
    component: DataSharingPartyDetailsComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'data-sharing-agreements',
    title: 'DSA Home',
    component: DataSharingAgreementListComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'user-accounts',
    title: 'View team members',
    component: UserAccountListComponent,
    canActivate: [authGuard],
    data: { roles: ['ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'view-user-account/:id',
    title: 'View user account',
    component: UserAccountDetailsComponent,
    canActivate: [authGuard],
    data: { roles: ['ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'update-user-account/:id',
    title: 'Update user details',
    component: UpdateUserAccountComponent,
    canActivate: [authGuard],
    data: { roles: ['ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'view-user-profile',
    title: 'View user profile',
    component: UserProfileDetailsComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'update-user-profile',
    title: 'Update user profile',
    component: UpdateUserProfileComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'data-content-definitions',
    title: 'View data content definitions',
    component: DataContentDefinitionListComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'update-data-content-definition/:id',
    title: 'Update data content definition',
    component: UpdateDataContentDefinitionComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
{
    path: 'view-data-content-definition/:id',
    title: 'View data content definition',
    component: DataContentDefinitionDetailsComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'create-data-content-definition',
    title: 'Add data content definition',
    component: CreateDataContentDefinitionComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
    {
    path: 'data-processors',
    title: 'View data processors',
    component: DataProcessorListComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'update-data-processor/:id',
    title: 'Update data processor',
    component: UpdateDataProcessorComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
{
    path: 'view-data-processor/:id',
    title: 'View data processor',
    component: DataProcessorDetailsComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'create-data-processor',
    title: 'Add data processor',
    component: CreateDataProcessorComponent,
    canActivate: [authGuard],
    data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
  },
  {
  path: 'data-processing-activities',
  title: 'View data processing activities',
  component: DataProcessingActivityListComponent,
  canActivate: [authGuard],
  data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
},
{
  path: 'create-data-processing-activity',
  title: 'Add data processing activity',
  component: CreateDataProcessingActivityComponent,
  canActivate: [authGuard],
  data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
},
{
  path: 'view-data-processing-activity/:id',
  title: 'View data processing activity',
  component: DataProcessingActivityDetailsComponent,
  canActivate: [authGuard],
  data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
},
{
  path: 'update-data-processing-activity/:id',
  title: 'Update data processing activity',
  component: UpdateDataProcessingActivityComponent,
  canActivate: [authGuard],
  data: { roles: ['MEMBER', 'ACCOUNT_ADMIN', 'SUPER_ADMIN'] }
},

  // Unguarded routes
  { path: 'create-customer-account', title: 'Create new account', component: CreateCustomerAccountComponent },
  { path: 'enter-customer-details', title: 'Enter customer details', component: EnterCustomerDetailsComponent },
  { path: 'enter-admin-user-details', title: 'Enter admin user details', component: EnterAdminUserDetailsComponent },
  { path: 'login', title: 'Login', component: LoginComponent },
  { path: 'forbidden', title: 'Forbidden', component: ForbiddenComponent },
  { path: '**', component: PageNotFoundComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
