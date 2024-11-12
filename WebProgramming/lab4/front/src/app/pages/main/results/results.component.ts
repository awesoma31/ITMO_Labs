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
  MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';

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
  entries: any[] = [];
  pageSize: number = 10;
  currentPage: number = 1;
  pointsService = inject(PointsService);
  totalEntries: number = 0;  // If you need total entries for pagination

  ngOnInit(): void {
    this.pointsService.points$.subscribe(points => {
      this.entries = points;
    });

    this.pointsService.totalEntries$.subscribe(total => {
      this.totalEntries = total;
    });

    this.loadPoints();
  }

  loadPoints(): void {
    this.pointsService.loadPoints(this.currentPage - 1, this.pageSize);
  }


  //todo add pagination refresh on point add
  onPageChange(pageNumber: number): void {
    this.currentPage = pageNumber;
    this.loadPoints();
  }
}
