import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { ExtractDomainPipe } from './extractDomain.pipe';
import { DataSharingPartyListComponent } from './data-sharing-party/data-sharing-party-list/data-sharing-party-list.component';
import { CreateDataSharingPartyComponent } from './data-sharing-party/create-data-sharing-party/create-data-sharing-party.component';
import { UpdateDataSharingPartyComponent } from './data-sharing-party/update-data-sharing-party/update-data-sharing-party.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DataSharingPartyDetailsComponent } from './data-sharing-party/data-sharing-party-details/data-sharing-party-details.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './access/login/login.component';
import { AdminComponent } from './access/admin/admin.component';
import { UserComponent } from './access/user/user.component';
import { HomeComponent } from './access/home/home.component';
import { HeaderComponent } from './access/header/header.component';
import { ForbiddenComponent } from './access/forbidden/forbidden.component';
import { RouterModule } from '@angular/router';
import { AuthInterceptor } from './access/auth/auth.interceptor';
import { ErrorInterceptor } from './errors/error.interceptor';
import { ApiInterceptor } from './api/api.interceptor';
import { AccessService } from './access/access.service';
import { PageNotFoundComponent } from './access/page-not-found/page-not-found.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DataSharingAgreementListComponent } from './data-sharing-agreement/data-sharing-agreement-list/data-sharing-agreement-list.component';
import { UserAccountListComponent } from './user-account/user-account-list/user-account-list.component';
import { MatTableModule } from '@angular/material/table';
import { BnNgIdleService } from 'bn-ng-idle';
import { CustomerAccountListComponent } from './customer-account/customer-account-list/customer-account-list.component';
import { CreateCustomerAccountComponent } from './customer-account/create-customer-account/create-customer-account.component';
import { CustomerAccountDetailsComponent } from './customer-account/customer-account-details/customer-account-details.component';
import { EnterAdminUserDetailsComponent } from './customer-account/create-customer-account/enter-admin-user-details/enter-admin-user-details.component';
import { EnterCustomerDetailsComponent } from './customer-account/create-customer-account/enter-customer-details/enter-customer-details.component';
import { AlertifyService } from './dialog/alertify/alertify.service';
import { ErrorDialogComponent } from './dialog/material/error-dialog/error-dialog.component';
import { ConfirmationDialogComponent } from './dialog/material/confirmation-dialog/confirmation-dialog.component';
import { CreateUserAccountComponent } from './user-account/create-user-account/create-user-account.component';
import { UserAccountDetailsComponent } from './user-account/user-account-details/user-account-details.component';
import { UpdateUserAccountComponent } from './user-account/update-user-account/update-user-account.component';
import { UserProfileDetailsComponent } from './user-profile/user-profile-details/user-profile-details.component';
import { UpdateUserProfileComponent } from './user-profile/update-user-profile/update-user-profile.component';
import { DataContentDefinitionListComponent } from './data-content-definition/data-content-definition-list/data-content-definition-list.component';
import { CreateDataContentDefinitionComponent } from './data-content-definition/create-data-content-definition/create-data-content-definition.component';
import { UpdateDataContentDefinitionComponent } from './data-content-definition/update-data-content-definition/update-data-content-definition.component';
import { DataContentDefinitionDetailsComponent } from './data-content-definition/data-content-definition-details/data-content-definition-details.component';
import { CreateDataProcessorComponent } from './data-processor/create-data-processor/create-data-processor.component';
import { UpdateDataProcessorComponent } from './data-processor/update-data-processor/update-data-processor.component';
import { DataProcessorDetailsComponent } from './data-processor/data-processor-details/data-processor-details.component';
import { DataProcessorListComponent } from './data-processor/data-processor-list/data-processor-list.component';

@NgModule({
  declarations: [
    AppComponent,
    DataSharingPartyListComponent,
    CreateDataSharingPartyComponent,
    UpdateDataSharingPartyComponent,
    ExtractDomainPipe,
    DataSharingPartyDetailsComponent,
    LoginComponent,
    AdminComponent,
    UserComponent,
    HomeComponent,
    HeaderComponent,
    ForbiddenComponent,
    PageNotFoundComponent,
    DataSharingAgreementListComponent,
    UserAccountListComponent,
    CustomerAccountListComponent,
    CreateCustomerAccountComponent,
    CustomerAccountDetailsComponent,
    EnterAdminUserDetailsComponent,
    EnterCustomerDetailsComponent,
    ErrorDialogComponent,
    ConfirmationDialogComponent,
    CreateUserAccountComponent,
    UserAccountDetailsComponent,
    UpdateUserAccountComponent,
    UserProfileDetailsComponent,
    UpdateUserProfileComponent,
    DataContentDefinitionListComponent,
    CreateDataContentDefinitionComponent,
    UpdateDataContentDefinitionComponent,
    DataContentDefinitionDetailsComponent,
    CreateDataProcessorComponent,
    UpdateDataProcessorComponent,
    DataProcessorDetailsComponent,
    DataProcessorListComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDialogModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatInputModule,
    MatOptionModule,
    MatSelectModule,
    MatTableModule,
    MatToolbarModule,
    ReactiveFormsModule,
    RouterModule,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ApiInterceptor, multi: true },
    AccessService,
    AlertifyService,
    BnNgIdleService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
