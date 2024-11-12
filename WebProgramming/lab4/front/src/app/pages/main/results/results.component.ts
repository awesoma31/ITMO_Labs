import {Component, inject, OnInit} from '@angular/core';
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
    pointsService = inject(PointsService);

    pointsPage: Point[] = [];
    pointsOnCurrentPage: number = 0;
    pageSize: number = environment.tablePageSize;
    currentPage: number = 1;
    totalEntries: number = 0;
    currentPageCount: number = 0;

    ngOnInit(): void {
        this.pointsService.points$.subscribe(points => {
            this.pointsPage = points;
            this.pointsOnCurrentPage = points.length
        });

        this.pointsService.totalEntries$.subscribe(total => {
            this.totalEntries = total;

        });

        this.pointsService.currentPageCount$.subscribe(count => {
            this.currentPageCount = count;
        });

        this.loadPoints();
    }

        loadPoints(): void {
        this.pointsService.loadPoints(this.currentPage - 1, this.pageSize);
    }

    onPageChange(pageNumber: number): void {
        this.currentPage = pageNumber;
        this.loadPoints();
    }
}
