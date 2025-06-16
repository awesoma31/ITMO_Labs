import {Routes} from '@angular/router';
import {LoginComponent} from './pages/login/login.component';
import {canActivateAuth} from "./auth/access.guard";
import {MainComponent} from "./pages/main/main.component";

export const routes: Routes = [
    {
        path: '', component: MainComponent,
        canActivate: [canActivateAuth]
    },
    {path: 'login', component: LoginComponent}
];
