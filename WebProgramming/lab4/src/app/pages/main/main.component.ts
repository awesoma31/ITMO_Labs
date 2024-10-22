import { Component } from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {ScreenSizeService} from "../../utils/screen-size.service";
import {NgForOf, NgIf} from "@angular/common";
import {NzButtonComponent} from "ng-zorro-antd/button";
import {NzMessageService} from "ng-zorro-antd/message";
import {NzInputDirective} from "ng-zorro-antd/input";
import {NzInputNumberGroupSlotComponent} from "ng-zorro-antd/input-number";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    RouterOutlet,
    NgIf,
    NzButtonComponent,
    NgForOf,
    NzInputDirective,
    NzInputNumberGroupSlotComponent
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent {
  currentScreenSize: string = 'desktop';

  constructor(private screenSizeService: ScreenSizeService) {}

  ngOnInit() {
    this.screenSizeService.screenSize$.subscribe(size => {
      this.currentScreenSize = size;
    });
  }

}
