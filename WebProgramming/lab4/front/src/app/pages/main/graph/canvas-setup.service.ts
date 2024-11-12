import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class CanvasSetupService {
    private canvas!: HTMLCanvasElement;
    private ctx!: CanvasRenderingContext2D;

    setupCanvas(canvas: HTMLCanvasElement, R: number): void {
        if (!canvas) {
            throw new Error("Canvas element not provided");
        }
        this.canvas = canvas;
        const ctx = canvas.getContext("2d");
        if (!ctx) {
            throw new Error("2D context not found");
        }
        this.ctx = ctx;
    }

    getCanvas(): HTMLCanvasElement {
        return this.canvas;
    }

    getContext(): CanvasRenderingContext2D {
        return this.ctx;
    }

}
