import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { UserRole } from '../models';
import { map } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.loggedIn()) {
    return router.createUrlTree(['/login']);
  }
  return true;
};

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.loggedIn()) {
    return router.createUrlTree(['/login']);
  }

  const currentUser = auth.currentUser();
  if (currentUser) {
    return currentUser.role === UserRole.ADMIN ? true : router.createUrlTree(['/reports']);
  }

  return auth.fetchCurrentUser().pipe(
    map(user => user.role === UserRole.ADMIN ? true : router.createUrlTree(['/reports']))
  );
};

export const managerGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.loggedIn()) {
    return router.createUrlTree(['/login']);
  }

  const currentUser = auth.currentUser();
  if (currentUser) {
    return (currentUser.role === UserRole.MANAGER || currentUser.role === UserRole.ADMIN) ? true : router.createUrlTree(['/dashboard']);
  }

  return auth.fetchCurrentUser().pipe(
    map(user => (user.role === UserRole.MANAGER || user.role === UserRole.ADMIN) ? true : router.createUrlTree(['/dashboard']))
  );
};

export const valetGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.loggedIn()) {
    return router.createUrlTree(['/login']);
  }

  const currentUser = auth.currentUser();
  if (currentUser) {
    return currentUser.role === UserRole.VALET ? true : router.createUrlTree(['/reports']);
  }

  return auth.fetchCurrentUser().pipe(
    map(user => user.role === UserRole.VALET ? true : router.createUrlTree(['/reports']))
  );
};
