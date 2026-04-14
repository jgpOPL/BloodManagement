import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
@Injectable({
  providedIn: 'root',
})
export class Bloodservice {
  constructor(private http: HttpClient) {

  }
  private baseurl = "http://localhost:7999/admin/"
  getallusers(page = 0, size = 10) {
    return this.http.get<any>(`${this.baseurl}allUsers?page=${page}&size=${size}`)
  }
  getBloodRequests(page = 0, size = 10) {
    return this.http.get<any>(`${this.baseurl}getBloodRequests?page=${page}&size=${size}`)
  }
  approveBloodReuqest(id: number) {
    return this.http.get(`${this.baseurl}approve-bloodRequest/${id}`, { responseType: 'text' })
  }
  getBloodStock(page = 0, size = 10) {
    return this.http.get<any>(`${this.baseurl}getBloodStock?page=${page}&size=${size}`)
  }
  downloadexcel() {
    console.log("from download excel blood service");

    return this.http.get(this.baseurl + "report/", { responseType: 'blob' });
  }
  deleteUser(id: number) {
    return this.http.delete(`${this.baseurl}removeUser/${id}`, { responseType: 'text' })
  }
  getalldonors(page = 0, size = 10) {
    return this.http.get<any>(`${this.baseurl}getAllDonors?page=${page}&size=${size}`)
  }
  approveDonor(id: any) {
    return this.http.get(`${this.baseurl}donor/approve/${id}`, { responseType: 'text' })
  }
  getUserId(username:string)
  {
     return this.http.get(`${this.baseurl}getUserId/${username}`)
  }
  getDonations(page = 0, size = 10) {
    return this.http.get<any>(`${this.baseurl}allDonations?page=${page}&size=${size}`)
  }
  getDashboardSummary() {
    return this.http.get<any>(`${this.baseurl}summary`)
  }
}
