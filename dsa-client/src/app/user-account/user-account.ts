import { Role } from '../role/role';
import { CustomerAccount } from '../customer-account/customer-account';

export class UserAccount {
  id = 0;
  firstName = '';
  lastName = '';
  email = '';
  contactNumber = '';
  password = '';
  jobTitle = '';
  roles: Role[] = [];
  parentAccount: CustomerAccount = new CustomerAccount();
}

