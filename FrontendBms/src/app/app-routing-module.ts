import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Home } from './components/home/home';
import { About } from './components/about/about';
import { Dashboard } from './components/dashboard/dashboard';
import { Login } from './components/login/login';
import { Registration } from './components/registration/registration';
import { Errorpage } from './components/errorpage/errorpage';
import { Dynamicroute } from './components/dynamicroute/dynamicroute';
import { Donordashboard } from './components/donordashboard/donordashboard';
import { Hospitaldashboard } from './components/hospitaldashboard/hospitaldashboard';
import { authguardGuard } from './guards/authguard-guard';
import { Unauthorized } from './components/unauthorized/unauthorized';
import { Forgetpassword } from './components/forgetpassword/forgetpassword';
import { Resetpassword } from './components/resetpassword/resetpassword';
import { ResetUsed } from './components/resetpassword/reset-used';
  const routes: Routes = [
    {
      path: '',
      redirectTo: '/home',
      pathMatch: 'full'
    },
    { path: 'user/:id', component: Dynamicroute },
    {
      path: 'home',
      component: Home
    },
    {
      path: 'about',
      component: About
    },
    {
      path: 'forget',
      component: Forgetpassword
    },
    {
      path: 'reset-password',
      component: Resetpassword
    },
    {
      path: 'reset-password/used',
      component: ResetUsed
    },
    {
      path: 'dashboard',
      component: Dashboard,
      canActivate:[authguardGuard],
       data: { role: 'ADMIN' }
    },
    {
      path: 'login',
      component: Login
    },
    {
      path: 'register',
      component: Registration
    },
     {
      path: 'unauthorized',
      component: Unauthorized
    },
    {
      path: 'donordashboard',
      component: Donordashboard,
      canActivate:[authguardGuard],
       data: { role: 'DONOR' }
    },
    {
      path: 'hospitaldashboard',
      component: Hospitaldashboard,
      canActivate:[authguardGuard],
       data: { role: 'HOSPITAL' }
    },
    
    {
      path: '**',
      component: Errorpage
    }
  ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
