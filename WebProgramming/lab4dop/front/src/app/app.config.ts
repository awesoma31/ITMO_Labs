import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideClientHydration} from '@angular/platform-browser';
import {en_US, provideNzI18n} from 'ng-zorro-antd/i18n';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {FormsModule} from '@angular/forms';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {provideHttpClient, withFetch, withInterceptors} from '@angular/common/http';
import {authTokenInterceptor} from "./auth/auth.iterceptor";
import {NzConfig, provideNzConfig} from 'ng-zorro-antd/core/config';

registerLocaleData(en);

const ngZorroConfig: NzConfig = {
    message: {nzMaxStack: 3, nzAnimate: true},
};

export const appConfig: ApplicationConfig = {
    providers: [
        provideZoneChangeDetection({eventCoalescing: true}),
        provideRouter(routes), provideClientHydration(),
        provideNzI18n(en_US), importProvidersFrom(FormsModule),
        provideAnimationsAsync(),
        provideHttpClient(
            withFetch(),
            withInterceptors([authTokenInterceptor])
        ),
        provideAnimationsAsync(),
        provideNzConfig(ngZorroConfig), provideAnimationsAsync(),
    ]
};


