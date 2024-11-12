import {Component, inject} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {NzFormControlComponent, NzFormDirective, NzFormItemComponent, NzFormLabelComponent} from 'ng-zorro-antd/form';
import {NzInputDirective} from 'ng-zorro-antd/input';
import {NzButtonComponent} from 'ng-zorro-antd/button';
import {AuthService} from "../../auth/auth.service";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {HttpErrorResponse} from '@angular/common/http';
import {NzMessageModule, NzMessageService} from 'ng-zorro-antd/message';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [
        RouterOutlet,
        NzFormControlComponent,
        NzFormItemComponent,
        NzFormLabelComponent,
        NzFormDirective,
        NzInputDirective,
        NzButtonComponent,
        ReactiveFormsModule,
        NzMessageModule,
    ],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})
export class LoginComponent {
    authService = inject(AuthService)
    router = inject(Router);
    message = inject(NzMessageService);

    form = new FormGroup({
        username: new FormControl(null, Validators.required),
        password: new FormControl(null, Validators.required)
    });

    constructor() {
    }

    onSubmit() {
        console.log(this.form.value)

        if (this.form.valid) {
            // @ts-ignore
            this.authService.login(this.form.value)
                .subscribe({
                        complete: () => {
                            console.log('Login successful')
                            console.log("access token: " + this.authService.accessToken)
                            console.log("refresh token: " + this.authService.refreshToken)
                            this.router.navigate(['']).then(res => {
                                if (!res) console.log("err with navigation")
                            })
                        },
                        error: (error: HttpErrorResponse) => {
                            console.error('Login failed', error);
                            this.message.error(`Login failed ${error.message}`);
                        }
                    }
                );
        }
    }
}
