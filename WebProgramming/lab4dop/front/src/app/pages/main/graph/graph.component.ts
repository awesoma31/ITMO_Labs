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
    private currentR = environment.defaultR;

    private cnvScale = environment.canvasScale;

    private pointRadius = 7;
    private polygonFillStyle = 'rgb(10, 10, 44)';
    private axisColor = 'white';
    private hitColor = 'rgb(119, 0, 255)';
    private missColor = 'rgb(150, 100, 100)';
    private axisLabelsColor = 'rgb(200, 190, 180)';
    private axisFont = "18px monospace";
    private missStrokeColor = "#ff6666";

    private hitStrokeColor = this.axisLabelsColor;

    constructor() {
        effect(() => {
            this.points = this.pointsService.points();
            this.drawGraph();
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
            this.animatePolygon(value);
            // this.drawGraph();
        });
        this.pointsService.pointsObservable$.subscribe(points => {
            this.points = points;
            this.drawGraph();
        });
    }

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

        this.drawPolygon(this.currentR);
        this.drawAxes();
        this.ctx.restore();

        this.drawAxisLabels(this.currentR);

        this.drawPoints();
    }

    private drawPolygon(r: number) {
        this.ctx.fillStyle = this.polygonFillStyle;
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
        this.ctx.strokeStyle = this.axisColor;
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

        this.ctx.fillStyle = this.axisLabelsColor; // Text fill color
        this.ctx.strokeStyle = "black"; // Border color for text
        this.ctx.lineWidth = 0.25; // Thickness of the text border
        this.ctx.font = this.axisFont;

        let xAxisLabelOffset = 2 * yLabelOffset + 2;
        const labels = [
            // x-axis labels
            {text: "R", x: this.canvas.width / 2 + a - xLabelOffset, y: this.canvas.height / 2 + xAxisLabelOffset},
            {
                text: "R/2",
                x: this.canvas.width / 2 + a / 2 - xLabelOffset,
                y: this.canvas.height / 2 + xAxisLabelOffset
            },
            {text: "-R", x: this.canvas.width / 2 - a - xLabelOffset, y: this.canvas.height / 2 + xAxisLabelOffset},
            {
                text: "-R/2",
                x: this.canvas.width / 2 - a / 2 - xLabelOffset,
                y: this.canvas.height / 2 + xAxisLabelOffset
            },

            // y-axis labels
            {text: "R", x: this.canvas.width / 2 + yLabelOffset, y: this.canvas.height / 2 - a + yLabelOffset},
            {text: "R/2", x: this.canvas.width / 2 + yLabelOffset, y: this.canvas.height / 2 - a / 2 + yLabelOffset},
            {text: "-R", x: this.canvas.width / 2 + yLabelOffset, y: this.canvas.height / 2 + a + yLabelOffset},
            {text: "-R/2", x: this.canvas.width / 2 + yLabelOffset, y: this.canvas.height / 2 + a / 2 + yLabelOffset},
        ];

        labels.forEach(label => {
            this.ctx.fillText(label.text, label.x, label.y);
            this.ctx.strokeText(label.text, label.x, label.y);
        });
    }

    private drawPoints() {
        this.points.forEach(point => {
            this.drawPoint(point.x, point.y, point.result);
        });
    }

    private drawPoint(x: number, y: number, result: boolean) {
        this.ctx.save();

        this.ctx.translate(this.canvas.width / 2, this.canvas.height / 2);
        this.ctx.scale(1, -1);


        if (result) {
            this.ctx.fillStyle = this.hitColor;
            this.ctx.strokeStyle = this.hitStrokeColor;
        } else {
            this.ctx.fillStyle = this.missColor;
            this.ctx.strokeStyle = this.missStrokeColor;
        }

        this.ctx.lineWidth = 0.5;

        this.ctx.beginPath();
        this.ctx.arc(x * this.cnvScale, y * this.cnvScale, this.pointRadius, 0, Math.PI * 2);

        this.ctx.fill();
        this.ctx.stroke();
        this.ctx.closePath();

        this.ctx.restore();
    }

    private animatePolygon(targetR: number): void {
        const animationStep = 0.05; // Adjust for smoothness

        const animate = () => {
            if (Math.abs(this.currentR - targetR) < animationStep) {
                this.currentR = targetR;
                this.drawGraph();
                return;
            }

            this.currentR += (targetR - this.currentR) * 0.1;
            this.drawGraph();

            requestAnimationFrame(animate);
        };

        requestAnimationFrame(animate);
    }
}
