import { HttpClient } from '@angular/common/http';
import { inject, Injectable, LOCALE_ID } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class Donorservice {
   constructor(private http: HttpClient) {
 
  }
  private baseurl = "http://localhost:7999/donor"
  registerDonor(user: any) {
    return this.http.post<any>(this.baseurl+"/register", user)
  }
  updateDonor(id:number,donorData:any) {
    return this.http.put<any>(`${this.baseurl}/profile/${id}`,donorData, { responseType: 'text' as 'json'})
  }
  donorProfile(id:number)
  {
      return this.http.get<any>(`${this.baseurl}/profile/${id}`)
  }
   getUserId(username:string)
  {
    return this.http.get(`${this.baseurl}/getUserId/${username}`)
  }
  donationHistory(id: number, page = 0, size = 10) {
    return this.http.get<any>(`${this.baseurl}/history/${id}?page=${page}&size=${size}&sort=donationDate,desc`)
  }
  donateBlood(data: any) {
    return this.http.post('http://localhost:7999/donor/donateBlood', data, { responseType: 'text' });
  }

}
