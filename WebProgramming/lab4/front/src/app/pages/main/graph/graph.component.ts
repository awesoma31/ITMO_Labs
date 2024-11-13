import {AfterViewInit, Component, effect, ElementRef, inject, ViewChild} from '@angular/core';
import {PointsService} from '../../../utils/points.service';
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
    private pointsService = inject(PointsService)
    private canvas!: HTMLCanvasElement;
    private ctx!: CanvasRenderingContext2D;

    private points: Point[] = this.pointsService.points();
    private r = environment.defaultR;
    private cnvScale = environment.canvasScale;

    constructor() {
        effect(() => {
            this.points = this.pointsService.points();
        });
    }

    ngAfterViewInit(): void {
        this.canvas = this.canvasRef.nativeElement;
        const ctx = this.canvas.getContext("2d");
        if (!ctx) {
            throw new Error("2D context not found");
        }
        this.ctx = ctx;

        this.pointsService.r$.subscribe(value => {
            this.r = value;
            this.drawGraph();
        });
        this.pointsService.pointsObservable$.subscribe(points => {
            this.points = points;
            this.drawGraph();
        });
    }

    //todo wrong points redrawing when more than 10

    //todo alert popup

    onCanvasClick(e: MouseEvent): void {
        const rect = this.canvas.getBoundingClientRect();

        const x = e.clientX - rect.left - this.canvas.width / 2;
        const y = -(e.clientY - rect.top - this.canvas.height / 2);

        const logicalX = (x / this.cnvScale).toFixed(2);
        const logicalY = (y / this.cnvScale).toFixed(2);

        const newPoint = {x: logicalX, y: logicalY, r: this.r};
        this.pointsService.addPoint(newPoint);
    }

    public drawGraph() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        this.ctx.save();

        this.ctx.translate(this.canvas.width / 2, this.canvas.height / 2);
        this.ctx.scale(1, -1);

        this.drawPolygon(this.r);
        this.drawAxes();
        this.ctx.restore();

        this.drawAxisLabels(this.r);

        this.drawPoints();
    }

    private drawPolygon(r: number) {
        this.ctx.fillStyle = 'rgb(255 255 51)';
        this.ctx.beginPath();

        // Top right triangle
        this.ctx.moveTo(0, 0);
        const a = r * this.cnvScale;
        this.ctx.lineTo(0, a / 2);
        this.ctx.lineTo(a, 0);

        // Bottom right rectangle
        this.ctx.lineTo(a, -a / 2);
        this.ctx.lineTo(0, -a / 2);

        // Bottom left arc
        this.ctx.arc(0, 0, a / 2, -Math.PI / 2, -Math.PI, true);

        this.ctx.closePath();
        this.ctx.fill();
    }

    private drawAxes(): void {
        this.ctx.strokeStyle = "black";
        this.ctx.beginPath();
        this.ctx.moveTo(-this.canvas.width / 2, 0);
        this.ctx.lineTo(this.canvas.width / 2, 0);
        this.ctx.moveTo(0, -this.canvas.height / 2);
        this.ctx.lineTo(0, this.canvas.height / 2);
        this.ctx.stroke();
    }

    private drawAxisLabels(r: number) {
        const a = r * this.cnvScale;
        const xLabelOffset = 15;
        const yLabelOffset = 5;

        this.ctx.fillStyle = "black";
        this.ctx.font = "16px monospace";

        // Positive x-axis labels
        this.ctx.fillText("R", this.canvas.width / 2 + a - xLabelOffset, this.canvas.height / 2 + yLabelOffset);
        this.ctx.fillText("R/2", this.canvas.width / 2 + a / 2 - xLabelOffset, this.canvas.height / 2 + yLabelOffset);

        // Negative x-axis labels
        this.ctx.fillText("-R", this.canvas.width / 2 - a - xLabelOffset, this.canvas.height / 2 + yLabelOffset);
        this.ctx.fillText("-R/2", this.canvas.width / 2 - a / 2 - xLabelOffset, this.canvas.height / 2 + yLabelOffset);

        // Positive y-axis labels
        this.ctx.fillText("R", this.canvas.width / 2 + yLabelOffset, this.canvas.height / 2 - a + yLabelOffset);
        this.ctx.fillText("R/2", this.canvas.width / 2 + yLabelOffset, this.canvas.height / 2 - a / 2 + yLabelOffset);

        // Negative y-axis labels
        this.ctx.fillText("-R", this.canvas.width / 2 + yLabelOffset, this.canvas.height / 2 + a + yLabelOffset);
        this.ctx.fillText("-R/2", this.canvas.width / 2 + yLabelOffset, this.canvas.height / 2 + a / 2 + yLabelOffset);
    }

    private drawPoints() {
        this.points.forEach(point => {
            this.drawDot(point.x, point.y, point.result);
        });
    }

    private drawDot(x: number, y: number, result: boolean) {
        this.ctx.save();

        this.ctx.translate(this.canvas.width / 2, this.canvas.height / 2);
        this.ctx.scale(1, -1);

        if (result) {
            this.ctx.fillStyle = "blue";
        } else {
            this.ctx.fillStyle = "red";
        }

        this.ctx.beginPath();
        this.ctx.arc(x * this.cnvScale, y * this.cnvScale, 4, 0, Math.PI * 2);
        this.ctx.fill();
        this.ctx.closePath();

        this.ctx.restore();
    }
}
