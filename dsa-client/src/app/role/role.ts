import { Permission } from '../permission/permission';

export class Role {
  id: number = 0;
  name: string = '';
  permissions: Permission [] = [];
}
