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
    private _lastPageNumber = signal(1);

    private _x = signal(0);
    private _y = signal(0);
    private _r = signal(environment.defaultR);

    constructor() {
    }

    loadLastPage(): void {
        const size = this.pageSize();

        this.http.get<PageDTO<Point>>(`${this.baseApiUrl}/page?page=last&size=${size}`).subscribe({
            next: (data) => {
                this.points = data.content;
                this.totalPointsCount = data.totalElements;
                this.pointsOnCurrentPage = data.content.length;
                this.lastPageNumber = data.totalPages;

                this.pointsSubject.next(data.content);
                this.totalPointsCountSubject.next(data.totalElements);
                this.currentPointsOnPageCountSubject.next(data.content.length);

                this.currentPageNumber = data.pageNumber;
            },
            error: (err) => {
                console.error('Error fetching points:', err);
                this.message.error('Error fetching points' + err.message);
            }
        });
    }

    loadPage(page: number | string): void {
        if (typeof page === 'number' && page < 0) {
            throw new Error("Page number must be greater than 0");
        }
        const size = this.pageSize();
        this.http.get<PageDTO<Point>>(`${this.baseApiUrl}/page?page=${page}&size=${size}`).subscribe({
            next: (data) => {
                this.points = data.content;
                this.totalPointsCount = data.totalElements;
                this.pointsOnCurrentPage = data.content.length;
                if (typeof page === "number") {
                    this.currentPageNumber = page;
                }

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
        this.currentPageNumber = this.lastPageNumber();

        this.http.post<Point>(`${this.baseApiUrl}/add`, pointData).subscribe({
            next: newPoint => {
                const previousPoints = this.points();
                if (previousPoints.length < 10) {
                    this.points().push(newPoint);
                    this.pointsSubject.next(this.points());

                    const updatedCount = this.pointsOnCurrentPage() + 1;
                    this.pointsOnCurrentPage = updatedCount;
                    this.currentPointsOnPageCountSubject.next(updatedCount);


                } else {
                    this._lastPageNumber.update(val => val + 1);
                    this.currentPageNumber = this.lastPageNumber();

                    this.points = [newPoint];
                    this.pointsSubject.next([newPoint]);

                    const updatedCount = 1;
                    this.pointsOnCurrentPage = updatedCount;
                    this.currentPointsOnPageCountSubject.next(updatedCount);
                }

                const updateTotalPoints = this.totalPointsCount() + 1;
                this.totalPointsCount = updateTotalPoints;
                this.totalPointsCountSubject.next(updateTotalPoints);
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

    get lastPageNumber(): WritableSignal<number> {
        return this._lastPageNumber;
    }

    set lastPageNumber(value: number) {
        this._lastPageNumber.set(value);
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
