import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Bloodservice } from '../../services/bloodservice';
import { ChangeDetectorRef } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Authservice } from '../../services/authservice';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login implements OnInit, OnDestroy {
  myForm: any = []
  private serv = inject(Bloodservice)
  private authService = inject(Authservice)
  private cd = inject(ChangeDetectorRef)
  private router = inject(Router);

  isLocked = false;
  lockUntilDisplay = ''; // e.g. "11:59:59 PM — available tomorrow"
  private lockTimer: any = null;

  toast = { visible: false, type: 'error', title: '', message: '', duration: 4000 };
  private toastTimer: any = null;

  showToast(type: 'error' | 'warning' | 'success', title: string, message: string, duration = 4000) {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toast = { visible: true, type, title, message, duration };
    this.cd.detectChanges();
    this.toastTimer = setTimeout(() => this.dismissToast(), duration);
  }

  dismissToast() {
    this.toast.visible = false;
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.cd.detectChanges();
  }

  get formControl() {
    return this.myForm.controls;
  }

  private startCountdown(lockedUntilMs: number) {
    if (this.lockTimer) clearInterval(this.lockTimer);
    this.isLocked = true;

    // Format the unlock time for display
    const unlockDate = new Date(lockedUntilMs);
    const timeStr = unlockDate.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const isNextDay = unlockDate.getDate() !== new Date().getDate();
    this.lockUntilDisplay = isNextDay
      ? `${timeStr} (available tomorrow)`
      : timeStr;

    // Auto-unlock when time passes
    const checkUnlock = setInterval(() => {
      if (Date.now() >= lockedUntilMs) {
        clearInterval(checkUnlock);
        this.isLocked = false;
        this.lockUntilDisplay = '';
        this.showToast('success', 'Account Unlocked', 'You can now log in again.');
        this.cd.detectChanges();
      }
    }, 10000); // check every 10s — no need for per-second countdown
    this.lockTimer = checkUnlock;

    this.cd.detectChanges();
  }

  constructor() {
    this.myForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    })
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    if (this.lockTimer) clearInterval(this.lockTimer);
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  openForgotPassword(event: Event) {
    event.preventDefault();
    this.router.navigate(['/forget']);
  }

  loginUser() {
    if (this.myForm.invalid || this.isLocked) return;

    const user = {
      username: this.myForm.value.username,
      password: this.myForm.value.password,
    };

    this.authService.login(user).subscribe({
      next: (res: any) => {
        if (res?.locked) {
          // Account is locked — start countdown from backend timestamp
          this.startCountdown(res.lockedUntilEpochMs);
          this.cd.detectChanges();
          return;
        }

        if (res?.token) {
          // Successful login
          localStorage.setItem('token', res.token);
          const payload = JSON.parse(atob(res.token.split('.')[1]));
          const role = payload.role[0];
          localStorage.setItem('role', role);
          localStorage.setItem('username', res.username);
          this.authService.setLoggedIn(true);

          const successMessage = document.getElementById('successMessage');
          if (successMessage) successMessage.classList.add('show');

          setTimeout(() => {
            if (role === 'ADMIN') this.router.navigate(['/dashboard']);
            else if (role === 'DONOR') this.router.navigate(['/donordashboard']);
            else if (role === 'HOSPITAL') this.router.navigate(['/hospitaldashboard']);
            else this.router.navigate(['/login']);
          }, 1000);
        } else {
          // Wrong credentials — show attempt warning
          this.handleFailedResponse(res?.failedAttempts ?? 1);
        }
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.showToast('error', 'Invalid Credentials', 'The username or password you entered is incorrect.');
        this.cd.detectChanges();
      }
    });
  }

  private handleFailedResponse(attempts: number) {
    if (attempts === 2) {
      // 2nd wrong attempt — warn that next will lock until end of day
      this.showToast('warning', 'Warning: Last Attempt',
        'One more incorrect attempt will lock your account until 11:59:59 PM. Your account will be available again the next day.', 6000);
    } else {
      this.showToast('error', 'Invalid Credentials', 'The username or password you entered is incorrect.');
    }
  }
}
