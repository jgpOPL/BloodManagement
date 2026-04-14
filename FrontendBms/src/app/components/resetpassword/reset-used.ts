import { Component } from '@angular/core';

@Component({
  selector: 'app-reset-used',
  standalone: false,
  template: `
    <div class="rp-page">
      <div class="rp-card">
        <div class="rp-icon-wrap error">✕</div>
        <h2>Link Already Used</h2>
        <p class="rp-subtitle">This password reset link has already been used. Please request a new one if needed.</p>
        <a routerLink="/forget" class="rp-btn" style="display:block;text-align:center;text-decoration:none;">
          Request a New Link
        </a>
        <a routerLink="/login" class="rp-back">← Back to Sign In</a>
      </div>
    </div>
  `,
  styleUrls: ['./resetpassword.css']
})
export class ResetUsed {}
