import { Routes } from '@angular/router';

import { ErrorComponent } from './error.component';

export const errorRoute: Routes = [
  {
    path: 'error',
    component: ErrorComponent,
    data: {
      pageTitle: 'Помилка!',
    },
  },
  {
    path: 'accessdenied',
    component: ErrorComponent,
    data: {
      pageTitle: 'Помилка!',
      errorMessage: 'Ви не авторизовані для доступу до цієї сторінки',
    },
  },
  {
    path: '404',
    component: ErrorComponent,
    data: {
      pageTitle: 'Помилка!',
      errorMessage: 'Сторінку не існує',
    },
  },
  {
    path: '**',
    redirectTo: '/404',
  },
];
