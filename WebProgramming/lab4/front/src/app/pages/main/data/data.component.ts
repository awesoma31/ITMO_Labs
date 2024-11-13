import {Component, inject} from '@angular/core';
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
import {PointsService} from '../../../utils/points.service';
import {environment} from '../../../../environments/environment';
import {NzMessageModule, NzMessageService} from 'ng-zorro-antd/message';

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
        NzIconDirective,
        NzMessageModule
    ],
    templateUrl: './data.component.html',
    styleUrl: './data.component.scss'
})
export class DataComponent {
    x: number = environment.defaultX;
    y: number = environment.defaultY;
    r: number = environment.defaultR;
    private pointsService = inject(PointsService)
    private message = inject(NzMessageService);

    constructor() {
        this.x = this.pointsService.x();
        this.y = this.pointsService.y();
        this.r = this.pointsService.r();
        this.pointsService.r$.subscribe(r => {
            this.r = r;
        });
    }

    sendPoint(): void {
        const data = {
            x: this.x,
            y: this.y,
            r: this.r
        };
        this.pointsService.addPoint(data);
    }

    updateX(value: number): void {
        this.pointsService.x = value;
    }

    updateY(value: number): void {
        this.pointsService.y = value;
    }

    updateR(value: number): void {
        this.pointsService.r = value;
        this.pointsService.setR(value);
    }
}
