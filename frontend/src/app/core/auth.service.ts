import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, EMPTY } from 'rxjs';
import { UserRole } from './models';

export interface AuthResponse {
  token: string | null;
  username: string;
  role: UserRole | string;
  clientId: number;
  clientName: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly tokenKey = 'valet_jwt_token';

  readonly loggedIn = signal(!!localStorage.getItem(this.tokenKey));
  readonly currentUser = signal<AuthResponse | null>(null);

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', { username, password }).pipe(
      tap(res => {
        if (res.token) {
          localStorage.setItem(this.tokenKey, res.token);
        }
        this.currentUser.set(res);
        this.loggedIn.set(true);
      })
    );
  }

  fetchCurrentUser(): Observable<AuthResponse> {
    if (!this.loggedIn()) {
      this.router.navigateByUrl('/login');
      return EMPTY;
    }
    return this.http.get<AuthResponse>('/api/auth/me').pipe(
      tap(res => this.currentUser.set(res))
    );
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.currentUser.set(null);
    this.loggedIn.set(false);
  }

  token(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
}