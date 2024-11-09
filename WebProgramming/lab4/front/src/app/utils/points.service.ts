import {inject, Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PointsService {
  http = inject(HttpClient);
  private baseApiUrl = 'http://localhost:8080/points';
  private pointsSubject = new BehaviorSubject<any[]>([]);
  public points$ = this.pointsSubject.asObservable();
  public totalEntries =  0;

  constructor() {}

  loadPoints(page: number = 0, size: number = 10): void {
    this.http.get<any>(`${this.baseApiUrl}?page=${page}&size=${size}`).subscribe({
      next: data => {
        this.pointsSubject.next(data.content); // Assuming backend returns Page object with 'content' containing the list of points
      },
      error: err => {
        console.error('Error fetching points:', err);
      }
    });
  }

  addPoint(pointData: any): void {
    this.http.post<any>(`${this.baseApiUrl}/add`, pointData).subscribe({
      next: newPoint => {
        const currentPoints = this.pointsSubject.getValue();
        if (currentPoints.length < 10) {
          currentPoints.unshift(newPoint);
        }
        this.pointsSubject.next(currentPoints);
      },
      error: error => {
        console.error('Error adding point:', error);
      }
    });
  }

  getTotalEntries(): number {
    this.http.get<any>(`${this.baseApiUrl}/total`).subscribe({
      next: data => {
        this.totalEntries = data;
      },
      error: err => {
        console.error('Error fetching total entries:', err);
        this.totalEntries = 0;
      }
    });
    return this.totalEntries;
  }
}
