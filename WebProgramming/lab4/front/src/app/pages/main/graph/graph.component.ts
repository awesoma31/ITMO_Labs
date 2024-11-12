import {AfterViewInit, Component, ElementRef, inject, ViewChild} from '@angular/core';
import {PointsService} from '../../../utils/points.service';
import {CanvasSetupService} from './canvas-setup.service';
import {Point} from '../../../utils/models.interface';
import {environment} from '../../../../environments/environment';


@Component({
    selector: 'app-graph',
    standalone: true,
    imports: [],
    templateUrl: './graph.component.html',
    styleUrl: './graph.component.scss'
})
export class GraphComponent implements AfterViewInit {
    @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;
    private canvasSetup = inject(CanvasSetupService);
    private pointsService = inject(PointsService)

    private points: Point[] = [];
    private r = environment.defaultR;
    private cnvScale = environment.canvasScale;

    constructor() {
    }

    ngAfterViewInit(): void {
        this.canvasSetup.setupCanvas(this.canvasRef.nativeElement, this.r);

        this.pointsService.r$.subscribe(value => {
            this.r = value;
            this.drawGraph();
        });
        this.pointsService.points$.subscribe(points => {
            this.points = points;
            this.drawGraph();
        });
    }

    //todo wrong points redrawing when more than 10

    //todo alert popup

    onCanvasClick(e: MouseEvent): void {
        const canvas = this.canvasSetup.getCanvas();
        const rect = canvas.getBoundingClientRect();

        const x = e.clientX - rect.left - canvas.width / 2;
        const y = -(e.clientY - rect.top - canvas.height / 2);

        const logicalX = (x / this.cnvScale).toFixed(2);
        const logicalY = (y / this.cnvScale).toFixed(2);

        const newPoint = {x: logicalX, y: logicalY, r: this.r};
        this.pointsService.addPoint(newPoint);
    }

    public drawGraph() {
        const canvas = this.canvasSetup.getCanvas()
        const ctx = this.canvasSetup.getContext();

        ctx.clearRect(0, 0, canvas.width, canvas.height);

        ctx.save();

        ctx.translate(canvas.width / 2, canvas.height / 2);
        ctx.scale(1, -1);

        this.drawPolygon(this.r);
        this.drawAxes();
        ctx.restore();

        this.drawAxisLabels(this.r);

        this.drawPoints();
    }

    private drawPolygon(r: number) {
        const ctx = this.canvasSetup.getContext()

        ctx.fillStyle = 'rgb(255 255 51)';
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

    private drawAxes(): void {
        const canvas = this.canvasSetup.getCanvas()
        const ctx = this.canvasSetup.getContext()

        ctx.strokeStyle = "black";
        ctx.beginPath();
        ctx.moveTo(-canvas.width / 2, 0);
        ctx.lineTo(canvas.width / 2, 0);
        ctx.moveTo(0, -canvas.height / 2);
        ctx.lineTo(0, canvas.height / 2);
        ctx.stroke();
    }

    private drawAxisLabels(r: number) {
        const canvas = this.canvasSetup.getCanvas();
        const ctx = this.canvasSetup.getContext();

        const a = r * this.cnvScale;
        const xLabelOffset = 15;
        const yLabelOffset = 5;

        ctx.fillStyle = "black";
        ctx.font = "16px monospace";

        // Positive x-axis labels
        ctx.fillText("R", canvas.width / 2 + a - xLabelOffset, canvas.height / 2 + yLabelOffset);
        ctx.fillText("R/2", canvas.width / 2 + a / 2 - xLabelOffset, canvas.height / 2 + yLabelOffset);

        // Negative x-axis labels
        ctx.fillText("-R", canvas.width / 2 - a - xLabelOffset, canvas.height / 2 + yLabelOffset);
        ctx.fillText("-R/2", canvas.width / 2 - a / 2 - xLabelOffset, canvas.height / 2 + yLabelOffset);

        // Positive y-axis labels
        ctx.fillText("R", canvas.width / 2 + yLabelOffset, canvas.height / 2 - a + yLabelOffset);
        ctx.fillText("R/2", canvas.width / 2 + yLabelOffset, canvas.height / 2 - a / 2 + yLabelOffset);

        // Negative y-axis labels
        ctx.fillText("-R", canvas.width / 2 + yLabelOffset, canvas.height / 2 + a + yLabelOffset);
        ctx.fillText("-R/2", canvas.width / 2 + yLabelOffset, canvas.height / 2 + a / 2 + yLabelOffset);
    }

    private drawPoints() {
        this.points.forEach(point => {
            this.drawDot(point.x, point.y, point.result);
        });
    }

    private drawDot(x: number, y: number, result: boolean) {
        const canvas = this.canvasSetup.getCanvas();
        const ctx = this.canvasSetup.getContext();

        ctx.save();

        ctx.translate(canvas.width / 2, canvas.height / 2);
        ctx.scale(1, -1);

        if (result) {
            ctx.fillStyle = "blue";
        } else {
            ctx.fillStyle = "red";
        }

        ctx.beginPath();
        ctx.arc(x * this.cnvScale, y * this.cnvScale, 4, 0, Math.PI * 2);
        ctx.fill();
        ctx.closePath();

        ctx.restore();
    }
}
