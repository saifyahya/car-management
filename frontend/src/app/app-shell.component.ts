import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';
import { LanguageService } from './core/language.service';
import { UserRole } from './core/models';
import { ToastContainerComponent } from './shared/components/toast-container/toast-container.component';

@Component({
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ToastContainerComponent],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppShellComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly langService = inject(LanguageService);

  readonly currentUser = this.auth.currentUser;

  constructor() {
    if (!this.auth.loggedIn()) {
      this.router.navigateByUrl('/login');
      return;
    }

    if (!this.auth.currentUser()) {
      this.auth.fetchCurrentUser().subscribe({
        next: (user) => this.redirectBasedOnRole(user.role),
        error: () => this.logout()
      });
    } else {
      this.redirectBasedOnRole(this.auth.currentUser()?.role || '');
    }
  }

  username(): string {
    return this.auth.currentUser()?.username || '';
  }

  private redirectBasedOnRole(role: string) {
    const currentUrl = this.router.url;
    if (role === UserRole.ADMIN && (currentUrl === '/' || currentUrl.includes('/dashboard') || currentUrl.includes('/check-in'))) {
      this.router.navigateByUrl('/clients');
    } else if (role === UserRole.MANAGER && (currentUrl === '/' || currentUrl.includes('/clients') || currentUrl.includes('/dashboard') || currentUrl.includes('/check-in'))) {
      this.router.navigateByUrl('/reports');
    } else if (role === UserRole.VALET && (currentUrl === '/' || currentUrl.includes('/clients') || currentUrl.includes('/reports') || currentUrl.includes('/users'))) {
      this.router.navigateByUrl('/dashboard');
    }
  }

  isAdmin(): boolean {
    return this.auth.currentUser()?.role === UserRole.ADMIN;
  }

  isValet(): boolean {
    return this.auth.currentUser()?.role === UserRole.VALET;
  }

  isManager(): boolean {
    return this.auth.currentUser()?.role === UserRole.MANAGER;
  }

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}