import {inject, Injectable, signal, WritableSignal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PageDTO, Point} from './models.interface';
import {environment} from '../../environments/environment';
import {BehaviorSubject} from 'rxjs';
import {NzMessageService} from 'ng-zorro-antd/message';

@Injectable({
    providedIn: 'root'
})
export class PointsService {
    private http = inject(HttpClient);
    private message = inject(NzMessageService);

    private pointsSubject = new BehaviorSubject<Point[]>([])
    private totalPointsCountSubject = new BehaviorSubject<number>(0);
    private currentPointsOnPageCountSubject = new BehaviorSubject<number>(0);
    private rSubject = new BehaviorSubject<number>(environment.defaultR);
    private baseApiUrl = environment.basePointsApiUrl;

    r$ = this.rSubject.asObservable();
    currentPointsOnPageCountObservable$ = this.currentPointsOnPageCountSubject.asObservable()
    pointsObservable$ = this.pointsSubject.asObservable();
    totalPointsCountObservable$ = this.totalPointsCountSubject.asObservable();

    private _currentPageNumber = signal(1);
    private _points = signal<Point[]>([]);
    private _pointsOnCurrentPage = signal(0);
    private _totalPointsCount = signal(0);
    private _pageSize = signal(environment.tablePageSize);

    private _x = signal(0);
    private _y = signal(0);
    private _r = signal(environment.defaultR);

    constructor() {}

    loadPoints(): void {
        const page = this.currentPageNumber() - 1;
        const size = this.pageSize();
        this.http.get<PageDTO<Point>>(`${this.baseApiUrl}/page?page=${page}&size=${size}`).subscribe({
            next: (data) => {
                this.points = data.content;
                this.pointsSubject.next(data.content);
                this.totalPointsCount = data.totalElements;
                this.pointsOnCurrentPage = data.content.length;

                this.currentPageNumber = data.totalElements % data.pageSize === 0 ?
                    Math.floor(data.totalElements / data.pageSize) :
                    Math.floor(data.totalElements / data.pageSize + 1);

                this.currentPointsOnPageCountSubject.next(data.content.length);
                this.totalPointsCountSubject.next(data.totalElements);
            },
            error: (err) => {
                console.error('Error fetching points:', err);
                this.message.error('Error fetching points' + err.message);
            }
        });
    }

    loadPage(page: number): void {
        const size = this.pageSize();
        this.http.get<PageDTO<Point>>(`${this.baseApiUrl}/page?page=${page}&size=${size}`).subscribe({
            next: (data) => {
                this.points = data.content;
                this.totalPointsCount = data.totalElements;
                this.pointsOnCurrentPage = data.content.length;
                this.currentPageNumber = page;

                this.pointsSubject.next(data.content);
                this.currentPointsOnPageCountSubject.next(data.content.length);
                this.totalPointsCountSubject.next(data.totalElements);
            },
            error: (err) => {
                console.error('Error fetching points:', err);
                this.message.error('Error fetching points' + err.message);
            }
        });
    }

    addPoint(pointData: any): void {
        this.http.post<any>(`${this.baseApiUrl}/add`, pointData).subscribe({
            next: newPoint => {
                const currentPoints = this.points();
                if (currentPoints.length < 10) {
                    currentPoints.push(newPoint);
                }
                this.points = currentPoints;
                this.pointsSubject.next(currentPoints);

                const updatedCount = this.pointsOnCurrentPage() + 1;
                this.pointsOnCurrentPage = updatedCount;
                this.currentPointsOnPageCountSubject.next(updatedCount);

                this.totalPointsCount = this.totalPointsCount() + 1;
                this.totalPointsCountSubject.next(this.totalPointsCount());

                if (updatedCount > this.pageSize()) {
                    this.loadPoints();
                }
                this.message.success('Point added successfully');
            },
            error: error => {
                console.error('Error adding point:', error);
                this.message.error('Error adding point' + error.message);
            }
        });
    }

    setR(value: number): void {
        this.rSubject.next(value);
    }

    get currentPageNumber(): WritableSignal<number> {
        return this._currentPageNumber;
    }

    set currentPageNumber(value: number) {
        this._currentPageNumber.set(value);
    }

    get points(): WritableSignal<Point[]> {
        return this._points;
    }

    set points(list: Point[]) {
        this._points.set(list);
    }

    get totalPointsCount(): WritableSignal<number> {
        return this._totalPointsCount;
    }

    set totalPointsCount(value: number) {
        this._totalPointsCount.set(value);
    }

    get x(): WritableSignal<number> {
        return this._x;
    }

    set x(value: number) {
        this._x.set(value);
    }

    get y(): WritableSignal<number> {
        return this._y;
    }

    set y(value: number) {
        this._y.set(value);
    }

    get r(): WritableSignal<number> {
        return this._r;
    }

    set r(value: number) {
        this._r.set(value);
    }

    get pointsOnCurrentPage(): WritableSignal<number> {
        return this._pointsOnCurrentPage;
    }

    set pointsOnCurrentPage(value: number) {
        this._pointsOnCurrentPage.set(value);
    }

    get pageSize(): WritableSignal<number> {
        return this._pageSize;
    }
}
