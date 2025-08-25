// navigation.service.ts
import { Injectable } from '@angular/core';
import {
  ActivatedRoute,
  NavigationEnd,
  NavigationExtras,
  Router,
  UrlTree,
} from '@angular/router';
import { filter } from 'rxjs/operators';

type NavTarget =
  | string                 // absolute URL, e.g. '/data-processors/42'
  | any[]                  // router commands, e.g. ['view-data-processing-activity', 7]
  | { commands: any[]; extras?: NavigationExtras }; // wrapped commands

interface TokenRecord { url: string; ts: number; }

@Injectable({ providedIn: 'root' })
export class NavigationService {
  private history: string[] = [];
  private readonly STORAGE_KEY = 'nav:returnTokens:v1';
  // optional retention for tokens (purge on startup). Tune if desired.
  private readonly TOKEN_TTL_MS = 24 * 60 * 60 * 1000;

  constructor(private router: Router) {
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => {
        this.history.push(e.urlAfterRedirects);
      });

    this.purgeOldTokens();
  }

  // ------------------------------------------------------------------
  // Backward-compatible API (kept so you don’t have to change callers)
  // ------------------------------------------------------------------

  /**
   * Push a navigation and carry a return anchor.
   * Legacy behaviour used history.state.returnTo. We still set that,
   * but we ALSO attach a robust token query param (?rtk=...) so that
   * Back works even after replaceUrl hops and reloads.
   */
  navigateWithReturnTo(commands: any[], extras: Parameters<Router['navigate']>[1] = {}) {
    const currentUrl = this.router.url;
    const token = this.issueToken(currentUrl);

    const state = { ...(extras?.state || {}), returnTo: currentUrl };
    const withToken = this.withTokenInQuery(extras, token);

    return this.router.navigate(commands, { ...withToken, state });
  }

  /**
   * Try returning to:
   * 1) an explicit token in the current URL (?rtk=...),
   * 2) legacy history.state.returnTo,
   * 3) our recorded history stack,
   * 4) a fallback.
   */
  async goBackOr(
    fallback: any[] = ['/'],
    extras: Parameters<Router['navigate']>[1] = {}
  ) {
    // 1) Resolve token from the current URL (robust across replaceUrl/reloads)
    const token = this.getTokenFromCurrentUrl();
    if (token) {
      const url = this.resolveToken(token);
      if (url) {
        return this.router.navigateByUrl(url, { replaceUrl: true });
      }
    }

    // 2) Legacy: use explicit returnTo in history.state if present
    const state = window.history.state as { returnTo?: string } | undefined;
    if (state?.returnTo) {
      return this.router.navigateByUrl(state.returnTo, { replaceUrl: true });
    }

    // 3) Use our recorded history (skip current URL)
    this.history.pop(); // current
    const last = this.history.pop();
    if (last) {
      return this.router.navigateByUrl(last);
    }

    // 4) Fallback
    return this.router.navigate(fallback, extras);
  }

  // ------------------------------------------------------------------
  // New, more explicit API for complex flows (recommended)
  // ------------------------------------------------------------------

  /** Open a route and attach an anchor so the child can return here with one click. */
  goWithReturn(target: NavTarget, extras?: NavigationExtras): void {
    const anchorUrl = this.router.url; // absolute
    const token = this.issueToken(anchorUrl);
    this.navigate(target, this.withTokenInQuery(extras, token));
  }

  /** Forward to another page reusing the incoming token (e.g. Update -> Details). */
  forwardWithSameReturn(
    route: ActivatedRoute,
    target: NavTarget,
    extras?: NavigationExtras,
    opts?: { replaceUrl?: boolean; state?: any }
  ): void {
    const token = this.getTokenFromRoute(route);
    const merged: NavigationExtras = {
      ...(extras || {}),
      ...(opts?.replaceUrl ? { replaceUrl: true } : {}),
      ...(opts?.state ? { state: opts.state } : {}),
    };
    const out = token ? this.withTokenInQuery(merged, token) : merged;
    this.navigate(target, out);
  }

  /** Back handler for pages that carry ?rtk=token. */
  backFromRoute(route: ActivatedRoute, fallback: NavTarget = ['/']): void {
    const token = this.getTokenFromRoute(route);
    if (token) {
      const url = this.resolveToken(token);
      if (url) {
        this.router.navigateByUrl(url, { replaceUrl: true });
        return;
      }
    }
    this.navigate(fallback, { replaceUrl: true });
  }

  /** Generate a token pointing to the current URL (if you need to attach manually). */
  tokenForHere(): string {
    return this.issueToken(this.router.url);
  }

  /** Attach an existing token to an outgoing navigation. */
  attachToken(target: NavTarget, token: string, extras?: NavigationExtras): void {
    this.navigate(target, this.withTokenInQuery(extras, token));
  }

  /** Pull token from a route. */
  getTokenFromRoute(route: ActivatedRoute): string | null {
    return route.snapshot.queryParamMap.get('rtk');
  }

  // ------------------------------------------------------------------
  // Internals
  // ------------------------------------------------------------------

  private navigate(target: NavTarget, extras?: NavigationExtras): void {
    if (typeof target === 'string') {
      this.router.navigateByUrl(target, extras);
      return;
    }
    if (Array.isArray(target)) {
      const x = { relativeTo: this.router.routerState.root, ...(extras || {}) };
      this.router.navigate(target, x);
      return;
    }
    if (target?.commands) {
      const x = {
        relativeTo: this.router.routerState.root,
        ...(target.extras || {}),
        ...(extras || {}),
      };
      this.router.navigate(target.commands, x);
      return;
    }
    // fallback
    this.router.navigateByUrl('/', extras);
  }

  private withTokenInQuery(extras: NavigationExtras | undefined, token: string): NavigationExtras {
    const qp = extras?.queryParams ?? {};
    return {
      ...(extras || {}),
      queryParams: { ...qp, rtk: token },
      queryParamsHandling: extras?.queryParamsHandling ?? 'merge',
    };
  }

  /** Create a token for an absolute URL and store it in sessionStorage. */
  private issueToken(url: string): string {
    const token = this.makeToken();
    const db = this.readDb();
    db[token] = { url, ts: Date.now() };
    this.writeDb(db);
    return token;
  }

  private resolveToken(token: string): string | null {
    const db = this.readDb();
    return db[token]?.url ?? null;
  }

  private readDb(): Record<string, TokenRecord> {
    try {
      return JSON.parse(sessionStorage.getItem(this.STORAGE_KEY) || '{}');
    } catch {
      return {};
    }
  }

  private writeDb(db: Record<string, TokenRecord>): void {
    try {
      sessionStorage.setItem(this.STORAGE_KEY, JSON.stringify(db));
    } catch { /* ignore quota errors */ }
  }

  private purgeOldTokens(): void {
    const db = this.readDb();
    const cutoff = Date.now() - this.TOKEN_TTL_MS;
    let changed = false;
    for (const [k, rec] of Object.entries(db)) {
      if (!rec?.ts || rec.ts < cutoff) {
        delete db[k];
        changed = true;
      }
    }
    if (changed) this.writeDb(db);
  }

  private makeToken(): string {
    // simple, unique-enough for a session
    return Math.random().toString(36).slice(2) + Date.now().toString(36);
  }

  private getTokenFromCurrentUrl(): string | null {
    try {
      const tree: UrlTree = this.router.parseUrl(this.router.url);
      return (tree.queryParams && tree.queryParams['rtk']) || null;
    } catch {
      return null;
    }
  }
}
