import { Role } from '../role/role';
import { CustomerAccount } from '../customer-account/customer-account';

export class UserAccount {
  id: number = 0;
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  contactNumber: string = '';
  password: string = '';
  jobTitle: string = '';
  roles: Role[] = [];
  parentAccount: CustomerAccount = new CustomerAccount();
}

