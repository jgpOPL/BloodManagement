import { Component, inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { Bloodservice } from '../../services/bloodservice';
import { Authservice } from '../../services/authservice';
@Component({
  selector: 'app-registration',
  standalone: false,
  templateUrl: './registration.html',
  styleUrl: './registration.css',
})
export class Registration implements OnInit {
  public registerForm: any
  ngOnInit(): void {

  }
  constructor() {
    this.registerForm = new FormGroup({
      username: new FormControl('', Validators.required),
      email: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
      role: new FormControl('', Validators.required),

    })
  }
  private myAuthService = inject(Authservice)
  private myRouter = inject(Router)
  get formControl() {
    return this.registerForm.controls;
  }
  registerUser() {
    this.myAuthService.registeruser(this.registerForm.value).subscribe({
      next: (res: any) => {
        const role = this.registerForm.value.role;

        if (role === 'ADMIN') {
          this.myRouter.navigate(['/dashboard']);
        } else if (role === 'DONOR') {
          this.myRouter.navigate(['/donordashboard']);
        } else if (role === 'HOSPITAL') {
          this.myRouter.navigate(['/hospitaldashboard']);
        }
      }
    })

  }
}
