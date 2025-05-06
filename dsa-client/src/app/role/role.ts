import { Permission } from '../permission/permission';

export class Role {
  id = 0;
  name = '';
  permissions: Permission [] = [];
}
