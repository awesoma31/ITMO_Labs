import { Component } from '@angular/core';
import {NzCellFixedDirective, NzTableComponent, NzThMeasureDirective} from "ng-zorro-antd/table";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-results',
  standalone: true,
  imports: [
    NzTableComponent,
    NzThMeasureDirective,
    NgForOf,
    NzCellFixedDirective
  ],
  templateUrl: './results.component.html',
  styleUrl: './results.component.scss'
})
export class ResultsComponent {
  entries: any[] = [{x: 1, y: 2, r: 3, result: true}, {x: -1, y: 1, r: 4, result: false}];

}
