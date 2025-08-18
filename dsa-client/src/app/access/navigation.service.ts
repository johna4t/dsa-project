// navigation.service.ts
import { Injectable } from '@angular/core';
import { NavigationEnd, Router, UrlTree } from '@angular/router';
import { filter } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class NavigationService {
  private history: string[] = [];

  constructor(private router: Router) {
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => {
        // Keep a simple stack of visited URLs
        this.history.push(e.urlAfterRedirects);
      });
  }

  /** Push a navigation and carry a returnTo state that points to current URL */
  navigateWithReturnTo(commands: any[], extras: Parameters<Router['navigate']>[1] = {}) {
    const state = { ...(extras?.state || {}), returnTo: this.router.url };
    return this.router.navigate(commands, { ...extras, state });
  }

  /** Try returning via history.state.returnTo (exact previous page), else history stack, else fallback */
  async goBackOr(fallback: any[] = ['/'], extras: Parameters<Router['navigate']>[1] = {}) {
    const state = window.history.state as { returnTo?: string } | undefined;

    // 1) If caller provided an explicit returnTo (most precise): use it
    if (state?.returnTo) {
      return this.router.navigateByUrl(state.returnTo);
    }

    // 2) Use our recorded history (skip current URL)
    this.history.pop(); // current
    const last = this.history.pop();
    if (last) {
      return this.router.navigateByUrl(last);
    }

    // 3) Fallback
    return this.router.navigate(fallback, extras);
  }
}
