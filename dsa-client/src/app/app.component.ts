import { Component } from '@angular/core';
import { BnNgIdleService } from 'bn-ng-idle';
import { UserLocalStorageService } from './access/user-local-storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'DSA Manager';

  timeout: number = 600;

  constructor(
    private bnIdle: BnNgIdleService,
    private userLocalStorage: UserLocalStorageService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.bnIdle.startWatching(this.timeout).subscribe((isTimedOut: boolean) => {
      if (isTimedOut) {
        console.log('Session expired');
        this.userLocalStorage.clear();
        this.router.navigate(['/login']);
        this.bnIdle.stopTimer();
      }
    });
  }
}
