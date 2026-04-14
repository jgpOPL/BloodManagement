import { Component, OnInit, OnDestroy } from '@angular/core';
import { Storageservice } from '../services/storageservice';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { interval, Subscription } from 'rxjs';
import { Authservice } from '../services/authservice';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-tokentimer',
  standalone: false,
  templateUrl: './tokentimer.html',
  styleUrl: './tokentimer.css',
})
export class Tokentimer implements OnInit, OnDestroy {
  timeLeft: number = 0;
  minutes: number = 0;
  seconds: number = 0;
  timerSub: Subscription | undefined;


  constructor(
    private storageService: Storageservice,
    private router: Router,
    public authService: Authservice,
    public cd: ChangeDetectorRef
  ) { }

  loginSub!: Subscription;
  ngOnInit() {
    this.loginSub = this.authService.loginStatus$.subscribe((isLoggedIn) => {
      if (isLoggedIn) {
        if (this.timerSub) this.timerSub.unsubscribe();
        this.startTimer();
      }
    });
    
  }

  ngOnDestroy() {
    if (this.timerSub) this.timerSub.unsubscribe();
    if (this.loginSub) this.loginSub.unsubscribe();
  }

  private getTokenExpiration(token: string): number | null {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp;
    } catch {
      return null;
    }
  }

  private isTokenExpired(token: string): boolean {
    const exp = this.getTokenExpiration(token);
    if (!exp) return true; // treat invalid token as expired
    const now = Math.floor(Date.now() / 1000);
    return now >= exp;
  }

  startTimer() {
    const token = this.storageService.getItem('token');
    if (!token || this.isTokenExpired(token)) {
      this.clearTokenAndRedirect();
      return;
    }

    const exp = (this.getTokenExpiration(token) as number) * 1000;

    this.timerSub = interval(1000).subscribe(() => {
      const now = Date.now();
      this.timeLeft = exp - now;

      if (this.timeLeft <= 0) {
        this.timeLeft = 0;
        this.minutes = 0;
        this.seconds = 0;
        this.cd.detectChanges();
        this.clearTokenAndRedirect();
      } else {
        this.minutes = Math.floor(this.timeLeft / 1000 / 60);
        this.seconds = Math.floor((this.timeLeft / 1000) % 60);
        this.cd.detectChanges();
      }
    });
  }

  private clearTokenAndRedirect() {
    this.timeLeft = 0;
    this.minutes = 0;
    this.seconds = 0;
    this.storageService.removeItem('token');
    this.storageService.removeItem('username');
    this.storageService.removeItem('role');
    this.router.navigate(['/login']);
    if (this.timerSub) this.timerSub.unsubscribe();
  }
}