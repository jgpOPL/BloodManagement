import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';
import { Authservice } from '../../services/authservice';
import { Donorservice } from '../../services/donorservice';
import { Bloodservice } from '../../services/bloodservice';

@Component({
  selector: 'app-donordashboard',
  standalone: false,
  templateUrl: './donordashboard.html',
  styleUrl: './donordashboard.css',
})
export class Donordashboard implements OnInit, OnDestroy {
  donor: any = null;
  donorHistory: any[] = [];
  donorForm: any;
  donationForm: any;
  showAddDonorForm = false;
  userId: any;
  donorId: any;
  username: string = localStorage.getItem('username') || '';
  showRegisterForm = false;
  showDonateModal = false;
  activeSection = 'profile';
  showUpdateModal = false;
  updateForm: FormGroup;
  isLoading = true;

  // ── History pagination ──
  historyPage = 0;
  historySize = 5;
  historyTotal = 0;
  historyTotalPages = 0;
  historyTotalUnits = 0;
  lastDonationDate: any = null;
  pageSizeOptions = [5, 10, 20];

  // ── Toast ──
  toast = { visible: false, type: 'success', title: '', message: '' };
  private toastTimer: any;

  public authService = inject(Authservice);
  public mydonorserv = inject(Donorservice);
  public myBloodServ = inject(Bloodservice);
  private cd = inject(ChangeDetectorRef);

