import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { LanguageService } from '../../core/language.service';
import { UserRole } from '../../core/models';

@Component({
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly langService = inject(LanguageService);

  username = '';
  password = '';
  readonly error = signal('');
  readonly loading = signal(false);
  readonly showPassword = signal(false);

  toggleShowPassword(): void {
    this.showPassword.update(v => !v);
  }

  login(): void {
    if (!this.username || !this.password) {
      this.error.set(this.langService.t('errCreds'));
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.auth.login(this.username, this.password).subscribe({
      next: (res) => {
        if (res.role === UserRole.ADMIN) {
          this.router.navigateByUrl('/clients');
        } else if (res.role === UserRole.MANAGER) {
          this.router.navigateByUrl('/reports');
        } else {
          this.router.navigateByUrl('/dashboard');
        }
      },
      error: (err) => {
        this.loading.set(false);
        if (err?.status === 403 || err?.error?.message?.toLowerCase().includes('not active')) {
          this.error.set(this.langService.t('errAccountInactive'));
        } else {
          this.error.set(this.langService.t('errBadCreds'));
        }
      }
    });
  }
}