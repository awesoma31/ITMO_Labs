import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ScreenSizeService {
  private screenSize = new BehaviorSubject<string>('desktop');
  screenSize$ = this.screenSize.asObservable();

  constructor() {
    if (this.isBrowser()) {
      this.checkScreenSize();
      window.addEventListener('resize', () => this.checkScreenSize());
    }
  }

  private checkScreenSize() {
    if (this.isBrowser()) {
      const width = window.innerWidth;
      if (width >= 1211) {
        this.screenSize.next('desktop');
      } else if (width >= 783) {
        this.screenSize.next('tablet');
      } else {
        this.screenSize.next('mobile');
      }
    }
  }

  private isBrowser(): boolean {
    return typeof window !== 'undefined';
  }
}
