import { Component, OnInit, inject } from '@angular/core';
import { Bloodservice } from '../../services/bloodservice';
import { ChangeDetectorRef } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router'
@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  users: any[] = [];
  bloodRequests: any[] = [];
  bloodStock: any[] = [];
  stockForm!: FormGroup;
  donors: any[] = [];
  donations: any[] = [];
  activeSection: 'dashboard' | 'donations' | 'users' | 'donors' | 'bloodStock' | 'bloodRequests' = 'dashboard';

  // ── Summary counts (from dedicated API — not page-dependent) ──
  summaryTotalUsers = 0;
  summaryTotalRequests = 0;
  summaryPending = 0;
  summaryApproved = 0;
  pageSizeOptions = [4, 10, 20, 50];

  usersPage = 0; usersSize = 10; usersTotal = 0; usersTotalPages = 0;
  reqPage = 0; reqSize = 10; reqTotal = 0; reqTotalPages = 0;
  stockPage = 0; stockSize = 10; stockTotal = 0; stockTotalPages = 0;
  donorPage = 0; donorSize = 10; donorTotal = 0; donorTotalPages = 0;
  donationsPage = 0; donationsSize = 10; donationsTotal = 0; donationsTotalPages = 0;

  private serv = inject(Bloodservice);
  private cd = inject(ChangeDetectorRef);

  constructor() {
    this.stockForm = new FormGroup({
      units: new FormControl('', Validators.required),
      bloodGroup: new FormControl('', Validators.required),
    });
  }

  ngOnInit(): void {
    this.getSummaryData();
    this.loadSummary();
  }

  showSection(section: 'users' | 'bloodStock' | 'bloodRequests' | 'donors' | 'donations') {
    this.activeSection = section;

    if (section === 'users') this.getuserData();
    if (section === 'bloodRequests') this.getbloodrequests();
    if (section === 'bloodStock') this.getbloodStock();
    if (section === 'donors') this.getAllDonors();
    if (section === 'donations') this.getDonations();
  }

  loadSummary() {
    this.serv.getDashboardSummary().subscribe({
      next: (res: any) => {
        this.summaryTotalUsers    = res.totalUsers;
        this.summaryTotalRequests = res.totalBloodRequests;
        this.summaryPending       = res.pendingBloodRequests + res.pendingDonors;
        this.summaryApproved      = res.approvedBloodRequests + res.approvedDonors;
        this.cd.detectChanges();
      }
    });
  }

  getSummaryData() {
    this.getuserData();
    this.getbloodrequests();
    this.getbloodStock();
    this.getAllDonors()
    this.getDonations()
  }

  private extractData(res: any): any[] {
    if (Array.isArray(res)) return res;
    return res?.data ?? res?.content ?? [];
  }

  getuserData() {
    this.serv.getallusers(this.usersPage, this.usersSize).subscribe({
      next: (res: any) => {
        this.users = this.extractData(res);
        this.usersTotal = res?.totalElements ?? 0;
        this.usersTotalPages = res?.totalPages ?? 0;
        this.cd.detectChanges();
      }
    });
  }

  getbloodrequests() {
    this.serv.getBloodRequests(this.reqPage, this.reqSize).subscribe({
      next: (res: any) => {
        this.bloodRequests = this.extractData(res);
        this.reqTotal = res?.totalElements ?? 0;
        this.reqTotalPages = res?.totalPages ?? 0;
        this.cd.detectChanges();
      }
    });
  }

  getbloodStock() {
    this.serv.getBloodStock(this.stockPage, this.stockSize).subscribe({
      next: (res: any) => {
        this.bloodStock = this.extractData(res);
        this.stockTotal = res?.totalElements ?? 0;
        this.stockTotalPages = res?.totalPages ?? 0;
        this.cd.detectChanges();
      }
    });
  }

  approveRequest(id: any) {
    this.serv.approveBloodReuqest(id).subscribe({
      next: (res: any) => {
        alert('Request verified');
        this.getbloodrequests();
        this.loadSummary();
      },
      error: () => { alert('Failed to approve request'); }
    });
  }

  approveDonor(id:any) {
    this.serv.approveDonor(id).subscribe({
      next: (res: any) => {
        alert('Donor verified');
        this.getAllDonors();
        this.loadSummary();
      },
      error: () => { alert('Failed to approve Donor'); }
    });
  }
  getAllDonors() {
    this.serv.getalldonors(this.donorPage, this.donorSize).subscribe({
      next: (res: any) => {
        this.donors = this.extractData(res);
        this.donorTotal = res?.totalElements ?? 0;
        this.donorTotalPages = res?.totalPages ?? 0;
        this.cd.detectChanges();
      },
      error: () => { alert("error in getting all donors"); }
    });
  }

  // ── Pagination controls ──
  onUsersPageChange(page: number) { this.usersPage = page; this.getuserData(); }
  onUsersSizeChange(size: number) { this.usersSize = size; this.usersPage = 0; this.getuserData(); }

  onReqPageChange(page: number) { this.reqPage = page; this.getbloodrequests(); }
  onReqSizeChange(size: number) { this.reqSize = size; this.reqPage = 0; this.getbloodrequests(); }

  onStockPageChange(page: number) { this.stockPage = page; this.getbloodStock(); }
  onStockSizeChange(size: number) { this.stockSize = size; this.stockPage = 0; this.getbloodStock(); }

  onDonorPageChange(page: number) { this.donorPage = page; this.getAllDonors(); }
  onDonorSizeChange(size: number) { this.donorSize = size; this.donorPage = 0; this.getAllDonors(); }

  onDonationsPageChange(page: number) { this.donationsPage = page; this.getDonations(); }
  onDonationsSizeChange(size: number) { this.donationsSize = size; this.donationsPage = 0; this.getDonations(); }
  downloadReport() {
    this.serv.downloadexcel().subscribe({
      next: (res: any) => {
        console.log("Download report res :",res);
        
        const url = window.URL.createObjectURL(res);
        const a = document.createElement('a');
        a.href = url;
        a.download = `BloodReport.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);

        console.log("from download report admin dashboard");
        alert('excel downloaded')
      },
      error: () => {
        alert('Failed to download excel');
      }
    });
  }
  edit(id:any)
  {

  }
  remove(id:any)
  {
    this.serv.deleteUser(id).subscribe({
    
      
      next:(res:any)=>{
          console.log("id",id);
        alert('user deleted sucessfully')

      },
       error: () => {
        console.log("id",id);
        alert('Failed to delete user');
      }
    })
  }
  getDonations() {
    this.serv.getDonations(this.donationsPage, this.donationsSize).subscribe({
      next: (res: any) => {
        this.donations = this.extractData(res);
        this.donationsTotal = res?.totalElements ?? 0;
        this.donationsTotalPages = res?.totalPages ?? 0;
        this.cd.detectChanges();
      },
      error: () => { alert('Error in getting all donations'); }
    });
  }
  get totalUsers() { return this.summaryTotalUsers; }
  get totalBloodRequests() { return this.summaryTotalRequests; }
  get totalDonors() { return this.donorTotal; }
  get pendingCount() { return this.summaryPending; }
  get approvedCount() { return this.summaryApproved; }

  min(a: number, b: number) { return Math.min(a, b); }

  getBloodColor(bloodGroup: string): string {
    const colors: Record<string, string> = {
      'A_POSITIVE':  'linear-gradient(135deg, #e74c3c, #c0392b)',  // Red (matches accent)
      'A_NEGATIVE':  'linear-gradient(135deg, #ff6b6b, #e74c3c)',  // Light red
      'B_POSITIVE':  'linear-gradient(135deg, #3a3a58, #2c2c45)',  // Dark purple (matches primary)
      'B_NEGATIVE':  'linear-gradient(135deg, #5b5b7e, #3a3a58)',  // Medium purple
      'AB_POSITIVE': 'linear-gradient(135deg, #22c55e, #16a34a)',  // Green (matches success)
      'AB_NEGATIVE': 'linear-gradient(135deg, #10b981, #059669)',  // Teal green
      'O_POSITIVE':  'linear-gradient(135deg, #f59e0b, #d97706)',  // Orange (matches warning)
      'O_NEGATIVE':  'linear-gradient(135deg, #64748b, #475569)',  // Slate (matches text-muted)
    };
    return colors[bloodGroup] ?? 'linear-gradient(135deg, #94a3b8, #64748b)';
  }

  getTextColor(bloodGroup: string): string {
    // All gradients now have good contrast with white text
    return '#fff';
  }
}