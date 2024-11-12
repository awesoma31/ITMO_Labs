import {AfterViewInit, Component, ElementRef, inject, ViewChild} from '@angular/core';
import {PointsService} from '../../../utils/points.service';
import {CanvasSetupService} from './canvas-setup.service';

@Component({
    selector: 'app-graph',
    standalone: true,
    imports: [],
    templateUrl: './graph.component.html',
    styleUrl: './graph.component.scss'
})
export class GraphComponent implements AfterViewInit {
    @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;
    canvasSetup = inject(CanvasSetupService);
    pointsService = inject(PointsService)
    r = this.pointsService.r
    private cnvScale = 50;
    constructor() {
    }

    ngAfterViewInit(): void {
        const r = 1;
        this.canvasSetup.setupCanvas(this.canvasRef.nativeElement, r);
        this.drawGraph(r);
        this.drawDots(r);

    }

    onCanvasClick(e: MouseEvent): void {
        //todo
    }

    private drawGraph(R: number): void {
        this.canvasSetup.setDynamicScalingFactor(R);
        const canvas = this.canvasSetup.getCanvas()
        const ctx = this.canvasSetup.getContext();

        ctx.clearRect(0, 0, canvas.width, canvas.height);

        ctx.translate(canvas.width / 2, canvas.height / 2);
        ctx.scale(1, -1);

        this.drawPolygon(ctx, R);
        this.drawAxis(ctx, canvas);
    }

    private drawAxis(ctx: CanvasRenderingContext2D, canvas: HTMLCanvasElement): void {
        ctx.strokeStyle = "black";
        ctx.beginPath();
        ctx.moveTo(-canvas.width / 2, 0);
        ctx.lineTo(canvas.width / 2, 0);
        ctx.moveTo(0, -canvas.height / 2);
        ctx.lineTo(0, canvas.height / 2);
        ctx.stroke();
    }

    private drawPolygon(ctx: CanvasRenderingContext2D, r: number): void {
        ctx.fillStyle = 'rgb(51 153 255)';
        ctx.beginPath();

        // Top right triangle
        ctx.moveTo(0, 0);
        const a = r * this.cnvScale;
        ctx.lineTo(0, a / 2);
        ctx.lineTo(a, 0);

        // Bottom right rectangle
        ctx.lineTo(a, -a / 2);
        ctx.lineTo(0, -a / 2);

        // Bottom left arc
        ctx.arc(0, 0, a / 2, -Math.PI / 2, -Math.PI, true);

        ctx.closePath();
        ctx.fill();
    }

    private drawDots(r: number) {
        const canvas = this.canvasSetup.getCanvas()
        const ctx = this.canvasSetup.getContext()

    }

    drawDot(x: number, y: number, result: boolean) {
        const canvas = this.canvasSetup.getCanvas()
        const ctx = this.canvasSetup.getContext()

        if (result) {
            ctx.fillStyle = "purple"
        } else {
            ctx.fillStyle = "red"
        }

        ctx.beginPath();
        ctx.arc(x * this.cnvScale, y * this.cnvScale, 4, 0, Math.PI * 2);
        ctx.fill();
    }
}
