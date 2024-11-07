import {Component, inject} from '@angular/core';
import {NzCellFixedDirective, NzTableComponent, NzThMeasureDirective} from "ng-zorro-antd/table";
import {NgForOf} from "@angular/common";
import {HttpClient} from '@angular/common/http';
import {NzPaginationComponent} from 'ng-zorro-antd/pagination';

@Component({
  selector: 'app-results',
  standalone: true,
  imports: [
    NzTableComponent,
    NzThMeasureDirective,
    NgForOf,
    NzCellFixedDirective,
    NzPaginationComponent
  ],
  templateUrl: './results.component.html',
  styleUrl: './results.component.scss'
})
export class ResultsComponent {
  entries: any[] = [];
  totalEntries: number = 0;  // Total number of entries for pagination.
  pageSize: number = 10;
  currentPage: number = 1;
  baseApiUrl: string = 'http://localhost:8080/points';
  http = inject(HttpClient);

  ngOnInit(): void {
    this.loadPoints(this.currentPage, this.pageSize);
  }

  loadPoints(page: number, size: number): void {
    const token = localStorage.getItem('token'); // Adjust as needed
    const headers = { Authorization: `Bearer ${token}` };

    this.http.get<any>(`${this.baseApiUrl}?page=${page - 1}&size=${size}`, { headers }).subscribe({
      next: data => {
        this.entries = data.content;
        this.totalEntries = data.totalElements;
      },
      error: err => {
        console.error('Error fetching points:', err);
      }
    });
  }

  onPageChange(pageNumber: number): void {
    this.currentPage = pageNumber;
    this.loadPoints(this.currentPage, this.pageSize);
  }
}
