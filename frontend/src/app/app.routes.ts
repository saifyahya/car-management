import { Routes } from '@angular/router';
import { adminGuard, authGuard, managerGuard, valetGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'v/:token',
    loadComponent: () => import('./features/public-ticket/public-ticket.component').then(m => m.PublicTicketComponent)
  },
  {
    path: 'me',
    canActivate: [authGuard],
    redirectTo: 'dashboard'
  },
  {
    path: '',
    loadComponent: () => import('./app-shell.component').then(m => m.AppShellComponent),
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        canActivate: [valetGuard],
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'check-in',
        canActivate: [valetGuard],
        loadComponent: () => import('./features/check-in/check-in.component').then(m => m.CheckInComponent)
      },
      {
        path: 'tickets/:id',
        canActivate: [valetGuard],
        loadComponent: () => import('./features/ticket-details/ticket-details.component').then(m => m.TicketDetailsComponent)
      },
      {
        path: 'reports',
        canActivate: [managerGuard],
        loadComponent: () => import('./features/reports/reports.component').then(m => m.ReportsComponent)
      },
      {
        path: 'users',
        canActivate: [managerGuard],
        loadComponent: () => import('./features/users/users.component').then(m => m.UsersComponent)
      },
      {
        path: 'clients',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/clients/clients.component').then(m => m.ClientsComponent)
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'reports'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'reports'
  }
];