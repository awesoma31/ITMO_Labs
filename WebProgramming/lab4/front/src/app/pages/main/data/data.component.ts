import {Component, inject, OnChanges, SimpleChanges} from '@angular/core';
import {NzInputNumberComponent} from 'ng-zorro-antd/input-number';
import {FormsModule} from "@angular/forms";
import {NzSliderComponent} from "ng-zorro-antd/slider";
import {NzInputGroupComponent} from "ng-zorro-antd/input";
import {NzFormControlComponent, NzFormItemComponent, NzFormLabelComponent} from "ng-zorro-antd/form";
import {NzButtonComponent} from "ng-zorro-antd/button";
import {NzColDirective} from "ng-zorro-antd/grid";
import {NgStyle} from "@angular/common";
import {RouterOutlet} from "@angular/router";
import {NzDividerComponent} from "ng-zorro-antd/divider";
import {NzSpaceComponent, NzSpaceItemDirective} from "ng-zorro-antd/space";
import {NzCardComponent} from "ng-zorro-antd/card";
import {NzIconDirective} from "ng-zorro-antd/icon";
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {PointsService} from '../../../utils/points.service';

@Component({
  selector: 'app-data',
  standalone: true,
  imports: [
    NzInputNumberComponent,
    FormsModule,
    NzSliderComponent,
    NzInputGroupComponent,
    NzFormItemComponent,
    NzButtonComponent,
    NzColDirective,
    NgStyle,
    RouterOutlet,
    NzDividerComponent,
    NzSpaceComponent,
    NzSpaceItemDirective,
    NzCardComponent,
    NzFormLabelComponent,
    NzFormControlComponent,
    NzIconDirective
  ],
  templateUrl: './data.component.html',
  styleUrl: './data.component.scss'
})
export class DataComponent implements OnChanges{
  http = inject(HttpClient);
  baseApiUrl = 'http://localhost:8080/points/';
  pointsService = inject(PointsService)

  x: number = 0;
  y: number = 0;
  r: number = 1;

  ngOnChanges(changes: SimpleChanges) {
    this.pointsService.r = this.r
  }

  sendPoint(): void {
    const data = {
      x: this.x,
      y: this.y,
      r: this.r
    };

    console.log('Sending point', data);

    this.pointsService.addPoint(data);

  }

}
