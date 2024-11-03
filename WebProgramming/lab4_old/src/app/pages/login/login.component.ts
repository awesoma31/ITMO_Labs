import {Component, inject} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {NzFormControlComponent, NzFormDirective, NzFormItemComponent, NzFormLabelComponent} from 'ng-zorro-antd/form';
import {NzInputDirective} from 'ng-zorro-antd/input';
import {NzButtonComponent} from 'ng-zorro-antd/button';
import {AuthService} from "../../auth/auth.service";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ScreenSizeService} from "../../utils/screen-size.service";

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
        ReactiveFormsModule
    ],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})
export class LoginComponent {
    authService = inject(AuthService)
    router = inject(Router);
    form = new FormGroup({
        username: new FormControl(null, Validators.required),
        password: new FormControl(null, Validators.required)
    })

    currentScreenSize: string = 'desktop';

    constructor(private screenSizeService: ScreenSizeService) {}

    ngOnInit() {
        this.screenSizeService.screenSize$.subscribe(size => {
            this.currentScreenSize = size;
        });
    }

    onSubmit() {
        console.log(this.form.value)

        //todo типизировать форму
        if (this.form.valid) {
            // @ts-ignore
            this.authService.login(this.form.value)
                .subscribe({
                        complete: () => {
                            console.log('Login successful')
                            this.router.navigate(['']).then(r => {if (!r) console.log("err with navigation")})
                        },
                    //todo show error message
                        error: (error) => console.error('Login failed', error)
                    }
                );
        }
    }

}
