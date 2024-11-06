import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TokenResponse} from "./auth.interface";
import {catchError, tap, throwError} from "rxjs";
import {CookieService} from "ngx-cookie-service";
import {Router} from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    http = inject(HttpClient)
    cookieService = inject(CookieService)
    router = inject(Router)
    baseApiUrl = 'http://localhost:8080/auth/'

    accessToken: string | null = null;
    refreshToken: string | null = null;

    constructor() {
    }

    get isAuth() {
        if (!this.accessToken) {
            this.accessToken = this.cookieService.get("token")
        }

        return !!this.accessToken
    }

    login(payload: { username: string, password: string }) {
        return this.http.post<TokenResponse>(
            `${this.baseApiUrl}login`,
            payload
        ).pipe(
            tap(value => {
                this.accessToken = value.accessToken;
                this.refreshToken = value.refreshToken;

                this.cookieService.set("token", this.accessToken);
                this.cookieService.set("refreshToken", this.refreshToken);
            })
        )
    }

    refreshAuthToken() {
        return this.http.post<TokenResponse>(
            `${this.baseApiUrl}refresh`,
            {
                refreshToken: this.refreshToken
            }
        ).pipe(
            tap(value => {
                    this.saveTokens(value)
                }
            ),
            catchError(err => {
                this.logout()
                return throwError(err)
            })
        )
    }

    private saveTokens(value: TokenResponse) {
        this.accessToken = value.accessToken;
        this.refreshToken = value.refreshToken;

        this.cookieService.set("token", this.accessToken);
        this.cookieService.set("refreshToken", this.refreshToken);
    }

    logout() {
        this.cookieService.deleteAll()
        this.accessToken = null;
        this.refreshToken = null;
        this.router.navigate(['login']).then(r => console.log(`Navigation success: ${r}`));
    }
}
