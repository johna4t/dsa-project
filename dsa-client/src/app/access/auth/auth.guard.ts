import {
  CanActivateFn,
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from '@angular/router';

import { inject } from '@angular/core';
import { UserLocalStorageService } from '../user-local-storage.service';
import { AccessService } from '../access.service';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {

  const router: Router = inject(Router);
  const userLocalStorage: UserLocalStorageService = inject(UserLocalStorageService);
  const accessService: AccessService = inject(AccessService);

  if (null !== userLocalStorage.getAccessToken()) {
    const roles = route.data["roles"] as Array<string>;

    if (roles) {
      const match = accessService.roleMatch(roles);

      if (match) {
        return true;
      } else {
        router.navigate(['/forbidden']);
        return false;
      }
    }
  }

  router.navigate(['/login']);
    return false;
};
