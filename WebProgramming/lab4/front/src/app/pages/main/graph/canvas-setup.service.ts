import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CanvasSetupService {
  private width!: number;
  private height!: number;
  private canvas!: HTMLCanvasElement;
  private ctx!: CanvasRenderingContext2D;
  private baseScaling!: number;
  private dynamicScalingFactor!: number;
  private k: number = 1.7; // Edit this constant if you want other scale for graph
  private isDrawMode: boolean = false;
  private isMagnetMode: boolean = false;

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
    this.setWidth(this.canvas.width);
    this.setHeight(this.canvas.height);
    this.k = 1.7; // edit this constant if you want other scale for graph
    this.baseScaling = this.width / 6;
    this.setDynamicScalingFactor(R);
  }

  getCanvas(): HTMLCanvasElement {
    return this.canvas;
  }

  getContext(): CanvasRenderingContext2D {
    return this.ctx;
  }

  getWidth(): number {
    return this.width;
  }

  getHeight(): number {
    return this.height;
  }

  getBaseScaling(): number {
    return this.baseScaling;
  }

  getDynamicScalingFactor(): number {
    return this.dynamicScalingFactor;
  }

  getK(): number {
    return this.k;
  }

  getGraphSetup(): any {
    return {
      ctx: this.getContext(),
      width: this.getWidth(),
      height: this.getHeight(),
      k: this.getK(),
      dynamicScalingFactor: this.getDynamicScalingFactor()
    };
  }

  getDrawModeState(): boolean {
    return this.isDrawMode;
  }

  getMagnetModeState(): boolean {
    return this.isMagnetMode;
  }

  setWidth(newWidth: number): void {
    this.width = newWidth;
  }

  setHeight(newHeight: number): void {
    this.height = newHeight;
  }

  setDynamicScalingFactor(R: number): void {
    this.dynamicScalingFactor = this.getBaseScaling() * this.getK() / R;
  }

  setK(newK: number): void {
    this.k = newK;
  }

  setDrawModeState(state: boolean): void {
    this.isDrawMode = state;
  }

  setMagnetModeState(state: boolean): void {
    this.isMagnetMode = state;
  }
}
