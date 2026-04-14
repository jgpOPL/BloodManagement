import { Component, inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Authservice } from '../../services/authservice';

@Component({
  selector: 'app-forgetpassword',
  standalone: false,
  templateUrl: './forgetpassword.html',
  styleUrl: './forgetpassword.css',
})
export class Forgetpassword implements OnInit {
  private authService = inject(Authservice);

  form!: FormGroup;
  state: 'idle' | 'loading' | 'error' = 'idle';
  errorMessage = '';
  toast = { visible: false, message: '' };
  private toastTimer: any;

  ngOnInit() {
    this.form = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
    });
  }

  get email() { return this.form.get('email'); }

  showToast(message: string) {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toast = { visible: true, message };
    this.toastTimer = setTimeout(() => this.toast.visible = false, 5000);
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    const email = this.form.value.email;
    this.state = 'loading';
    this.errorMessage = '';

    this.authService.sendPasswordResetLink(email).subscribe({
      next: () => {
        this.state = 'idle';
        this.form.reset();
        this.showToast('Reset link sent! Check your inbox.');
      },
      error: (err) => {
        this.state = 'error';
        this.errorMessage = err.error?.message || err.error || 'No account found with that email address.';
      }
    });
  }
}
