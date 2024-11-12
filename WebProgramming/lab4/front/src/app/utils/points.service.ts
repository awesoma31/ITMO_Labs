import {inject, Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {PageDTO, Point} from './models.interface';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class PointsService {
    private http = inject(HttpClient);
    private baseApiUrl = environment.basePointsApiUrl;

    private _points = new BehaviorSubject<Point[]>([]);
    private totalEntriesSubject = new BehaviorSubject<number>(0);

    totalPages = 0;

    private xSubject = new BehaviorSubject<number>(0);
    private ySubject = new BehaviorSubject<number>(0);
    private rSubject = new BehaviorSubject<number>(1);
    private currentPageCountSubject = new BehaviorSubject<number>(0);

    points$ = this._points.asObservable();
    totalEntries$ = this.totalEntriesSubject.asObservable();
    currentPageCount$ = this.currentPageCountSubject.asObservable()
    x$ = this.xSubject.asObservable();
    y$ = this.ySubject.asObservable();
    r$ = this.rSubject.asObservable();

    pageSize = 10

    constructor() {
    }

    setX(value: number): void {
        this.xSubject.next(value);
    }

    setY(value: number): void {
        this.ySubject.next(value);
    }

    setR(value: number): void {
        this.rSubject.next(value);
    }

    loadPoints(page: number = 0, size: number = 10): void {
        this.http.get<PageDTO<Point>>(`${this.baseApiUrl}?page=${page}&size=${size}`).subscribe({
            next: (data) => {
                this._points.next(data.content);
                this.totalEntriesSubject.next(data.totalElements);
                this.totalPages = data.totalPages;
                this.currentPageCountSubject.next(data.content.length);
            },
            error: (err) => {
                console.error('Error fetching points:', err);
            }
        });
    }

    addPoint(pointData: any): void {
        this.http.post<any>(`${this.baseApiUrl}/add`, pointData).subscribe({
            next: newPoint => {
                const currentPoints = this._points.getValue();
                if (currentPoints.length < 10) {
                    currentPoints.unshift(newPoint);
                }
                this._points.next(currentPoints);

                const updatedCount = this.currentPageCountSubject.getValue() + 1;
                this.currentPageCountSubject.next(updatedCount);

                const updatedTotal = this.totalEntriesSubject.getValue() + 1;
                this.totalEntriesSubject.next(updatedTotal);

                if (updatedCount > this.pageSize) {
                    this.loadPoints(0, this.pageSize);
                }
            },
            error: error => {
                console.error('Error adding point:', error);
            }
        });
    }
}
