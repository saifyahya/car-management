import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
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
  private readonly tokenKey = 'valet_jwt_token';

  readonly loggedIn = signal(!!sessionStorage.getItem(this.tokenKey));
  readonly currentUser = signal<AuthResponse | null>(null);

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', { username, password }).pipe(
      tap(res => {
        if (res.token) {
          sessionStorage.setItem(this.tokenKey, res.token);
        }
        this.currentUser.set(res);
        this.loggedIn.set(true);
      })
    );
  }

  fetchCurrentUser(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>('/api/auth/me').pipe(
      tap(res => this.currentUser.set(res))
    );
  }

  logout() {
    sessionStorage.removeItem(this.tokenKey);
    this.currentUser.set(null);
    this.loggedIn.set(false);
  }

  token(): string | null {
    return sessionStorage.getItem(this.tokenKey);
  }
}