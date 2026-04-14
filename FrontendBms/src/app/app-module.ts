import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Login } from './components/login/login';
import { Registration } from './components/registration/registration';
import { Home } from './components/home/home';
import { About } from './components/about/about';
import { Dashboard } from './components/dashboard/dashboard';
import { Errorpage } from './components/errorpage/errorpage';
import { Dynamicroute } from './components/dynamicroute/dynamicroute';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Bloodservice } from './services/bloodservice';
import { Authinterceptor } from './services/authinterceptor';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { Donordashboard } from './components/donordashboard/donordashboard';
import { Hospitaldashboard } from './components/hospitaldashboard/hospitaldashboard';
import { Unauthorized } from './components/unauthorized/unauthorized';
import { Tokentimer } from './tokentimer/tokentimer';
import { Forgetpassword } from './components/forgetpassword/forgetpassword';
import { Resetpassword } from './components/resetpassword/resetpassword';
import { ResetUsed } from './components/resetpassword/reset-used';
@NgModule({
  declarations: [
    App,
    Login,
    Registration,
    Home,
    About,
    Dashboard,
    Errorpage,
    Dynamicroute,
    Donordashboard,
    Hospitaldashboard,
    Unauthorized,
    Tokentimer,
    Forgetpassword,
    Resetpassword,
    ResetUsed,
  ],
  imports: [BrowserModule, AppRoutingModule, ReactiveFormsModule, HttpClientModule, FormsModule],
  // providers: [provideBrowserGlobalErrorListeners(),Bloodservice,Authinterceptor],
  providers: [
    Bloodservice,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: Authinterceptor,
      multi: true,
    },
  ],
  bootstrap: [App],
})
export class AppModule {}