  constructor(private fb: FormBuilder) {
    this.donorForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      weight: ['', [Validators.required, Validators.min(30), Validators.max(200)]],
      height: ['', [Validators.required, Validators.min(1), Validators.max(2.5)]],
      bloodGroup: ['', Validators.required],
      age: ['', [Validators.required, Validators.min(18), Validators.max(65)]],
      gender: ['', Validators.required],
      city: ['', [Validators.required, Validators.minLength(2)]],
      userId: [{ value: '', disabled: true }, Validators.required]
    });

    this.donationForm = this.fb.group({
      quantity: ['', [Validators.required, Validators.min(0.5), Validators.max(5)]],
      remarks: ['', Validators.required]
    });

    this.updateForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      weight: ['', [Validators.required, Validators.min(30), Validators.max(200)]],
      height: ['', [Validators.required, Validators.min(1), Validators.max(2.5)]],
      age: ['', [Validators.required, Validators.min(18), Validators.max(65)]],
      gender: ['', Validators.required],
      city: ['', [Validators.required, Validators.minLength(2)]],
      bloodGroup: [{ value: '', disabled: true }],
      userId: [{ value: '', disabled: true }]
    });
  }

  showToast(type: 'success' | 'error' | 'warning', title: string, message: string) {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toast = { visible: true, type, title, message };
    this.cd.detectChanges();
    this.toastTimer = setTimeout(() => { this.toast.visible = false; this.cd.detectChanges(); }, 4000);
  }

  dismissToast() { this.toast.visible = false; }

  min(a: number, b: number) { return Math.min(a, b); }

  ngOnInit(): void {
    this.getUserIdAdmin(this.username);
  }

  ngOnDestroy(): void {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  showSection(section: string) {
    this.activeSection = section;
    if (section === 'history') {
      this.historyPage = 0;
      this.loadHistory();
      this.loadTotalUnits();
    }
  }

  getUserIdAdmin(username: string) {
    this.isLoading = true;
    this.myBloodServ.getUserId(username).subscribe({
      next: (res: any) => {
        this.userId = res;
        this.getDonorId();
      },
      error: () => { this.isLoading = false; }
    });
  }

  getDonorId() {
    this.isLoading = true;
    this.mydonorserv.getUserId(this.username).subscribe({
      next: (res: any) => {
        this.donorId = res;
        this.getDonorData();
      },
      error: () => {
        this.isLoading = false;
        this.showRegisterForm = true;
        this.donorForm.patchValue({ userId: this.userId });
        this.cd.detectChanges();
      }
    });
  }

  getDonorData() {
    this.mydonorserv.donorProfile(this.donorId).subscribe({
      next: (res: any) => {
        this.donor = res;
        this.isLoading = false;
        this.showRegisterForm = false;
        this.cd.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.showRegisterForm = true;
        this.donorForm.patchValue({ userId: this.userId });
        this.cd.detectChanges();
      }
    });
  }

  private extractList(res: any): any[] {
    return Array.isArray(res) ? res : (res?.data ?? res?.content ?? []);
  }

  loadHistory() {
    this.mydonorserv.donationHistory(this.donorId, this.historyPage, this.historySize).subscribe({
      next: (res: any) => {
        this.donorHistory = this.extractList(res);
        this.historyTotal = res?.totalElements ?? this.donorHistory.length;
        this.historyTotalPages = res?.totalPages ?? 1;
        this.cd.detectChanges();
      },
      error: () => {}
    });
  }

  loadTotalUnits() {
    // Step 1: get totalElements from page 0
    this.mydonorserv.donationHistory(this.donorId, 0, 1).subscribe({
      next: (res: any) => {
        const total = res?.totalElements ?? 0;
        if (total === 0) { this.historyTotalUnits = 0; this.cd.detectChanges(); return; }
        // Step 2: fetch all records using totalElements as size
        this.mydonorserv.donationHistory(this.donorId, 0, total).subscribe({
          next: (all: any) => {
            const records: any[] = this.extractList(all);
            this.historyTotalUnits = records.reduce((sum, d) => sum + (Number(d.quantity) || 0), 0);
            // also derive last donation date from the latest record
            const sorted = [...records].sort((a, b) =>
              new Date(b.donationDate).getTime() - new Date(a.donationDate).getTime()
            );
            this.lastDonationDate = sorted[0]?.donationDate ?? null;
            this.cd.detectChanges();
          },
          error: () => {}
        });
      },
      error: () => {}
    });
  }

  onHistoryPageChange(page: number) { this.historyPage = page; this.loadHistory(); }
  onHistorySizeChange(size: number) { this.historySize = size; this.historyPage = 0; this.loadHistory(); }

  registerDonor() {
    if (this.donorForm.invalid) { this.donorForm.markAllAsTouched(); return; }
    const payload = { ...this.donorForm.getRawValue() };
    this.mydonorserv.registerDonor(payload).subscribe({
      next: (res: any) => {
        this.donor = res;
        this.showRegisterForm = false;
        this.isLoading = false;
        this.getDonorId();
        this.showToast('success', 'Registered!', 'Donor registered successfully.');
        this.cd.detectChanges();
      },
      error: () => { this.showRegisterForm = false; }
    });
  }

  openDonateModal() {
    this.showDonateModal = true;
    this.donationForm.reset();
    this.cd.detectChanges();
  }

  submitDonation() {
    if (this.donationForm.invalid) { this.donationForm.markAllAsTouched(); return; }

    const donationData = {
      ...this.donationForm.value,
      donorId: this.donorId
    };

    this.mydonorserv.donateBlood(donationData).subscribe({
      next: (res: any) => {
        this.showDonateModal = false;
        this.donationForm.reset();
        this.historyPage = 0;
        this.loadHistory();
        this.loadTotalUnits();
        this.getDonorData(); // refresh lastDonationDate
        this.showToast('success', 'Thank You!', 'Your blood donation has been recorded successfully.');
        this.cd.detectChanges();
      },
      error: (err: any) => {
        this.showDonateModal = false;
        const msg = err?.error?.message || err?.error || 'Failed to submit donation. Please try again.';
        this.showToast('error', 'Donation Failed', msg);
        this.cd.detectChanges();
      }
    });
  }

  openUpdateModal() {
    this.showUpdateModal = true;
    this.updateForm.patchValue({
      name: this.donor?.name,
      weight: this.donor?.weight,
      height: this.donor?.height,
      age: this.donor?.age,
      gender: this.donor?.gender,
      city: this.donor?.city,
      bloodGroup: this.donor?.bloodGroup,
      userId: this.donor?.userId
    });
  }

  submitUpdate() {
    if (this.updateForm.invalid) { this.updateForm.markAllAsTouched(); return; }
    const payload = { ...this.updateForm.getRawValue() };
    this.mydonorserv.updateDonor(this.donorId, payload).subscribe({
      next: (res: any) => {
        this.showUpdateModal = false;
        this.getDonorData();
        this.showToast('success', 'Profile Updated', 'Your profile has been updated successfully.');
        this.cd.detectChanges();
      },
      error: (err: any) => {
        const msg = err?.error?.message || err?.error || 'Failed to update profile.';
        this.showToast('error', 'Update Failed', msg);
      }
    });
  }

  editDonor(donor: any) {}
  deleteDonor(donor: number) {}
  addDonor() {}
  logout() {}
}
