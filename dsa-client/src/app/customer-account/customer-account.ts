import { Address } from "./address";
import { UserAccount } from "../user-account/user-account";

export class CustomerAccount {
  id = 0;
  name = '';
  departmentName = '';
  url = '';
  branchName = '';
  address: Address = new Address;
  users: UserAccount[] = [];
}
