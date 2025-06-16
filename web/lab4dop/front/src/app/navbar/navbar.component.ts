import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../auth/auth.service';
import {MatIcon} from '@angular/material/icon';
import {MatToolbar} from '@angular/material/toolbar';
import {MatIconButton} from '@angular/material/button';

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [
        MatIcon,
        MatToolbar,
        MatIconButton,
    ],
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
    router = inject(Router);
    authService = inject(AuthService)


    logout() {
        this.authService.logout();
    }
}
