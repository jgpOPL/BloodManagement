import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Storageservice } from '../services/storageservice';
import {jwtDecode} from 'jwt-decode';


export const authguardGuard: CanActivateFn = (route, state) => {

  const myRoute = inject(Router)
  const myStorageService = inject(Storageservice)
  const token = myStorageService.getItem('token')
  const role = myStorageService.getItem('role')
  const expectedRole = route.data?.['role'];
  console.log("Role in authguard : ", role);
  console.log("Expected role in authguard : ", expectedRole);
  if (!token) {
    myRoute.navigate(['/login'])
    return false;
  }
  let decodedToken: any;
  try {
    decodedToken = jwtDecode(token);
  } catch (err) {
    console.error('Invalid token:', err);
    myRoute.navigate(['/login']);
    return false;
  }
  const now = Date.now();
  const exp = decodedToken.exp * 1000;
  if (exp < now) {
    console.warn('Token expired');
    myStorageService.removeItem('token');
    myStorageService.removeItem('username');
    myStorageService.removeItem('role');
    myRoute.navigate(['/login']);
    return false;
  }

  const userRoles: string[] = decodedToken.role || [];
  // console.log('User roles:', userRoles);
  // console.log('Expected role:', expectedRole);

   if (expectedRole && !userRoles.includes(expectedRole)) {
    myRoute.navigate(['/unauthorized']);
    return false;
  }
  return true;

};
