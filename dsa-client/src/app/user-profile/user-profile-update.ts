import { UserAccount } from '../user-account/user-account';

export class UserProfileUpdate {
  user: UserAccount = new UserAccount();
  oldPassword: string = '';
}

