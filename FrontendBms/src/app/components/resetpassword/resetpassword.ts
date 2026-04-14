import { Component, inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Authservice } from '../../services/authservice';
import { ChangeDetectorRef } from '@angular/core';
function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const pw = group.get('newPassword')?.value;
  const confirm = group.get('confirmPassword')?.value;
  return pw && confirm && pw !== confirm ? { mismatch: true } : null;
}

@Component({
  selector: 'app-resetpassword',
  standalone: false,
  templateUrl: './resetpassword.html',
  styleUrl: './resetpassword.css',
})
export class Resetpassword implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(Authservice);
  private cd=inject(ChangeDetectorRef)

  token = '';
  hasToken = true;

  form!: FormGroup;
  state: 'idle' | 'loading' | 'success' | 'error' | 'verifying' | 'invalid' = 'idle';
  errorMessage = '';
  historyErrorMessage = '';
  showNew = false;
  showConfirm = false;

  ngOnInit() {
    this.form = new FormGroup({
      newPassword: new FormControl('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: new FormControl('', Validators.required),
    }, { validators: passwordsMatch });

    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      this.hasToken = false;
      return;
    }

    // Verify token immediately on page load
    this.state = 'verifying';
    this.authService.verifyResetToken(this.token).subscribe({
      next: () => {
        // Token is valid — show the form
        this.state = 'idle';
        this.cd.detectChanges();
      },
      error: (err) => {
        // Token is invalid/expired/used — show error screen
        let msg = '';
        try {
          const parsed = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
          msg = parsed?.message || '';
        } catch {
          msg = err.error || '';
        }
        this.state = 'invalid';
        this.errorMessage = msg || 'Reset link has already been used. Redirecting to login...';
        this.cd.detectChanges();
        setTimeout(() => this.router.navigate(['/login']), 4000);
      }
    });
  }

  get np() { return this.form.get('newPassword'); }
  get cp() { return this.form.get('confirmPassword'); }

  submit() {
    if (this.form.invalid) { 
      this.form.markAllAsTouched(); 
      return; 
    }
    
    this.state = 'loading';
    this.errorMessage = '';
    this.historyErrorMessage = '';

    const { newPassword, confirmPassword } = this.form.value;
    this.authService.resetPasswordWithToken(this.token, newPassword, confirmPassword).subscribe({
      next: () => {
        localStorage.setItem(`isReset_${this.token}`, 'true');
        this.state = 'success';
        this.cd.detectChanges();
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        // responseType:'text' means err.error is a raw JSON string — parse it
        let msg = '';
        try {
          const parsed = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
          msg = parsed?.message || '';
        } catch {
          msg = err.error || '';
        }

        if (msg.toLowerCase().includes('last 3 passwords') ||
            msg.toLowerCase().includes('cannot be the same') ||
            msg.toLowerCase().includes('reuse')) {
          this.state = 'idle';
          this.historyErrorMessage = msg;
        } else if (msg.toLowerCase().includes('expired') ||
                   msg.toLowerCase().includes('invalid') ||
                   msg.toLowerCase().includes('already been used')) {
          this.state = 'invalid';
          this.errorMessage = 'Reset link has already been used. Redirecting to login...';
          setTimeout(() => this.router.navigate(['/login']), 4000);
        } else {
          this.state = 'error';
          this.errorMessage = msg || 'Something went wrong. Please try again.';
        }
        this.cd.detectChanges();
      }
    });
  }
}
