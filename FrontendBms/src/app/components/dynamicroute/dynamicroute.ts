import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-dynamicroute',
  standalone: false,
  templateUrl: './dynamicroute.html',
  styleUrl: './dynamicroute.css',
})
export class Dynamicroute implements OnInit {
   userId: string | null = '';
  constructor(private route: ActivatedRoute) {}
  ngOnInit(): void {
     this.userId = this.route.snapshot.paramMap.get('id');
  }
}
