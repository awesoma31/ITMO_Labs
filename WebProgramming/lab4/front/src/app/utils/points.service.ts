import {inject, Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {PageDTO, Point} from './pageResponse.interface';

@Injectable({
  providedIn: 'root'
})
export class PointsService {
  http = inject(HttpClient);
  private baseApiUrl = 'http://localhost:8080/points';
  private pointsSubject = new BehaviorSubject<any[]>([]);
  public points$ = this.pointsSubject.asObservable();
  private totalEntriesSubject = new BehaviorSubject<number>(0);
  public totalEntries$ = this.totalEntriesSubject.asObservable();
  public totalPages = 0;
  //todo move from here
  private _r:number = 0;

  constructor() {}

  set r(val: number) {
    this._r = val;
  }

  get r(): number {
    return this._r
  }

  loadPoints(page: number = 0, size: number = 10): void {
    this.http.get<PageDTO<Point>>(`${this.baseApiUrl}?page=${page}&size=${size}`).subscribe({
      next: (data) => {
        this.pointsSubject.next(data.content);
        this.totalEntriesSubject.next(data.totalElements);
        this.totalPages = data.totalPages;
      },
      error: (err) => {
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
}
