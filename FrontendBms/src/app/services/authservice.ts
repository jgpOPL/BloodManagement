import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
@Injectable({
  providedIn: 'root',
})
export class Authservice {
  constructor(private http: HttpClient) {

  }
  private loggedIn = false;
  private loginStatusSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  loginStatus$ = this.loginStatusSubject.asObservable();

  private baseurl = "http://localhost:7999/auth"
  login(user: { username: string, password: string }) {
    return this.http.post(this.baseurl + "/login", user)
  }
  registeruser(data: any) {
    return this.http.post(this.baseurl + "/register", data, { responseType: 'text' })
  }
  setLoggedIn(value: boolean) {
    this.loggedIn = value;
    this.loginStatusSubject.next(value);
  }

  isLoggedIn(): boolean {
    return this.loggedIn || !!localStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    this.loggedIn = false;
      this.loginStatusSubject.next(false);
  }
  sendOTP(username: any) {
    return this.http.post(this.baseurl + "/forgotPassword", username)
  }
  resetPassword(data: any) {
    return this.http.post(this.baseurl + "/VerifyOtp", data, { responseType: 'text' })
  }

  // ── New forgot/reset password via email link ──
  sendPasswordResetLink(email: string) {
    return this.http.post(this.baseurl + "/forgot-password", { email }, { responseType: 'text' });
  }
  verifyResetToken(token: string) {
    return this.http.get(this.baseurl + "/verify-reset-token", { params: { token }, responseType: 'text' });
  }
  resetPasswordWithToken(token: string, newPassword: string, confirmPassword: string) {
    return this.http.post(this.baseurl + "/reset-password", { token, newPassword, confirmPassword, },{responseType:'text'});
  }
}
