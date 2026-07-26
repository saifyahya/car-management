import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { LanguageService } from '../../core/language.service';

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

  username = 'admin';
  password = 'admin123';
  readonly error = signal('');

  login(): void {
    if (!this.username || !this.password) {
      this.error.set(this.langService.t('errCreds'));
      return;
    }

    this.auth.login(this.username, this.password);
    this.router.navigateByUrl('/dashboard');
  }
}