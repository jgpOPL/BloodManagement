import { Component, inject, OnInit } from '@angular/core';
import { Hospitalservice } from '../../services/hospitalservice';
@Component({
  selector: 'app-hospitaldashboard',
  standalone: false,
  templateUrl: './hospitaldashboard.html',
  styleUrl: './hospitaldashboard.css',
})
export class Hospitaldashboard implements OnInit{
  hospitalprofile:any
  hospitalId:any
  userId:any
  username: string = localStorage.getItem('username') || ''
  private hospitalserv=inject(Hospitalservice)
  ngOnInit(): void {
    
  }
  hospitalProfile(hospitalId:any)
  {
    this.hospitalserv.HospitalProfile(hospitalId).subscribe({
      next:(res:any)=>{
        console.log("Hospital profile",res);
        
      }
    })
  }
    // getUserIdAdmin(username: string) {

    //   this.hospitalserv.getUserId(username).subscribe({
    //     next: (res: any) => {
    //       console.log("Admin Get user id", res);
    //       this.userId = res
    //       this.getDonorId()
    //       // this.getDonorData()
    //       // this.donationHistory(this.userId)
    //     },
    //     error: () => {
    //       // alert('Error in getting userID')
    //     }
    //   })
    // }

}
