import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
@Injectable({
  providedIn: 'root',
})
export class Hospitalservice {
    constructor(private http: HttpClient) {
 
  }
  private baseurl = "http://localhost:7999/hospital"
  registerHospital(user: any) {
    return this.http.post<any>(this.baseurl+"/register", user)
  }
  updateHospital(id:number,hospital:any) {
    return this.http.put<any>(`${this.baseurl}/profile/${id}`,hospital, { responseType: 'text' as 'json'})
  }
  requestBlood(data:any)
  {
    return this.http.post<any>(`${this.baseurl}/request`,data,{responseType: 'text' as 'json'})
  }
  HospitalProfile(id:number)
  {
    return this.http.get<any>(`${this.baseurl}/profile/${id}`)
  }
  getUserId(username:string)
  {

  }
}
