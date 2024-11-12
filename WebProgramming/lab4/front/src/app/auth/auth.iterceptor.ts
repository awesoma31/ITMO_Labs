import {HttpHandlerFn, HttpInterceptorFn, HttpRequest} from "@angular/common/http";
import {inject} from "@angular/core";
import {AuthService} from "./auth.service";
import {BehaviorSubject, catchError, filter, switchMap, tap, throwError} from "rxjs";


let isRefreshing = new BehaviorSubject<boolean>(false)

export const authTokenInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
    const authService = inject(AuthService)
    const token = authService.accessToken;

    if (!token) return next(req)

    if (isRefreshing.value) {
        return refreshAndProceed(authService, req, next)
    }

    return next(addToken(req, token))
        .pipe(
            catchError(err => {
                if (err.status === 403) {
                    return refreshAndProceed(authService, req, next)
                }

                return throwError(() => err)
            })
        )
};


const refreshAndProceed = (
    authService: AuthService,
    req: HttpRequest<unknown>,
    next: HttpHandlerFn
) => {
    if (!isRefreshing.value) {
        isRefreshing.next(true)

        return authService.refreshAuthToken()
            .pipe(
                switchMap(token => {
                    return next(addToken(req, token.accessToken))
                        .pipe(
                            tap(() => isRefreshing.next(false))
                        )
                }),
                catchError(err => {
                    isRefreshing.next(false);
                    return throwError(() => err)
                })
            )
    }

    if (req.url.includes('refresh')) return next(addToken(req, authService.accessToken!))

    return isRefreshing.pipe(
        filter(isRefreshing => !isRefreshing),
        switchMap(res => {
            return next(addToken(req, authService.accessToken!))
        })
    )
};

const addToken = (req: HttpRequest<unknown>, accessToken: string) => {
    return req.clone({
        setHeaders: {
            Authorization: `Bearer ${accessToken}`
        }
    })
};
