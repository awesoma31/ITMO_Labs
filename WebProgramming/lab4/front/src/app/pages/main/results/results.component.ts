import {Component, inject, OnInit} from '@angular/core';
import {NzCellFixedDirective, NzTableComponent, NzThMeasureDirective} from "ng-zorro-antd/table";
import {NgForOf} from "@angular/common";
import {NzPaginationComponent} from 'ng-zorro-antd/pagination';
import {PointsService} from '../../../utils/points.service';

@Component({
  selector: 'app-results',
  standalone: true,
  imports: [
    NzTableComponent,
    NzThMeasureDirective,
    NgForOf,
    NzCellFixedDirective,
    NzPaginationComponent,
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
    // this.totalEntries = this.pointsService.getTotalEntries();

    
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
