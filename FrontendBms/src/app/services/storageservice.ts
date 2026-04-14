import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class Storageservice {
  setItem(key:any,value:any)
  {
    localStorage.setItem(key,value)
  }
  getItem(key:any)
  {
    return localStorage.getItem(key)
  }
  removeItem(key:any)
  {
    localStorage.removeItem(key)
  }
}
