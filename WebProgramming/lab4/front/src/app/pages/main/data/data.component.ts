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
    private pointsService = inject(PointsService)
    private message = inject(NzMessageService);

    x: number = environment.defaultX;
    y: number = environment.defaultY;
    r: number = environment.defaultR;

    constructor() {
        this.pointsService.x$.subscribe(value => this.x = value);
        this.pointsService.y$.subscribe(value => this.y = value);
        this.pointsService.r$.subscribe(value => this.r = value);
    }

    sendPoint(): void {
        const data = {
            x: this.x,
            y: this.y,
            r: this.r
        };
        console.log('Sending point', data);
        this.pointsService.addPoint(data);
        this.message.success('Point added successfully');
    }

    updateX(value: number): void {
        this.pointsService.setX(value);
    }

    updateY(value: number): void {
        this.pointsService.setY(value);
    }

    updateR(value: number): void {
        this.pointsService.setR(value);
    }
}
