import {inject, Injectable, signal, WritableSignal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PageDTO, Point} from './models.interface';
import {environment} from '../../environments/environment';
import {BehaviorSubject} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class PointsService {
    public pointsSubject = new BehaviorSubject<Point[]>([])
    pointsObservable$ = this.pointsSubject.asObservable();
    private http = inject(HttpClient);
    private baseApiUrl = environment.basePointsApiUrl;
    private rSubject = new BehaviorSubject<number>(environment.defaultR);
    r$ = this.rSubject.asObservable();
    private currentPageCountSubject = new BehaviorSubject<number>(0);
    currentPageCountObservable$ = this.currentPageCountSubject.asObservable()
    private totalPointsSubject = new BehaviorSubject<number>(0);
    totalPointsObservable$ = this.totalPointsSubject.asObservable();

    constructor() {
    }

    private _points = signal<Point[]>([]);

    get points(): WritableSignal<Point[]> {
        return this._points;
    }

    set points(list: Point[]) {
        this._points = signal(list);
    }

    private _totalPointsCount = signal(0);

    get totalPointsCount(): WritableSignal<number> {
        return this._totalPointsCount;
    }

    set totalPointsCount(value: number) {
        this._totalPointsCount = signal(value);
    }

    private _x = signal(0);

    get x(): WritableSignal<number> {
        return this._x;
    }

    set x(value: number) {
        this._x = signal(value);
    }

    private _y = signal(0);

    get y(): WritableSignal<number> {
        return this._y;
    }

    set y(value: number) {
        this._y = signal(value);
    }

    private _r = signal(environment.defaultR);

    get r(): WritableSignal<number> {
        return this._r;
    }

    set r(value: number) {
        this._r = signal(value);
    }

    private _currentPageCount = signal(0);

    get currentPageCount(): WritableSignal<number> {
        return this._currentPageCount;
    }

    set currentPageCount(value: number) {
        this._currentPageCount = signal(value);
    }

    private _pageSize = signal(environment.tablePageSize);

    get pageSize(): WritableSignal<number> {
        return this._pageSize;
    }

    loadPoints(page: number = 0, size: number = 10): void {
        this.http.get<PageDTO<Point>>(`${this.baseApiUrl}?page=${page}&size=${size}`).subscribe({
            next: (data) => {
                this.points = data.content;
                this.pointsSubject.next(data.content);
                this.totalPointsCount = data.totalElements;
                this.currentPageCount = data.content.length;

                this.currentPageCountSubject.next(data.content.length);
                this.totalPointsSubject.next(data.totalElements);
            },
            error: (err) => {
                console.error('Error fetching points:', err);
            }
        });
    }

    addPoint(pointData: any): void {
        this.http.post<any>(`${this.baseApiUrl}/add`, pointData).subscribe({
            next: newPoint => {
                const currentPoints = this.points();
                if (currentPoints.length < 10) {
                    currentPoints.unshift(newPoint);
                }
                this.points = currentPoints;
                this.pointsSubject.next(currentPoints);

                const updatedCount = this.currentPageCount() + 1;
                this.currentPageCount = updatedCount;
                this.currentPageCountSubject.next(updatedCount);

                this.totalPointsCount = this.totalPointsCount() + 1;
                this.totalPointsSubject.next(this.totalPointsCount());

                if (updatedCount > this.pageSize()) {
                    //todo check
                    this.loadPoints(0, this.pageSize());
                }
            },
            error: error => {
                console.error('Error adding point:', error);
            }
        });
    }

    setR(value: number): void {
        this.rSubject.next(value);
    }
}
