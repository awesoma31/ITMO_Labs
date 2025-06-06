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
    submitType: 'login' | 'register' = 'login';

    form = new FormGroup({
        username: new FormControl(null, Validators.required),
        password: new FormControl(null, Validators.required)
    });

    constructor() {
    }

    onSubmit() {
        if (this.submitType === 'login') {
            this.login();
        } else if (this.submitType === 'register') {
            this.register();
        }
    }

    private login() {
        if (this.form.valid) {
            // @ts-ignore
            this.authService.login(this.form.value)
                .subscribe({
                    complete: () => {
                        this.router.navigate(['']).then(res => {
                            if (!res) console.log("err with navigation")
                        })
                    },
                    error: (error: HttpErrorResponse) => {
                        console.error('Login failed', error);
                        if (error.status === 403) {
                            this.message.error(`Invalid username or password`);
                        } else {
                            this.message.error(`Login failed ${error.message}`);
                        }
                    }
                });
        }
    }

    private register() {
        if (this.form.valid) {
            // @ts-ignore
            this.authService.register(this.form.value)
                .subscribe({
                    next: () => {
                        this.message.success('Registered successfully');
                    },
                    complete: () => {
                        this.message.success('Registered successfully');
                    },
                    error: (error: HttpErrorResponse) => {
                        if (error.status === 400) {
                            this.message.error(`Invalid username or password`);
                        } else if (error.status === 500) {
                            this.message.error(`Internal server error` + error.message);
                        } else if (error.status === 201 || error.status === 200) {
                            this.message.success(`User registered`);
                        } else {
                            this.message.error(`Register failed ${error.message}`);
                        }
                    }
                });
        }
    }

    toggleStatus(): void {
        this.submitType = this.submitType === 'login' ? 'register' : 'login';
    }
}
