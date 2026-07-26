import { Injectable, signal } from '@angular/core';
@Injectable({ providedIn: 'root' }) export class AuthService {
     private readonly credentialKey = 'valet_credentials';
    readonly loggedIn = signal(!!sessionStorage.getItem(this.credentialKey));
     login(username: string, password: string) {
         sessionStorage.setItem(this.credentialKey, btoa(`${username}:${password}`));
          this.loggedIn.set(true); 
        } 
        logout() { 
            sessionStorage.removeItem(this.credentialKey); this.loggedIn.set(false); } credentials() { return sessionStorage.getItem(this.credentialKey); } }