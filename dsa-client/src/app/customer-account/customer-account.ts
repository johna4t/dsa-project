import { Address } from "./address";
import { UserAccount } from "../user-account/user-account";

export class CustomerAccount {
  id: number = 0;
  name: string = '';
  departmentName: string = '';
  url: string = '';
  branchName: string = '';
  address: Address = new Address;
  users: UserAccount[] = [];
}
