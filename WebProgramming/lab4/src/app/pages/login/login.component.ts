import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NzFormControlComponent, NzFormItemComponent, NzFormLabelComponent} from 'ng-zorro-antd/form';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RouterOutlet,
    NzFormControlComponent,
    NzFormItemComponent,
    NzFormLabelComponent
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

}
