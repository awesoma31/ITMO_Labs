import {Component, effect, inject, OnInit} from '@angular/core';
import {NzCellFixedDirective, NzTableComponent, NzThMeasureDirective} from "ng-zorro-antd/table";
import {NgForOf} from "@angular/common";
import {NzPaginationComponent} from 'ng-zorro-antd/pagination';
import {PointsService} from '../../../utils/points.service';
import {MatPaginator} from '@angular/material/paginator';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable
} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {Point} from '../../../utils/models.interface';
import {environment} from '../../../../environments/environment';
import {NzMessageService} from 'ng-zorro-antd/message';

@Component({
    selector: 'app-results',
    standalone: true,
    imports: [
        NzTableComponent,
        NzThMeasureDirective,
        NgForOf,
        NzCellFixedDirective,
        NzPaginationComponent,
        MatPaginator,
        MatTable,
        MatColumnDef,
        MatHeaderCell,
        MatCell,
        MatHeaderCellDef,
        MatCellDef,
        MatSort,
        MatHeaderRow,
        MatRow,
        MatRowDef,
        MatHeaderRowDef,
    ],
    templateUrl: './results.component.html',
    styleUrl: './results.component.scss'
})
export class ResultsComponent implements OnInit {
    private pointsService = inject(PointsService);
    private message = inject(NzMessageService);

    pointsPage: Point[] = [];
    pointsOnCurrentPage: number = 0;
    pageSize: number = environment.tablePageSize;
    currentPageNumber: number = 1;
    totalEntries: number = 0;
    currentPageCount: number = 0;

    constructor() {
        effect(() => {
            this.pointsPage = this.pointsService.points();

            this.pointsOnCurrentPage = this.pointsService.points().length
            this.totalEntries = this.pointsService.totalPointsCount();
            this.currentPageCount = this.pointsService.pointsOnCurrentPage();
            this.currentPageNumber = this.pointsService.currentPageNumber();
        });
    }

    ngOnInit(): void {
        this.pointsService.pointsObservable$.subscribe(points => {
            this.pointsPage = points;
            this.pointsOnCurrentPage = points.length
        });

        this.pointsService.totalPointsCountObservable$.subscribe(total => {
            this.totalEntries = total;

        });

        this.pointsService.currentPointsOnPageCountObservable$.subscribe(count => {
            this.currentPageCount = count;
        });

        this.pointsService.loadLastPage();
    }

    onPageChange(pageNumber: number): void {

        try {
            this.pointsService.currentPageNumber = pageNumber;
            this.pointsService.loadPage(pageNumber);
        } catch (e: any) {
            console.error(e.message);
            this.message.error(e.message);
        }
    }
}
