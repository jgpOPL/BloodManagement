import { Component, signal } from '@angular/core';
import { Login } from './components/login/login';
import { Authservice } from './services/authservice';
import { Router } from '@angular/router';
@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('RoutingDemo');
  constructor(public authService: Authservice, private router: Router) {}
  logout() {
  this.authService.logout();
  this.router.navigate(['/login']);
}
getDashboardRoute(): string {
    const role = localStorage.getItem('role'); 
    switch (role) {
      case 'ADMIN':
        return '/dashboard';          
      case 'DONOR':
        return '/donordashboard';     
      case 'HOSPITAL':
        return '/hospitaldashboard'; 
      default:
        return '/';                 
    }
  }
}
